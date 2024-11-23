package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.common.crafting.requirement.RequirementDimension;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface DimensionRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS dimensions(List<ResourceLocation> dimensions, int x, int y) {
    return dimensions(dimensions, false, x, y);
  }

  default MachineRecipeBuilderJS dimensions(List<ResourceLocation> dimensions, boolean blacklist, int x, int y) {
    return addRequirement(new RequirementDimension(dimensions, blacklist, new PositionedRequirement(x, y)));
  }

  default MachineRecipeBuilderJS dimensions(List<ResourceLocation> dimensions) {
    return dimensions(dimensions, false);
  }

  default MachineRecipeBuilderJS dimensions(List<ResourceLocation> dimensions, boolean blacklist) {
    return dimensions(dimensions, blacklist, 0, 0);
  }
}
