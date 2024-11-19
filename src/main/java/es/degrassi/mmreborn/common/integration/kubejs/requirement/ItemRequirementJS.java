package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiPositionedRequirement;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmreborn.common.machine.IOType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public interface ItemRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireItem(SizedIngredient stack, int x, int y) {
    if(stack.getItems().length == 0)
      return this.error("Invalid empty ingredient in item input requirement");
    return addRequirement(new RequirementItem(IOType.INPUT, stack,
        new JeiPositionedRequirement(x, y)));
  }

  default MachineRecipeBuilderJS produceItem(ItemStack stack, int x, int y) {
    if(stack.isEmpty())
      return this.error("Invalid empty item in item output requirement");
    return addRequirement(new RequirementItem(IOType.OUTPUT, new SizedIngredient(Ingredient.of(stack),
        stack.getCount()),
        new JeiPositionedRequirement(x, y)));
  }
  default MachineRecipeBuilderJS requireItem(SizedIngredient stack) {
    return requireItem(stack, 0, 0);
  }

  default MachineRecipeBuilderJS produceItem(ItemStack stack) {
    return produceItem(stack, 0, 0);
  }
}
