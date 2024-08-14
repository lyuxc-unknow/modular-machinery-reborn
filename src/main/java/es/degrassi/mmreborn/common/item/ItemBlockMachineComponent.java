package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.data.Config;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ItemBlockMachineComponent extends BlockItem implements ItemDynamicColor {

  public ItemBlockMachineComponent(Block block, Properties props) {
    super(block, props);
  }

  @Override
  public int getColorFromItemstack(ItemStack stack, int tintIndex) {
    if(stack.isEmpty()) {
      return 0;
    }
    return Config.machineColor;
  }
}
