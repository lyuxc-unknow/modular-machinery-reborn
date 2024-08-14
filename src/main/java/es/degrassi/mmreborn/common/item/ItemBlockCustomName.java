package es.degrassi.mmreborn.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ItemBlockCustomName extends BlockItem {

  public ItemBlockCustomName(Block block, Properties props) {
    super(block, props);
  }

  @Override
  public int getDamage(ItemStack stack) {
    return stack.getDamageValue();
  }
}
