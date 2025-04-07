package es.degrassi.mmreborn.api.integration.emi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import dev.emi.emi.api.stack.EmiIngredient;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;

public class RegisterEmiRequirementToIngredientEvent extends Event implements IModBusEvent {
  private final Map<RequirementType<?>, EmiIngredientFactory<?>> stacks = Maps.newHashMap();

  public <R extends RecipeRequirement<T, C>, C extends IRequirement<T>, T extends MachineComponent<?>, E extends EmiIngredient> void register(RequirementType<C> requirement, EmiIngredientFactory<R> factory) {
    if (stacks.containsKey(requirement)) {
      throw new IllegalArgumentException("Emi ingredient already registered for requirement: " + requirement.getCodec().name());
    }
    stacks.put(requirement, factory);
  }

  public Map<RequirementType<?>, EmiIngredientFactory<?>> getStacks() {
    return ImmutableMap.copyOf(stacks);
  }
}
