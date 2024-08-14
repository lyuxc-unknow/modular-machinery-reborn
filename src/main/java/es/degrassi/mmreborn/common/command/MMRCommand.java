package es.degrassi.mmreborn.common.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.util.EnergyDisplayUtil;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.MachineJsonReloadListener;
import es.degrassi.mmreborn.common.network.server.SOpenFilePacket;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.util.MMRLogger;
import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class MMRCommand {
  public static LiteralArgumentBuilder<CommandSourceStack> register(String name) {
    return Commands.literal(name)
      .then(logging())
      .then(reload());
  }

  public static ArgumentBuilder<CommandSourceStack, ?> logging() {
    return Commands.literal("log")
      .requires(cs -> cs.hasPermission(2))
      .executes(ctx -> {
        if (ctx.getSource().getEntity() instanceof ServerPlayer player)
          PacketDistributor.sendToPlayer(player, new SOpenFilePacket(new File("logs/modular_machinery_reborn/mmr.log").toURI().toString()));
        return 1;
      });
  }

  public static ArgumentBuilder<CommandSourceStack, ?> reload() {
    return Commands.literal("reload")
      .requires(cs -> cs.hasPermission(2))
      .executes(ctx -> {
        Config.load();
        EnergyHatchSize.loadFromConfig();
        FluidHatchSize.loadFromConfig();
        EnergyDisplayUtil.loadFromConfig();
        if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
          reloadMachines(player.server, player);
//          reloadRecipes(player.server, player);
        }
        return 1;
      });
  }

  public static void reloadMachines(MinecraftServer server, @Nullable ServerPlayer player) {
    new MachineJsonReloadListener().reload(CompletableFuture::completedFuture, server.getResourceManager(), InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, server, server)
      .thenRun(() -> {
        if (player != null)
          player.sendSystemMessage(Component.translatable(ModularMachineryReborn.MODID + ".command.reload.machines").withStyle(ChatFormatting.GRAY));
      });
  }

//  public static void reloadRecipes(MinecraftServer server, ServerPlayer player) {
//    MachineRecipe.RECIPES.clear();
//    MMRLogger.INSTANCE.info(
//      "All recipes: {}",
//      server
//        .getRecipeManager()
//        .getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get())
//        .stream()
//        .map(RecipeHolder::value)
//        .map(MachineRecipe::asJson)
//        .toList()
//    );
//    server.getRecipeManager().getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get()).forEach(holder -> {
//      MachineRecipe recipe = holder.value();
//      DynamicMachine machine = recipe.getOwningMachine();
//      if (machine == null) return;
//      MachineRecipe.RECIPES.computeIfAbsent(machine, list -> new LinkedList<>()).add(recipe);
//    });
//    MMRLogger.INSTANCE.info("reloaded recipes -> {}", MachineRecipe.RECIPES.values());
//    if (player != null && !MachineRecipe.RECIPES.isEmpty())
//      player.sendSystemMessage(Component.translatable(ModularMachineryReborn.MODID + ".command.reload.recipes").withStyle(ChatFormatting.GRAY));
//  }
}
