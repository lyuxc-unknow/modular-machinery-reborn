package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ExperienceInputHatchEntity extends ExperienceHatchEntity {

  public ExperienceInputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.EXPERIENCE_INPUT_HATCH.get(), pos, state, ExperienceHatchSize.TINY, IOType.INPUT);
  }

  public ExperienceInputHatchEntity(BlockPos pos, BlockState state, ExperienceHatchSize size) {
    super(EntityRegistration.EXPERIENCE_INPUT_HATCH.get(), pos, state, size, IOType.INPUT);
  }
}
