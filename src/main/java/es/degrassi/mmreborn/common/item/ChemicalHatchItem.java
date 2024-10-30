package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.block.BlockChemicalHatch;
import es.degrassi.mmreborn.common.block.BlockFluidHatch;
import es.degrassi.mmreborn.common.block.prop.ChemicalHatchSize;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import lombok.Getter;

@Getter
public class ChemicalHatchItem extends ItemBlockMachineComponent {
  private final ChemicalHatchSize type;

  public ChemicalHatchItem(BlockChemicalHatch block, ChemicalHatchSize type) {
    super(block, new Properties());
    this.type = type;
  }
}
