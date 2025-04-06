package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.block.ParallelHatchBlock;
import es.degrassi.mmreborn.common.block.prop.ParallelHatchSize;
import lombok.Getter;

@Getter
public class ParallelHatchItem extends ItemBlockMachineComponent {
  private final ParallelHatchSize type;

  public ParallelHatchItem(ParallelHatchBlock block, ParallelHatchSize type) {
    super(block, new Properties());
    this.type = type;
  }
}
