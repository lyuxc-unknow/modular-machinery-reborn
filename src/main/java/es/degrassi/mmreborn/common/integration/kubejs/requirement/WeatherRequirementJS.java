package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.common.crafting.requirement.RequirementWeather;
import es.degrassi.mmreborn.common.crafting.requirement.jei.IJeiRequirement;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;

public interface WeatherRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS weather(RequirementWeather.WeatherType type, int x, int y) {
    return addRequirement(new RequirementWeather(type, new IJeiRequirement.JeiPositionedRequirement(x, y)));
  }
}
