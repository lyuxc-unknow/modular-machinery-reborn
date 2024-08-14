package es.degrassi.mmreborn.common.block;

import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.util.RedstoneHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEnergyHatch extends BlockMachineComponent {
  protected final EnergyHatchSize type;
  public BlockEnergyHatch(EnergyHatchSize type) {
    super(
      Properties.of()
        .strength(2f, 10f)
        .sound(SoundType.METAL)
        .dynamicShape()
        .noOcclusion()
    );
    this.type = type;
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState pState) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
    return RedstoneHelper.getRedstoneLevel(pLevel.getBlockEntity(pPos));
  }
}