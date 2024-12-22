package es.degrassi.mmreborn.common.integration.theoneprobe;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.helper.CraftingStatus;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.integration.theoneprobe.element.CustomProgress;
import es.degrassi.mmreborn.common.util.Utils;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class TOPInfoProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {
  @Override
  public Void apply(ITheOneProbe probe) {
    probe.registerProvider(this);
    probe.registerElementFactory(new CustomProgress.CustomProgressFactory());
    return null;
  }

  @Override
  public ResourceLocation getID() {
    return ModularMachineryReborn.rl("mmr_info_provider");
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData data) {
    BlockEntity tile = level.getBlockEntity(data.getPos());
    if (tile instanceof MachineControllerEntity controller) {
      if (controller.isPaused()) {
        info.mcText(Component.translatable("gui.controller.status.paused").withStyle(ChatFormatting.DARK_RED));
        return;
      }
      showCraftingInfo(controller, info);
    }
  }

  private void showCraftingInfo(MachineControllerEntity tile, IProbeInfo info) {
    CraftingStatus status = tile.getCraftingStatus();
    MutableComponent message = Component.translatable(status.getUnlocMessage());
    switch (status.getStatus()) {
      case CRAFTING -> message.withStyle(ChatFormatting.GREEN);
      case NO_RECIPE -> message.withStyle(ChatFormatting.GOLD);
      case MISSING_STRUCTURE, FAILURE -> message.withStyle(ChatFormatting.RED);
    }
    info.mcText(message);
    if (tile.hasActiveRecipe()) {
      int ticks = tile.getCraftingManager().getTicks();
      int total = tile.getCraftingManager().getRecipeTicks();
      float progress = (float) ticks / total;
      boolean seconds = total >= 20;
      info.element(
          new CustomProgress(
              ticks,
              total,
              info.defaultProgressStyle()
                  .suffix(Component
                      .literal(
                          "/"
                              + (seconds ? Utils.decimalFormat(total / 20d) : Utils.decimalFormat(total))
                              + (seconds ? "s" : "")
                              + " ("
                              + Utils.decimalFormatWithPercentage(progress * 100)
                              + ")"
                      )
                  )
          )
      );
    }
  }
}
