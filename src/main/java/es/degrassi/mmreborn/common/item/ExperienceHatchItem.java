package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.block.BlockExperienceHatch;
import es.degrassi.mmreborn.common.block.BlockFluidHatch;
import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import lombok.Getter;

@Getter
public class ExperienceHatchItem extends ItemBlockMachineComponent {
  private final ExperienceHatchSize type;

  public ExperienceHatchItem(BlockExperienceHatch block, ExperienceHatchSize type) {
    super(block, new Properties());
    this.type = type;
  }
}
