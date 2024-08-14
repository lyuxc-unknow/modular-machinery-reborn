package es.degrassi.mmreborn.common.util;

import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.entity.base.TileInventory;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class RedstoneHelper {
  public static int getRedstoneLevel(BlockEntity sync) {
    return switch (sync) {
      case TileInventory entity -> ItemHandlerHelper.calcRedstoneFromInventory(entity.getInventory());
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
      default -> 0;
    };
  }
}
