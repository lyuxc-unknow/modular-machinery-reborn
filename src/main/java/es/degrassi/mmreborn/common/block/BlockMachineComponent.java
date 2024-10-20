package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.entity.base.BlockEntitySynchronized;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockMachineComponent extends Block implements BlockDynamicColor, EntityBlock {
  public BlockMachineComponent(Properties properties) {
    super(properties.requiresCorrectToolForDrops());
  }

  @Override
  public int getColorMultiplier(BlockState state, @Nullable BlockAndTintGetter worldIn, @Nullable BlockPos pos, int tintIndex) {
    if(worldIn == null || pos == null) {
      return Config.machineColor;
    }
    BlockEntity te = worldIn.getBlockEntity(pos);
    if (te instanceof ColorableMachineEntity) {
      return ((ColorableMachineEntity) te).getMachineColor();
    }
    return Config.machineColor;
  }

  @Override
  @SuppressWarnings("deprecation")
  protected boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam) {
    super.triggerEvent(pState, pLevel, pPos, pId, pParam);
    final BlockEntity be = pLevel.getBlockEntity(pPos);
    return be != null && be.triggerEvent(pId, pParam);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
    return new ColorableMachineComponentEntity(blockPos, blockState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
    return (level, pos, state, blockEntity) -> {
      if (blockEntity.getType() == pBlockEntityType && blockEntity instanceof BlockEntitySynchronized entity) {
        entity.tick();
      }
    };
  }
}
