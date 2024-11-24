package es.degrassi.mmreborn.common.integration.emi;

import es.degrassi.mmreborn.api.integration.emi.EmiStackFactory;
import es.degrassi.mmreborn.api.integration.emi.RegisterEmiRequirementToStackEvent;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import net.neoforged.fml.ModLoader;

import java.util.Map;

public class EmiStackRegistry {
  private static Map<RequirementType<?>, EmiStackFactory<?, ?>> stacks;

  public static void init() {
    RegisterEmiRequirementToStackEvent event = new RegisterEmiRequirementToStackEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    stacks = event.getStacks();
  }

  public static boolean hasEmiStack(RequirementType<?> type) {
    return stacks.containsKey(type);
  }

  @SuppressWarnings("unchecked")
  public static <C extends ComponentRequirement<T, C>, T> EmiStackFactory<C, T> getStack(RequirementType<C> type) {
    return (EmiStackFactory<C, T>) stacks.get(type);
  }
}
