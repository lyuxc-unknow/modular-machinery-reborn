package es.degrassi.mmreborn.common.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemBlueprint extends Item {
  public ItemBlueprint() {
    super(
      new Properties()
        .stacksTo(16)
    );
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
    pTooltipComponents.add(Component.translatable("tooltip.blueprint"));
  }
}
