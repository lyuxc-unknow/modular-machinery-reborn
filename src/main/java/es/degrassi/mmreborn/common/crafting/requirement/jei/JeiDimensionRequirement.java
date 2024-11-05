package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementDimension;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
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
    return 18;
  }

  @Override
  public int getHeight() {
    return 18;
  }

  @Override
  public List<ResourceLocation> ingredients() {
    return Lists.newArrayList(requirement.filter().iterator());
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    StringBuilder dimensions = new StringBuilder();
    ingredients().forEach(dimension -> dimensions.append(dimension.toString()).append(","));
    int index = dimensions.lastIndexOf(",");
    if (index >= dimensions.length() - 1)
      dimensions.deleteCharAt(index);
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, getPosition().x(), getPosition().y())
        .addItemStack(ItemRegistration.DIMENSIONAL_DETECTOR.toStack())
        .addRichTooltipCallback((view, tooltip) -> {
          tooltip.clear();
          tooltip.add(Component.translatable(
              "modular_machinery_reborn.jei.ingredient.dimension." + requirement.blacklist(),
              dimensions.toString()
          ));
        });
  }
}
