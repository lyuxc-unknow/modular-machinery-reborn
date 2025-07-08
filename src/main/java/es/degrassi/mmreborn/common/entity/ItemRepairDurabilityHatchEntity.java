package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.block.prop.ItemDurabilityHatchSize;
import es.degrassi.mmreborn.common.entity.base.TileDurabilityHatch;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.state.BlockState;

public class ItemRepairDurabilityHatchEntity extends TileDurabilityHatch {
  public ItemRepairDurabilityHatchEntity(BlockPos pos, BlockState blockState, ItemDurabilityHatchSize size) {
    super(EntityRegistration.ITEM_REPAIR_DURABILITY_HATCH.get(), pos, blockState, size, IOType.OUTPUT);
  }
  public ItemRepairDurabilityHatchEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.ITEM_REPAIR_DURABILITY_HATCH.get(), pos, blockState, ItemDurabilityHatchSize.TINY, IOType.OUTPUT);
  }

  @Override
  public IOInventory buildInventory(int slots) {
    int[] inSlots = new int[slots];
    for (int i = 0; i < slots; i++) {
      inSlots[i] = i;
    }
    return new IOInventory(new int[0], inSlots, stack -> stack.has(DataComponents.DAMAGE),Direction.values());
  }
}
