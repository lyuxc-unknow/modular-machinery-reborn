package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.HybridTank;
import es.degrassi.mmreborn.common.util.IEnergyHandler;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FluidInputHatchEntity extends FluidTankEntity implements MachineComponentEntity {

  public FluidInputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.FLUID_INPUT_HATCH.get(), pos, state);
  }

  public FluidInputHatchEntity(BlockPos pos, BlockState state, FluidHatchSize size) {
    super(EntityRegistration.FLUID_INPUT_HATCH.get(), pos, state, size, IOType.INPUT);
  }

  @Nullable
  @Override
  public MachineComponent provideComponent() {
    return new MachineComponent.FluidHatch(IOType.INPUT) {
      @Override
      public HybridTank getContainerProvider() {
        return getTank();
      }
    };
  }
}
