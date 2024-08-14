package es.degrassi.mmreborn.common.entity.base;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class BlockEntitySynchronized extends BlockEntity {
  private static final String REQUEST_UPDATE_KEY = "requestModelUpdate";
  private boolean requestModelUpdate = false;

  public BlockEntitySynchronized(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
    super(type, pos, blockState);
  }

  public void tick() {
  }

  public void readCustomNBT(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    requestModelUpdate = nbt.contains(REQUEST_UPDATE_KEY) && nbt.getBoolean(REQUEST_UPDATE_KEY);
  }

  public void writeCustomNBT(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    nbt.putBoolean(REQUEST_UPDATE_KEY, requestModelUpdate);
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadAdditional(nbt, pRegistries);
    readCustomNBT(nbt, pRegistries);
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
    writeCustomNBT(nbt, pRegistries);
  }

  @Override
  public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
    CompoundTag nbt = super.getUpdateTag(pRegistries);
    saveAdditional(nbt, pRegistries);
    return nbt;
  }

  @Override
  public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
    loadAdditional(tag, lookupProvider);
  }

  @Override
  public final ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  public void markForUpdate() {
    if (requestModelUpdate) requestModelDataUpdate();
    requestModelUpdate = false;
//    le.notifyBlockUpdate(pos, thisState, thisState, 3);
    getLevel().setBlockAndUpdate(getBlockPos(), getBlockState());
    setChanged();
  }
}
