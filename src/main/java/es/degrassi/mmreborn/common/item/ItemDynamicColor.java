package es.degrassi.mmreborn.common.item;

import es.degrassi.mmreborn.common.data.Config;
import net.minecraft.world.item.ItemStack;

public interface ItemDynamicColor {
  default int getColorFromItemstack(ItemStack stack, int tintIndex) {
    return Config.machineColor;
  }
}
