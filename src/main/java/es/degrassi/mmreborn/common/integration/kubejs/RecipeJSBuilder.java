package es.degrassi.mmreborn.common.integration.kubejs;

import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;

public interface RecipeJSBuilder {
    MachineRecipeBuilderJS addRequirement(ComponentRequirement<?, ?> requirement);
}
