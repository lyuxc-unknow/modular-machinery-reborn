package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.machine.component.FluidHatch;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.HybridTank;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FluidOutputHatchEntity extends FluidTankEntity implements MachineComponentEntity {

  public FluidOutputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.FLUID_OUTPUT_HATCH.get(), pos, state);
  }

  public FluidOutputHatchEntity(BlockPos pos, BlockState state, FluidHatchSize size) {
    super(EntityRegistration.FLUID_OUTPUT_HATCH.get(), pos, state, size, IOType.OUTPUT);
  }
}
