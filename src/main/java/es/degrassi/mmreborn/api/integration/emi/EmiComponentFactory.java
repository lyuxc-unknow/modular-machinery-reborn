package es.degrassi.mmreborn.api.integration.emi;

import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiComponent;

public interface EmiComponentFactory<R extends RecipeRequirement<?, ?>, X> {
  EmiComponent<X, R> create(R requirement);
}
