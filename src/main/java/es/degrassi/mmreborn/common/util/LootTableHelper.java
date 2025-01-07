package es.degrassi.mmreborn.common.util;

import es.degrassi.mmreborn.api.TagUtil;
import es.degrassi.mmreborn.common.registration.Registration;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LootTableHelper {

  private static final List<ResourceLocation> tables = new ArrayList<>();
  private static Map<ResourceLocation, List<LootData>> lootsMap = new HashMap<>();

  public static void addTable(ResourceLocation table) {
    if(!tables.contains(table))
      tables.add(table);
  }

  public static void generate(MinecraftServer server) {
    lootsMap.clear();
    LootParams params =
        new LootParams.Builder(server.overworld()).create(Registration.MODULAR_MACHINERY_LOOT_PARAMETER_SET);
    LootContext context = new LootContext.Builder(params).create(Optional.empty());
    for (ResourceLocation table : tables) {
      List<LootData> loots = getLoots(table, server, context);
      lootsMap.put(table, loots);
    }
  }

  private static List<LootData> getLoots(ResourceLocation table, MinecraftServer server, LootContext context) {
    List<LootData> loots = new ArrayList<>();
    LootTable lootTable = server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, table));
    BiFunction<ItemStack, LootContext, ItemStack> globalFunction = lootTable.compositeFunction;
    List<LootPool> pools = getPoolsFromLootTable(lootTable);

    if(pools == null)
      return Collections.emptyList();

    for(LootPool pool : pools) {
      List<LootPoolEntryContainer> entries = pool.entries;
      float total = entries.stream().filter(entry -> entry instanceof LootPoolSingletonContainer).mapToInt(entry -> ((LootPoolSingletonContainer)entry).weight).sum();
      String rolls = getBaseRolls(pool.getRolls(), context);
      String bonusRolls = getBonusRolls(pool.getBonusRolls(), context);
      entries.stream().filter(entry -> entry instanceof LootItem)
          .map(entry -> (LootItem)entry)
          .forEach(entry -> {
            Consumer<ItemStack> consumer = stack -> loots.add(new LootData(stack, entry.weight / total, rolls, bonusRolls));
            consumer = applyFunctions(consumer, entry.functions, globalFunction, context);
            entry.createItemStack(consumer, context);
          });

      entries.stream().filter(entry -> entry instanceof TagEntry)
          .map(entry -> (TagEntry)entry)
          .forEach(entry -> {
            Consumer<ItemStack> consumer = stack -> loots.add(new LootData(stack, entry.weight / total / (entry.expand ? TagUtil.getItems(entry.tag).count() : 1), rolls, bonusRolls));
            consumer = applyFunctions(consumer, entry.functions, globalFunction, context);
            entry.createItemStack(consumer, context);
          });

      entries.stream().filter(entry -> entry instanceof NestedLootTable)
          .map(entry -> (NestedLootTable)entry)
          .map(entry -> getLoots(entry.contents.map(ResourceKey::location, LootTable::getLootTableId), server, context))
          .forEach(loots::addAll);
    }
    return loots;
  }

  private static Consumer<ItemStack> applyFunctions(Consumer<ItemStack> consumer, List<LootItemFunction> functions, BiFunction<ItemStack, LootContext, ItemStack> globalFunction, LootContext context) {
    for(LootItemFunction function : functions)
      consumer = LootItemFunction.decorate(function, consumer, context);
    return LootItemFunction.decorate(globalFunction, consumer, context);
  }

  private static String getBaseRolls(@Nullable NumberProvider rolls, LootContext context) {
    return switch (rolls) {
      case ConstantValue value -> Math.round(value.value()) + " Rolls";
      case UniformGenerator uniform -> "[" + uniform.min().getInt(context) + "," + uniform.max().getInt(context) + "] Rolls (uniform)";
      case BinomialDistributionGenerator binomial -> "[0," + binomial.n().getInt(context) + "] Rolls (binomial)";
      case null, default -> "";
    };
  }

  private static String getBonusRolls(@Nullable NumberProvider rolls, LootContext context) {
    return switch (rolls) {
      case ConstantValue value -> value.value() != 0.0f ? Math.round(value.value() * context.getLuck()) + " Rolls" : "";
      case UniformGenerator uniform -> "[" + uniform.min().getInt(context) * context.getLuck() + "," + uniform.max().getInt(context) * context.getLuck() + "] Rolls (uniform)";
      case BinomialDistributionGenerator binomial -> "[0," + binomial.n().getInt(context) * context.getLuck() + "] Rolls (binomial)";
      case null, default -> "";
    };
  }

  public static Map<ResourceLocation, List<LootData>> getLoots() {
    return lootsMap;
  }

  public static void receiveLoots(Map<ResourceLocation, List<LootData>> newLoots) {
    lootsMap = newLoots;
  }

  public static List<LootData> getLootsForTable(ResourceLocation table) {
    return lootsMap.getOrDefault(table, Collections.emptyList());
  }

  @SuppressWarnings("unchecked")
  @Nullable
  public static List<LootPool> getPoolsFromLootTable(LootTable table) {
    for(Field field : LootTable.class.getDeclaredFields()) {
      if(field.getName().equals("e") || field.getName().equals("f_79109_") || field.getName().equals("pools")) {
        field.setAccessible(true);
        try {
          return (List<LootPool>) field.get(table);
        } catch (IllegalAccessException ignored) {

        }
      }
    }
    throw new RuntimeException("NOPE");
  }

  public record LootData(ItemStack stack, double chance, String rolls, String bonusRolls) {
    public static final StreamCodec<RegistryFriendlyByteBuf, LootData> STREAM_CODEC = StreamCodec.composite(
        ItemStack.STREAM_CODEC,
        LootData::stack,
        ByteBufCodecs.DOUBLE,
        LootData::chance,
        ByteBufCodecs.STRING_UTF8,
        LootData::rolls,
        ByteBufCodecs.STRING_UTF8,
        LootData::bonusRolls,
        LootData::new
    );
  }
}
