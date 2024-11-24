package es.degrassi.mmreborn.api.integration.emi;

import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;

import java.util.List;

public interface EmiStackFactory<T extends ComponentRequirement<C, T>, C> {
  List<EmiStack> create(T requirement);
}
