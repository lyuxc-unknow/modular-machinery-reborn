package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.entity.ItemInputBusEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.machine.component.ItemBus;
import es.degrassi.mmreborn.common.util.IOInventory;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

@Getter
public abstract class TileItemBus extends TileInventory implements MachineComponentEntity {

  private ItemBusSize size;

  public TileItemBus(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState) {
    super(entityType, pos, blockState);
  }

  public TileItemBus(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState, ItemBusSize size) {
    super(entityType, pos, blockState, size.getSlotCount());
    this.size = size;
  }

  @Nullable
  @Override
  public ItemBus provideComponent() {
    return new ItemBus(IOType.INPUT) {
      @Override
      public IOInventory getContainerProvider() {
        return inventory;
      }
    };
  }

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.loadAdditional(compound, pRegistries);

    this.size = ItemBusSize.value(compound.getString("busSize"));
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);

    compound.putString("busSize", this.size.getSerializedName());
  }
}
