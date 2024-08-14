package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.TileInventory;
import es.degrassi.mmreborn.common.entity.base.TileItemBus;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ItemInputBusEntity extends TileItemBus implements MachineComponentEntity {

  public ItemInputBusEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.ITEM_INPUT_BUS.get(), pos, state);
  }

  public ItemInputBusEntity(BlockPos pos, BlockState state, ItemBusSize type) {
    super(EntityRegistration.ITEM_INPUT_BUS.get(), pos, state, type);
  }

  @Override
  public IOInventory buildInventory(TileInventory tile, int size) {
    int[] slots = new int[size];
    for (int i = 0; i < size; i++) {
      slots[i] = i;
    }
    return new IOInventory(tile, slots, new int[] {});
  }

  @Nullable
  @Override
  public MachineComponent provideComponent() {
    return new MachineComponent.ItemBus(IOType.INPUT) {
      @Override
      public IOInventory getContainerProvider() {
        return ItemInputBusEntity.this.inventory;
      }
    };
  }

}
