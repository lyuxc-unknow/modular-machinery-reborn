package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.entity.ExperienceOutputHatchEntity;
import es.degrassi.mmreborn.common.entity.FluidOutputHatchEntity;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockExperienceOutputHatch extends BlockExperienceHatch {
  public BlockExperienceOutputHatch(ExperienceHatchSize size) {
    super(size);
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
    return new ExperienceOutputHatchEntity(blockPos, blockState, size);
  }

  @Override
  protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
    List<ItemStack> drops = super.getDrops(state, builder);
    switch (size) {
      case TINY ->        drops.add(ItemRegistration.EXPERIENCE_OUTPUT_HATCH_TINY.get().getDefaultInstance());
      case SMALL ->       drops.add(ItemRegistration.EXPERIENCE_OUTPUT_HATCH_SMALL.get().getDefaultInstance());
      case NORMAL ->      drops.add(ItemRegistration.EXPERIENCE_OUTPUT_HATCH_NORMAL.get().getDefaultInstance());
      case REINFORCED ->  drops.add(ItemRegistration.EXPERIENCE_OUTPUT_HATCH_REINFORCED.get().getDefaultInstance());
      case BIG ->         drops.add(ItemRegistration.EXPERIENCE_OUTPUT_HATCH_BIG.get().getDefaultInstance());
      case HUGE ->        drops.add(ItemRegistration.EXPERIENCE_OUTPUT_HATCH_HUGE.get().getDefaultInstance());
      case LUDICROUS ->   drops.add(ItemRegistration.EXPERIENCE_OUTPUT_HATCH_LUDICROUS.get().getDefaultInstance());
      case VACUUM ->      drops.add(ItemRegistration.EXPERIENCE_OUTPUT_HATCH_VACUUM.get().getDefaultInstance());
    }
    return drops;
  }
}
