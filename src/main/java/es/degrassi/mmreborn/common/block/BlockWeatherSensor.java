package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.common.entity.DimensionalDetectorEntity;
import es.degrassi.mmreborn.common.entity.WeatherSensorEntity;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BlockWeatherSensor extends BlockMachineComponent {
  public BlockWeatherSensor() {
    super(
      Properties.of()
        .strength(2F, 10F)
        .sound(SoundType.METAL)
        .requiresCorrectToolForDrops()
        .dynamicShape()
        .noOcclusion()
    );
  }

  @Override
  protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
    List<ItemStack> drops = super.getDrops(state, builder);
    drops.add(ItemRegistration.WEATHER_SENSOR.get().getDefaultInstance());
    return drops;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new WeatherSensorEntity(pos, state);
  }

}
