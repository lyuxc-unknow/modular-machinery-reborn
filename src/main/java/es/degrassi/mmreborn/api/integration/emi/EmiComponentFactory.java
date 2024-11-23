package es.degrassi.mmreborn.api.integration.emi;

import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiComponent;

public interface EmiComponentFactory<C extends ComponentRequirement<T, C>, T> {
  EmiComponent<T, C> create(C requirement);
}
