package es.degrassi.mmreborn.common.crafting.helper.restriction;

import es.degrassi.mmreborn.common.crafting.helper.ComponentOutputRestrictor;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import net.minecraft.world.item.ItemStack;

public class RestrictionInventory extends ComponentOutputRestrictor<ItemStack> {

  public RestrictionInventory(ItemStack inserted, ProcessingComponent<?> exactComponent) {
    super(inserted, exactComponent);
  }
}
