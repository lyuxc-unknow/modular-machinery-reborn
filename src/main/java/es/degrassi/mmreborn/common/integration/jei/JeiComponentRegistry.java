package es.degrassi.mmreborn.common.integration.jei;

import es.degrassi.mmreborn.api.integration.jei.JeiComponentFactory;
import es.degrassi.mmreborn.api.integration.jei.RegisterJeiComponentEvent;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import net.neoforged.fml.ModLoader;

import java.util.Map;

public class JeiComponentRegistry {
  private static Map<RequirementType<?>, JeiComponentFactory<?, ?>> components;

  public static void init() {
    RegisterJeiComponentEvent event = new RegisterJeiComponentEvent();
    ModLoader.postEventWrapContainerInModOrder(event);
    components = event.getComponents();
  }

  public static boolean hasJeiComponent(RequirementType<?> requirement) {
    return components.containsKey(requirement);
  }

  @SuppressWarnings("unchecked")
  public static <C extends ComponentRequirement<T, C>, T> JeiComponentFactory<C, T> getJeiComponent(RequirementType<C> requirement) {
    return (JeiComponentFactory<C, T>) components.get(requirement);
  }
}
