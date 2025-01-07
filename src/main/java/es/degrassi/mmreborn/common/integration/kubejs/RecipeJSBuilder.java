package es.degrassi.mmreborn.common.integration.kubejs;

import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;

public interface RecipeJSBuilder {
    MachineRecipeBuilderJS addRequirement(RecipeRequirement<?, ?> requirement);

    MachineRecipeBuilderJS error(String error, Object... args);
}
