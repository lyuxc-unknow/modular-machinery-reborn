package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementWeather;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WeatherSensorEntity extends ColorableMachineComponentEntity implements MachineComponentEntity {
  public WeatherSensorEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.WEATHER_SENSOR.get(), pos, blockState);
  }

  @Override
  public @Nullable MachineComponent provideComponent() {
    return new MachineComponent<>(IOType.INPUT) {
      @Override
      public ComponentType getComponentType() {
        return ComponentRegistration.COMPONENT_WEATHER.get();
      }

      @Override
      public RequirementWeather.WeatherType getContainerProvider() {
        return null;
      }
    };
  }
}
