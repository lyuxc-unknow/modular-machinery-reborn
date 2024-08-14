package es.degrassi.mmreborn.common.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityRestrictedTick extends ColorableMachineComponentEntity {

  private long lastUpdateWorldTick = -1;
  public int ticksExisted = 0;

  public BlockEntityRestrictedTick(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState) {
    super(entityType, pos, blockState);
  }

  public final void tick() {
    if (getLevel() == null || getLevel().isClientSide()) return;
    long currentTick = getLevel().getGameTime();
    if (lastUpdateWorldTick == currentTick) {
      return;
    }
    lastUpdateWorldTick = currentTick;
    doRestrictedTick();
    ticksExisted++;
  }

  public abstract void doRestrictedTick();
}
