package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmreborn.common.machine.IOType;

public interface EnergyRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireEnergy(int amount, int x, int y) {
    return addRequirement(new RecipeRequirement<>(new RequirementEnergy(IOType.INPUT, amount, new PositionedRequirement(x, y))));
  }

  default MachineRecipeBuilderJS produceEnergy(int amount, int x, int y) {
    return addRequirement(new RecipeRequirement<>(new RequirementEnergy(IOType.OUTPUT, amount, new PositionedRequirement(x, y))));
  }
  default MachineRecipeBuilderJS requireEnergy(int amount) {
    return requireEnergy(amount, 0, 0);
  }

  default MachineRecipeBuilderJS produceEnergy(int amount) {
    return produceEnergy(amount, 0, 0);
  }
}
