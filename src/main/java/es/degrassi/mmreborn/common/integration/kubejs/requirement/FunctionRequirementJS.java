package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFunction;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;

public interface FunctionRequirementJS extends RecipeJSBuilder {

  default RecipeJSBuilder requireFunctionToStart(String id) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementFunction(RequirementFunction.Phase.CHECK, id)));
  }

  default RecipeJSBuilder requireFunctionOnStart(String id) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementFunction(RequirementFunction.Phase.START, id)));
  }

  default RecipeJSBuilder requireFunctionEachTick(String id) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementFunction(RequirementFunction.Phase.TICK, id)));
  }

  default RecipeJSBuilder requireFunctionOnEnd(String id) {
    return this.addRequirement(new RecipeRequirement<>(new RequirementFunction(RequirementFunction.Phase.END, id)));
  }
}
