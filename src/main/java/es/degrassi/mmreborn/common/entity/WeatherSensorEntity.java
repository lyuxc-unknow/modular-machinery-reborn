package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.component.WeatherComponent;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

@MethodsReturnNonnullByDefault
public class WeatherSensorEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<WeatherComponent> {
  public WeatherSensorEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.WEATHER_SENSOR.get(), pos, blockState);
  }

  @Override
  public WeatherComponent provideComponent() {
    return new WeatherComponent();
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadAdditional(nbt, pRegistries);
  }
}
