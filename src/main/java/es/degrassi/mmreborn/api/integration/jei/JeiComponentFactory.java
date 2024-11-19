package es.degrassi.mmreborn.api.integration.jei;

import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiComponent;

@FunctionalInterface
public interface JeiComponentFactory<C extends ComponentRequirement<T, C>, T> {
  JeiComponent<T, C> create(C requirement);
}
