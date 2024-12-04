package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Registration {


  public static final LootContextParamSet MODULAR_MACHINERY_LOOT_PARAMETER_SET = LootContextParamSets.register(
      "modular_machinery_reborn", builder ->
      builder.optional(LootContextParams.ORIGIN).optional(LootContextParams.BLOCK_ENTITY)
  );

  public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ModularMachineryReborn.MODID);

  public static final Supplier<DataComponentType<ResourceLocation>> MACHINE_DATA = DATA_COMPONENTS.register("machine", () -> DataComponentType.<ResourceLocation>builder()
    .persistent(ResourceLocation.CODEC)
    .networkSynchronized(ResourceLocation.STREAM_CODEC)
    .build()
  );

  public static final Supplier<DataComponentType<List<BlockPos>>> STRUCTURE_CREATOR_DATA = DATA_COMPONENTS.register("structure_creator", () -> DataComponentType.<List<BlockPos>>builder()
      .persistent(BlockPos.CODEC.listOf())
      .networkSynchronized(BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()))
      .build()
  );

  public static void register(final IEventBus bus) {
    DATA_COMPONENTS.register(bus);
    ComponentRegistration.register(bus);
    RequirementTypeRegistration.register(bus);
    BlockRegistration.register(bus);
    ItemRegistration.register(bus);
    EntityRegistration.register(bus);
    ContainerRegistration.register(bus);
    RecipeRegistration.register(bus);
    CreativeTabsRegistration.register(bus);
  }
}
