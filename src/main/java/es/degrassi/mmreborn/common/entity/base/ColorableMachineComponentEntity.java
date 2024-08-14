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
    this.definedColor = newColor;
    setRequestModelUpdate(true);
    this.markForUpdate();
    if (getLevel() instanceof ServerLevel l) {
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()), new SUpdateMachineColorPacket(newColor, getBlockPos()));
    }
  }

  @Override
  public void readCustomNBT(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.readCustomNBT(nbt, pRegistries);
    if (nbt.contains("casingColor")) {
      definedColor = nbt.getInt("casingColor");
      return;
    }
    definedColor = Config.machineColor;
  }

  @Override
  public void writeCustomNBT(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.writeCustomNBT(nbt, pRegistries);
    nbt.putInt("casingColor", this.definedColor);
  }
}
