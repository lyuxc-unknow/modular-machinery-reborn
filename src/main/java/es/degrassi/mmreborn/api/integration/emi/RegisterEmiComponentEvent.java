package es.degrassi.mmreborn.api.integration.emi;

import com.google.common.collect.ImmutableMap;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.HashMap;
import java.util.Map;

public class RegisterEmiComponentEvent extends Event implements IModBusEvent {
  private final Map<RequirementType<?>, EmiComponentFactory<?, ?>> components = new HashMap<>();

  public <R extends RecipeRequirement<?, ?>, T extends IRequirement<?>, X> void register(RequirementType<T> requirement,
                                                                                          EmiComponentFactory<R, X> component) {
    if (components.containsKey(requirement))
      throw new IllegalArgumentException("Emi component already registered for requirement: " + requirement.getCodec().name());
    components.put(requirement, component);
  }

  public Map<RequirementType<?>, EmiComponentFactory<?, ?>> getComponents() {
    return ImmutableMap.copyOf(components);
  }
}
