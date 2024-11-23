package es.degrassi.mmreborn.common.integration.emi.recipe;

import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
import es.degrassi.mmreborn.ModularMachineryReborn;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;

public class MMREmiStackSerializer implements EmiStackSerializer<MMREmiStack> {
  @Override
  public String getType() {
    return "mmrstack";
  }

  @Override
  public MMREmiStack create(ResourceLocation id, DataComponentPatch componentChanges, long amount) {
    return MMREmiStack.of(ModularMachineryReborn.getRequirementRegistrar().get(id));
  }
}
