package es.degrassi.mmreborn.common.entity;

import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ExperienceHatch;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ExperienceOutputHatchEntity extends ExperienceHatchEntity {

  public ExperienceOutputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.EXPERIENCE_OUTPUT_HATCH.get(), pos, state);
  }

  public ExperienceOutputHatchEntity(BlockPos pos, BlockState state, ExperienceHatchSize size) {
    super(EntityRegistration.EXPERIENCE_OUTPUT_HATCH.get(), pos, state, size, IOType.OUTPUT);
  }

  @Nullable
  @Override
  public ExperienceHatch provideComponent() {
    return new ExperienceHatch(IOType.OUTPUT) {
      @Override
      public @NotNull IExperienceHandler getContainerProvider() {
        return getTank();
      }
    };
  }

}
