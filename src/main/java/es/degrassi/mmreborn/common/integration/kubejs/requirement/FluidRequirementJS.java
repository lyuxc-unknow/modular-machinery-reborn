package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.api.FluidIngredient;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFluid;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmreborn.common.machine.IOType;
import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireFluid(FluidStack stack, int x, int y) {
    return requireFluid(stack, 1, x, y);
  }

  default MachineRecipeBuilderJS produceFluid(FluidStack stack, int x, int y) {
    return produceFluid(stack, 1, x, y);
  }

  default MachineRecipeBuilderJS requireFluid(FluidStack stack) {
    return requireFluid(stack, 1, 0, 0);
  }

  default MachineRecipeBuilderJS produceFluid(FluidStack stack) {
    return produceFluid(stack, 1, 0, 0);
  }

  default MachineRecipeBuilderJS requireFluid(FluidStack stack, float chance, int x, int y) {
    if (chance < 0)
      return this.error("Chance can not bellow 0");
    if (chance > 1)
      return this.error("Chance can not be greater than 0");
    RequirementFluid requirement = new RequirementFluid(IOType.INPUT, new FluidIngredient(stack.getFluid()),
        stack.getAmount(), new PositionedRequirement(x, y));
    requirement.setChance(chance);
    return addRequirement(requirement);
  }

  default MachineRecipeBuilderJS produceFluid(FluidStack stack, float chance, int x, int y) {
    if (chance < 0)
      return this.error("Chance can not bellow 0");
    if (chance > 1)
      return this.error("Chance can not be greater than 0");
    RequirementFluid requirement = new RequirementFluid(IOType.OUTPUT, new FluidIngredient(stack.getFluid()),
        stack.getAmount(), new PositionedRequirement(x, y));
    requirement.setChance(chance);
    return addRequirement(requirement);
  }

  default MachineRecipeBuilderJS requireFluid(FluidStack stack, float chance) {
    return requireFluid(stack, chance, 0, 0);
  }

  default MachineRecipeBuilderJS produceFluid(FluidStack stack, float chance) {
    return produceFluid(stack, chance, 0, 0);
  }
}
