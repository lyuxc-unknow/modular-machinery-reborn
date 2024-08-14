package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.common.data.Config;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockDynamicColor {
  default int getColorMultiplier(BlockState state, @Nullable BlockAndTintGetter worldIn, @Nullable BlockPos pos, int tintIndex) {
    return Config.machineColor;
  }
}
