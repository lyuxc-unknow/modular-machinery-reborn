package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.block.BlockEnergyHatch;
import es.degrassi.mmreborn.common.block.BlockInputBus;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import lombok.Getter;

@Getter
public class InputBusItem extends ItemBlockMachineComponent {
  private final ItemBusSize type;

  public InputBusItem(BlockInputBus block, ItemBusSize type) {
    super(block, new Properties());
    this.type = type;
  }
}
