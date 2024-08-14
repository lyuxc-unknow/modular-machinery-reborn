package es.degrassi.mmreborn.common.crafting;

import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public interface PreparedRecipe {

  ResourceLocation getId();

  ResourceLocation getAssociatedMachineName();

  int getTotalProcessingTickTime();

  int getPriority();

  default boolean voidPerTickFailure() {
    return false;
  }

  List<ComponentRequirement<?, ?>> getComponents();

}
