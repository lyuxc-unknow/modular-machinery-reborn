package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.common.block.prop.ItemDurabilityHatchSize;
import es.degrassi.mmreborn.common.entity.ItemConsumeDurabilityHatchEntity;
import es.degrassi.mmreborn.common.entity.ItemRepairDurabilityHatchEntity;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockRepairDurabilityHatch extends BlockDurabilityHatch {
  public BlockRepairDurabilityHatch(ItemDurabilityHatchSize size) {
    super(size);
  }
  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
    return new ItemRepairDurabilityHatchEntity(blockPos, blockState, this.size);
  }

  @Override
  protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
    List<ItemStack> drops = super.getDrops(state, builder);
    switch (size) {
      case TINY ->        drops.add(ItemRegistration.ITEM_REPAIR_DURABILITY_HATCH_TINY.get().getDefaultInstance());
      case SMALL ->       drops.add(ItemRegistration.ITEM_REPAIR_DURABILITY_HATCH_SMALL.get().getDefaultInstance());
      case NORMAL ->      drops.add(ItemRegistration.ITEM_REPAIR_DURABILITY_HATCH_NORMAL.get().getDefaultInstance());
      case BIG ->         drops.add(ItemRegistration.ITEM_REPAIR_DURABILITY_HATCH_BIG.get().getDefaultInstance());
    }
    return drops;
  }
}
