package es.degrassi.mmreborn.common.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityRestrictedTick extends ColorableMachineComponentEntity {
  public int ticksExisted = 0;

  public BlockEntityRestrictedTick(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState) {
    super(entityType, pos, blockState);
  }

  public final void tick() {
    if (getLevel() == null || getLevel().isClientSide()) return;
    doRestrictedTick();
    ticksExisted++;
  }

  public abstract void doRestrictedTick();
}
