package es.degrassi.mmreborn.common.integration.kubejs.requirement;

import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementBiome;
import es.degrassi.mmreborn.common.integration.kubejs.MachineRecipeBuilderJS;
import es.degrassi.mmreborn.common.integration.kubejs.RecipeJSBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface BiomeRequirementJS extends RecipeJSBuilder {

  default MachineRecipeBuilderJS biomes(List<ResourceLocation> biomes, int x, int y) {
    return biomes(biomes, false, x, y);
  }

  default MachineRecipeBuilderJS biomes(List<ResourceLocation> biomes, boolean blacklist, int x, int y) {
    return addRequirement(new RecipeRequirement<>(new RequirementBiome(biomes, blacklist, new PositionedRequirement(x, y))));
  }

  default MachineRecipeBuilderJS biomes(List<ResourceLocation> biomes) {
    return biomes(biomes, false);
  }

  default MachineRecipeBuilderJS biomes(List<ResourceLocation> biomes, boolean blacklist) {
    return biomes(biomes, blacklist, 0, 0);
  }
}
