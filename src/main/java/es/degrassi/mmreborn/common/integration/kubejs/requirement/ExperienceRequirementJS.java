package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementExperience;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmreborn.common.machine.IOType;

public interface ExperienceRequirementJS extends RecipeJSBuilder {

  @HideFromJS
  default MachineRecipeBuilderJS requireExperience(long amount, int x, int y) {
    return addRequirement(new RequirementExperience(IOType.INPUT, amount, new PositionedRequirement(x, y)));
  }

  @HideFromJS
  default MachineRecipeBuilderJS produceExperience(long amount, int x, int y) {
    return addRequirement(new RequirementExperience(IOType.OUTPUT, amount, new PositionedRequirement(x, y)));
  }

  default MachineRecipeBuilderJS requireExperience(long amount) {
    return requireExperience(amount, 0, 0);
  }

  default MachineRecipeBuilderJS produceExperience(long amount) {
    return produceExperience(amount, 0, 0);
  }
}
