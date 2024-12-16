package es.degrassi.mmreborn.common.util;

import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.TileInventory;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class RedstoneHelper {
  public static int getRedstoneLevel(@Nullable BlockEntity sync) {
    if (sync == null) return 0;
    return switch (sync) {
      case MachineControllerEntity entity -> {
        Level level = entity.getLevel();
        AtomicInteger redstone = new AtomicInteger(0);
        AtomicInteger counter = new AtomicInteger(0);
        entity.getFoundComponentsMap().keySet().forEach(pos -> {
          BlockEntity be = level.getBlockEntity(pos);
          if (be instanceof MachineComponentEntity) {
            redstone.getAndAdd(getRedstoneLevel(be));
            counter.getAndIncrement();
          }
        });
        yield redstone.get() / counter.get();
      }
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
      case ExperienceHatchEntity entity -> {
        float cap = entity.getTank().getExperienceCapacity();
        float cur = entity.getTank().getExperience();
        yield Mth.clamp(Math.round(15F * (cur / cap)), 0, 15);
      }
      default -> 0;
    };
  }
}
