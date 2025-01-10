package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.entity.base.TileInventory;
import es.degrassi.mmreborn.common.entity.base.TileItemBus;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemInputBusEntity extends TileItemBus {

  public ItemInputBusEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.ITEM_INPUT_BUS.get(), pos, state, ItemBusSize.TINY, IOType.INPUT);
  }

  public ItemInputBusEntity(BlockPos pos, BlockState state, ItemBusSize type) {
    super(EntityRegistration.ITEM_INPUT_BUS.get(), pos, state, type, IOType.INPUT);
  }

  @Override
  public IOInventory buildInventory(int size) {
    int[] slots = new int[size];
    for (int i = 0; i < size; i++) {
      slots[i] = i;
    }
    return new IOInventory(slots, new int[] {});
  }

}
