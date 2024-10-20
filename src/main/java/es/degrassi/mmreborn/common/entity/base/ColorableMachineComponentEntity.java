package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.network.server.SUpdateMachineColorPacket;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class ColorableMachineComponentEntity extends BlockEntitySynchronized implements ColorableMachineEntity {
  private int definedColor = Config.machineColor;

  public ColorableMachineComponentEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.COLORABLE_MACHINE.get(), pos, blockState);
  }

  public ColorableMachineComponentEntity(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState) {
    super(entityType, pos, blockState);
  }

  @Override
  public int getMachineColor() {
    return definedColor;
  }

  @Override
  public void setMachineColor(int newColor) {
    setChanged();
    this.definedColor = newColor;
    setRequestModelUpdate(true);
    triggerEvent(1, 0);
//    getLevel().updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
//    getLevel().setBlockAndUpdate(getBlockPos(), getBlockState());
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()),
        new SUpdateMachineColorPacket(newColor, getBlockPos()));
    }
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadAdditional(nbt, pRegistries);
    if (nbt.contains("casingColor")) {
      definedColor = nbt.getInt("casingColor");
      return;
    }
    definedColor = Config.machineColor;
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
    nbt.putInt("casingColor", this.definedColor);
  }

  @Override
  public boolean triggerEvent(int id, int type) {
    if (id == 1) {
      if (getLevel() != null && getLevel().isClientSide())
        scheduleRenderUpdate();
      return true;
    }
    return false;
  }

  public void scheduleRenderUpdate() {
    if (getLevel() != null) {
      if (getLevel().isClientSide()) {
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1 << 3);
      } else {
        getLevel().blockEvent(getBlockPos(), getBlockState().getBlock(), 1, 0);
      }
    }
  }
}
