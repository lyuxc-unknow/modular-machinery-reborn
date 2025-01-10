package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementTime;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import es.degrassi.mmreborn.common.util.IntRange;

public interface TimeRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS time(String time, int x, int y) {
    try {
      IntRange range = IntRange.createFromString(time);
      return this.addRequirement(new RecipeRequirement<>(new RequirementTime(range, new PositionedRequirement(x, y))));
    } catch (IllegalArgumentException e) {
      return error("Impossible to parse time range: \"{}\", ", time, e);
    }
  }
  default MachineRecipeBuilderJS time(String time) {
    return time(time, 0, 0);
  }
}
