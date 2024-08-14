package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.block.BlockEnergyHatch;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import lombok.Getter;

@Getter
public class EnergyHatchItem extends ItemBlockMachineComponent {
  private final EnergyHatchSize type;

  public EnergyHatchItem(BlockEnergyHatch block, EnergyHatchSize type) {
    super(block, new Properties());
    this.type = type;
  }
}
