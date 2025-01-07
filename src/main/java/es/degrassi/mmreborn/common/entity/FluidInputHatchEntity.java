package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@MethodsReturnNonnullByDefault
public class FluidInputHatchEntity extends FluidTankEntity {

  public FluidInputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.FLUID_INPUT_HATCH.get(), pos, state, FluidHatchSize.TINY, IOType.INPUT);
  }

  public FluidInputHatchEntity(BlockPos pos, BlockState state, FluidHatchSize size) {
    super(EntityRegistration.FLUID_INPUT_HATCH.get(), pos, state, size, IOType.INPUT);
  }
}
