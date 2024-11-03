package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementBiome;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementDimension;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class JeiBiomeRequirement extends JeiComponent<ResourceLocation, RequirementBiome> {
  public JeiBiomeRequirement(RequirementBiome requirement) {
    super(requirement, 0, 0);
  }

  @Override
  public int getWidth() {
    return 0;
  }

  @Override
  public int getHeight() {
    return 0;
  }

  @Override
  public List<ResourceLocation> ingredients() {
    return Lists.newArrayList(requirement.filter().iterator());
  }

  @Override
  public void setRecipeInput(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    StringBuilder biomes = new StringBuilder();
    ingredients().forEach(biome -> biomes.append(biome.toString()).append(","));
    int index = biomes.lastIndexOf(",");
    if (index >= biomes.length() - 1)
      biomes.deleteCharAt(index);
    category.textsToRender.add(
      Component.translatable(
        "modular_machinery_reborn.jei.ingredient.biome." + requirement.blacklist(),
          biomes.toString()
      )
    );
  }

  @Override
  public void setRecipeOutput(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {}
}
