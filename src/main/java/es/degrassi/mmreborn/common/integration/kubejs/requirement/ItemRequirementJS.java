package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.api.ItemIngredient;
import es.degrassi.mmreborn.api.ItemTagIngredient;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.crafting.requirement.jei.IJeiRequirement;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmreborn.common.machine.IOType;
import net.minecraft.world.item.ItemStack;

public interface ItemRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireItem(ItemStack stack, int x, int y) {
    return addRequirement(new RequirementItem(IOType.INPUT, new ItemIngredient(stack.getItem()), stack.getCount(), new IJeiRequirement.JeiPositionedRequirement(x, y)));
  }

  default MachineRecipeBuilderJS requireItemTag(String tag, int x, int y) {
    return requireItemTag(tag, 1, x, y);
  }

  default MachineRecipeBuilderJS requireItemTag(String tag, int amount, int x, int y) {
    return addRequirement(new RequirementItem(IOType.INPUT, ItemTagIngredient.create(tag), amount, new IJeiRequirement.JeiPositionedRequirement(x, y)));
  }

  default MachineRecipeBuilderJS produceItem(ItemStack stack, int x, int y) {
    return addRequirement(new RequirementItem(IOType.OUTPUT, new ItemIngredient(stack.getItem()), stack.getCount(), new IJeiRequirement.JeiPositionedRequirement(x, y)));
  }
}
