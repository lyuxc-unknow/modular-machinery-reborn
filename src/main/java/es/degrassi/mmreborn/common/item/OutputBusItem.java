package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.block.BlockOutputBus;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import lombok.Getter;

@Getter
public class OutputBusItem extends ItemBlockMachineComponent {
  private final ItemBusSize type;

  public OutputBusItem(BlockOutputBus block, ItemBusSize type) {
    super(block, new Properties());
    this.type = type;
  }
}
