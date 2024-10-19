package es.degrassi.mmreborn.common.integration.kubejs.requirements;

import es.degrassi.mmreborn.api.FluidIngredient;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFluid;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmreborn.common.machine.IOType;
import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireFluid(FluidStack stack) {
    return addRequirement(new RequirementFluid(IOType.INPUT, new FluidIngredient(stack.getFluid()), stack.getAmount()));
  }

  default MachineRecipeBuilderJS produceFluid(FluidStack stack) {
    return addRequirement(new RequirementFluid(IOType.OUTPUT, new FluidIngredient(stack.getFluid()), stack.getAmount()));
  }
}
