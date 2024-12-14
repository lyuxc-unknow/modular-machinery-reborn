package es.degrassi.mmreborn.common.entity;

import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.EnergyHatch;
import es.degrassi.mmreborn.common.machine.component.ExperienceHatch;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.IEnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ExperienceInputHatchEntity extends ExperienceHatchEntity {

  public ExperienceInputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.EXPERIENCE_INPUT_HATCH.get(), pos, state);
  }

  public ExperienceInputHatchEntity(BlockPos pos, BlockState state, ExperienceHatchSize size) {
    super(EntityRegistration.EXPERIENCE_INPUT_HATCH.get(), pos, state, size, IOType.INPUT);
  }

  @Nullable
  @Override
  public ExperienceHatch provideComponent() {
    return new ExperienceHatch(IOType.INPUT) {
      @Override
      public @NotNull IExperienceHandler getContainerProvider() {
        return getTank();
      }
    };
  }
}
