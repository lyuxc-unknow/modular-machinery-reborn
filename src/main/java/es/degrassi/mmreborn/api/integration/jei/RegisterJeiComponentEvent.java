package es.degrassi.mmreborn.api.integration.jei;

import com.google.common.collect.ImmutableMap;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.HashMap;
import java.util.Map;

public class RegisterJeiComponentEvent extends Event implements IModBusEvent {
  private final Map<RequirementType<?>, JeiComponentFactory<?, ?>> components = new HashMap<>();

  public <C extends ComponentRequirement<T, C>, T> void register(RequirementType<C> requirement, JeiComponentFactory<C, T> component) {
    if (components.containsKey(requirement))
      throw new IllegalArgumentException("Jei component already registered for requirement: " + requirement.getCodec().name());
    components.put(requirement, component);
  }

  public Map<RequirementType<?>, JeiComponentFactory<?, ?>> getComponents() {
    return ImmutableMap.copyOf(components);
  }
}
