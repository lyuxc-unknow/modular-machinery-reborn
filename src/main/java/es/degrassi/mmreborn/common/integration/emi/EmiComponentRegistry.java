package es.degrassi.mmreborn.common.integration.emi;

import es.degrassi.mmreborn.api.integration.emi.EmiComponentFactory;
import es.degrassi.mmreborn.api.integration.emi.RegisterEmiComponentEvent;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import net.neoforged.fml.ModLoader;

import java.util.Map;

public class EmiComponentRegistry {
  private static Map<RequirementType<?>, EmiComponentFactory<?, ?>> components;

  public static void init() {
    RegisterEmiComponentEvent event = new RegisterEmiComponentEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    components = event.getComponents();
  }

  public static boolean hasEmiComponent(RequirementType<?> type) {
    return components.containsKey(type);
  }

  @SuppressWarnings("unchecked")
  public static <C extends ComponentRequirement<T, C>, T> EmiComponentFactory<C, T> getEmiComponent(RequirementType<C> type) {
    return (EmiComponentFactory<C, T>) components.get(type);
  }
}
