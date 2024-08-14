package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Getter
public abstract class TileItemBus extends TileInventory {

  private ItemBusSize size;

  public TileItemBus(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState) {
    super(entityType, pos, blockState);
  }

  public TileItemBus(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState, ItemBusSize size) {
    super(entityType, pos, blockState, size.getSlotCount());
    this.size = size;
  }

  @Override
  public void readCustomNBT(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.readCustomNBT(compound, pRegistries);

    this.size = ItemBusSize.value(compound.getString("busSize"));
  }

  @Override
  public void writeCustomNBT(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.writeCustomNBT(compound, pRegistries);

    compound.putString("busSize", this.size.getSerializedName());
  }
}
