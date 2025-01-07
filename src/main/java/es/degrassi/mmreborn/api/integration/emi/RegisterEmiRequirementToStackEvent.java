package es.degrassi.mmreborn.api.integration.emi;

import com.google.common.collect.ImmutableMap;
import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.HashMap;
import java.util.Map;

public class RegisterEmiRequirementToStackEvent extends Event implements IModBusEvent {
  private final Map<RequirementType<?>, EmiStackFactory<?>> stacks = new HashMap<>();

  public <R extends RecipeRequirement<T, C>, C extends IRequirement<T>, T extends MachineComponent<?>,
      E extends EmiStack> void register(RequirementType<C> requirement, EmiStackFactory<R> factory) {
    if (stacks.containsKey(requirement)) {
      throw new IllegalArgumentException("Emi stack already registered for requirement: " + requirement.getCodec().name());
    }
    stacks.put(requirement, factory);
  }

  public Map<RequirementType<?>, EmiStackFactory<?>> getStacks() {
    return ImmutableMap.copyOf(stacks);
  }
}
