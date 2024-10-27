package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.api.ItemIngredient;
import es.degrassi.mmreborn.api.ItemTagIngredient;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmreborn.common.machine.IOType;
import net.minecraft.world.item.ItemStack;

public interface ItemRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireItem(ItemStack stack) {
    return addRequirement(new RequirementItem(IOType.INPUT, new ItemIngredient(stack.getItem()), stack.getCount()));
  }

  default MachineRecipeBuilderJS requireItemTag(String tag) {
    return requireItemTag(tag, 1);
  }

  default MachineRecipeBuilderJS requireItemTag(String tag, int amount) {
    return addRequirement(new RequirementItem(IOType.INPUT, ItemTagIngredient.create(tag), amount));
  }

  default MachineRecipeBuilderJS produceItem(ItemStack stack) {
    return addRequirement(new RequirementItem(IOType.OUTPUT, new ItemIngredient(stack.getItem()), stack.getCount()));
  }
}
