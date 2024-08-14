package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.block.BlockEnergyHatch;
import es.degrassi.mmreborn.common.block.BlockFluidHatch;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import lombok.Getter;

@Getter
public class FluidHatchItem extends ItemBlockMachineComponent {
  private final FluidHatchSize type;

  public FluidHatchItem(BlockFluidHatch block, FluidHatchSize type) {
    super(block, new Properties());
    this.type = type;
  }
}
