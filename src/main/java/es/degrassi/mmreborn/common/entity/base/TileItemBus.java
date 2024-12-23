package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.entity.FluidInputHatchEntity;
import es.degrassi.mmreborn.common.entity.ItemInputBusEntity;
import es.degrassi.mmreborn.common.machine.IOType;
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
  private IOType ioType;

  public TileItemBus(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState, ItemBusSize size, IOType ioType) {
    super(entityType, pos, blockState, size.getSlotCount());
    this.size = size;
    this.ioType = ioType;
  }

  @Nullable
  @Override
  public ItemBus provideComponent() {
    return new ItemBus(ioType) {
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
    this.ioType = IOType.getByString(compound.getString("ioType"));
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);

    compound.putString("busSize", this.size.getSerializedName());
    if (ioType == null) {
      ioType = this instanceof ItemInputBusEntity ? IOType.INPUT : IOType.OUTPUT;
    }
    compound.putString("ioType", this.ioType.getSerializedName());
  }
}
