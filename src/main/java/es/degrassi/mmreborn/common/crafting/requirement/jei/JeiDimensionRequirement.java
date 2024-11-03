package es.degrassi.mmreborn.common.crafting.requirement.jei;

import com.google.common.collect.Iterables;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementDimension;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class JeiDimensionRequirement extends JeiComponent<ResourceLocation, RequirementDimension> {
  public JeiDimensionRequirement(RequirementDimension requirement) {
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
    StringBuilder dimensions = new StringBuilder();
    ingredients().forEach(dimension -> dimensions.append(dimension.toString()).append(","));
    int index = dimensions.lastIndexOf(",");
    if (index >= dimensions.length() - 1)
      dimensions.deleteCharAt(index);
    category.textsToRender.add(
      Component.translatable(
        "modular_machinery_reborn.jei.ingredient.dimension." + requirement.blacklist(),
        dimensions.toString()
      )
    );
  }

  @Override
  public void setRecipeOutput(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {}
}
