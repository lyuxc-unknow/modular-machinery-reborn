package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmreborn.common.machine.IOType;

public interface EnergyRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS requireEnergy(int amount) {
    return addRequirement(new RequirementEnergy(IOType.INPUT, amount));
  }

  default MachineRecipeBuilderJS produceEnergy(int amount) {
    return addRequirement(new RequirementEnergy(IOType.OUTPUT, amount));
  }
}
