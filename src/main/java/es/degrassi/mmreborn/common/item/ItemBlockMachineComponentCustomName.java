package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.block.BlockMachineComponent;
import es.degrassi.mmreborn.common.data.Config;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ItemBlockMachineComponentCustomName extends ItemBlockCustomName implements ItemDynamicColor {

  public ItemBlockMachineComponentCustomName(Block block, Properties props) {
    super(block, props);
  }

  @Override
  public int getColorFromItemstack(ItemStack stack, int tintIndex) {
    if(stack.isEmpty()) {
      return 0;
    }
    if(stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof BlockMachineComponent) {
      return Config.machineColor;
    }
    return 0;
  }

}
