package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.common.util.IOInventory;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Getter
public abstract class TileInventory extends ColorableMachineComponentEntity {

  protected IOInventory inventory;

  public TileInventory(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState) {
    super(entityType, pos, blockState);
  }

  public TileInventory(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState, int size) {
    super(entityType, pos, blockState);
    this.inventory = buildInventory(this, size);
  }

  public abstract IOInventory buildInventory(TileInventory tile, int size);

  @Override
  public void readCustomNBT(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.readCustomNBT(compound, pRegistries);

    this.inventory = IOInventory.deserialize(this, compound.getCompound("inventory"), pRegistries);
  }

  @Override
  public void writeCustomNBT(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.writeCustomNBT(compound, pRegistries);

    compound.put("inventory", this.inventory.writeNBT(pRegistries));
  }
}
