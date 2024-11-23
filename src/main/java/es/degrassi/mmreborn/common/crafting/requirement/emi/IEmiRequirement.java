package es.degrassi.mmreborn.common.crafting.requirement.emi;

import dev.emi.emi.api.widget.WidgetHolder;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.integration.emi.recipe.MMREmiRecipe;

public interface IEmiRequirement<C, T extends ComponentRequirement<C, T>> {
  T getRequirement();

  void addWidgets(WidgetHolder widgets, MMREmiRecipe recipe);

  default PositionedRequirement getPosition() {
    return getRequirement().getPosition();
  }
}
