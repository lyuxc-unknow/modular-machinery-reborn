package es.degrassi.mmreborn.api.integration.emi;

import com.google.common.collect.ImmutableMap;
import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.HashMap;
import java.util.Map;

public class RegisterEmiRequirementToStackEvent extends Event implements IModBusEvent {
  private final Map<RequirementType<?>, EmiStackFactory<?, ?>> stacks = new HashMap<>();

  public <C extends ComponentRequirement<T, C>, T, E extends EmiStack> void register(RequirementType<C> requirement,
                                                                                     EmiStackFactory<C, T> factory) {
    if (stacks.containsKey(requirement)) {
      throw new IllegalArgumentException("Emi stack already registered for requirement: " + requirement.getCodec().name());
    }
    stacks.put(requirement, factory);
  }

  public Map<RequirementType<?>, EmiStackFactory<?, ?>> getStacks() {
    return ImmutableMap.copyOf(stacks);
  }
}
