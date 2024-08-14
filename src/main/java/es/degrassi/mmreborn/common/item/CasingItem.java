package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.block.BlockCasing;
import lombok.Getter;

@Getter
public class CasingItem extends ItemBlockMachineComponent {

  public CasingItem(BlockCasing casing) {
    super(casing, new Properties());
  }
}
