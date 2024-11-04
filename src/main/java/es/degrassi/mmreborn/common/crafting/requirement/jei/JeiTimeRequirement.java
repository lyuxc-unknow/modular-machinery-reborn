package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementTime;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementWeather;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.util.IntRange;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

public class JeiTimeRequirement extends JeiComponent<IntRange, RequirementTime> {
  public JeiTimeRequirement(RequirementTime requirement) {
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
  public List<IntRange> ingredients() {
    return List.of(requirement.time());
  }

  @Override
  public void setRecipeInput(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    category.textsToRender.add(
      Component.translatable(
        "modular_machinery_reborn.jei.ingredient.time",
        requirement.time().toFormattedString()
      )
    );
  }

  @Override
  public void setRecipeOutput(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {}
}
