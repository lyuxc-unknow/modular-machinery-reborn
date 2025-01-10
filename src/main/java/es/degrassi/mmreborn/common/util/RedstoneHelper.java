package es.degrassi.mmreborn.common.util;

import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.entity.base.TileInventory;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

public class RedstoneHelper {
  public static int getRedstoneLevel(@Nullable BlockEntity sync) {
    if (sync == null) return 0;
    return switch (sync) {
      case MachineControllerEntity entity -> {
        if (entity.getStatus().isCrafting()) yield 15;
        if (!entity.getStatus().isMissingStructure()) yield 1;
        yield 0;
      }
      case TileInventory entity -> entity.getInventory().calcRedstoneFromInventory();
      case FluidTankEntity ft -> {
        FluidTank tank = ft.getTank();
        float cap = tank.getCapacity();
        float cur = tank.getFluidAmount();
        yield Mth.clamp(Math.round(15F * (cur / cap)), 0, 15);
      }
      case EnergyHatchEntity entity -> {
        float cap = entity.getMaxEnergy();
        float cur = entity.getCurrentEnergy();
        yield Mth.clamp(Math.round(15F * (cur / cap)), 0, 15);
      }
      case ExperienceHatchEntity entity -> {
        float cap = entity.getTank().getExperienceCapacity();
        float cur = entity.getTank().getExperience();
        yield Mth.clamp(Math.round(15F * (cur / cap)), 0, 15);
      }
      default -> 0;
    };
  }

  public static int getReceivingRedstone(@Nullable BlockEntity sync) {
    if (sync == null || sync.getLevel() == null) return 0;
    return switch (sync) {
      case MachineControllerEntity entity -> entity.getLevel().getBestNeighborSignal(entity.getBlockPos());
      case TileInventory entity -> entity.getLevel().getBestNeighborSignal(entity.getBlockPos());
      case FluidTankEntity entity -> entity.getLevel().getBestNeighborSignal(entity.getBlockPos());
      case EnergyHatchEntity entity -> entity.getLevel().getBestNeighborSignal(entity.getBlockPos());
      case ExperienceHatchEntity entity -> entity.getLevel().getBestNeighborSignal(entity.getBlockPos());
      default -> 0;
    };
  }
}
