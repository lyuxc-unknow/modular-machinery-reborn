package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.EnergyHatch;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.IEnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class EnergyInputHatchEntity extends EnergyHatchEntity {

  public EnergyInputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.ENERGY_INPUT_HATCH.get(), pos, state);
    this.size = null;
  }

  public EnergyInputHatchEntity(BlockPos pos, BlockState state, EnergyHatchSize size) {
    super(EntityRegistration.ENERGY_INPUT_HATCH.get(), pos, state, size, IOType.INPUT);
  }
}
