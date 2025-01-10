package es.degrassi.mmreborn.common.crafting.requirement.jei;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmreborn.common.machine.component.EnergyComponent;
import es.degrassi.mmreborn.common.util.Utils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JeiEnergyComponent extends JeiComponent<Long, RecipeRequirement<EnergyComponent, RequirementEnergy>> {
  private int width = 16;
  private int height = 52;
  private int recipeTime;

  public JeiEnergyComponent(RecipeRequirement<EnergyComponent, RequirementEnergy> requirement) {
    super(requirement, 18, 0);
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public List<Long> ingredients() {
    return Lists.newArrayList(requirement.requirement().getRequiredEnergyPerTick());
  }

  @Override
  public void render(@NotNull GuiGraphics guiGraphics, @NotNull Long ingredient) {
    width += 2;
    height += 2;
    super.render(guiGraphics, ingredient);
    width -= 2;
    height -= 2;
  }

  @Override
  @SuppressWarnings("removal")
  public @NotNull List<Component> getTooltip(@NotNull Long ingredient, @NotNull TooltipFlag tooltipFlag) {
    List<Component> tooltip = super.getTooltip(ingredient, tooltipFlag);
    String mode = requirement.requirement().getMode().isInput() ? "input" : "output";
    tooltip.add(
        Component.translatable(
            "modular_machinery_reborn.jei.ingredient.energy.total." + mode,
            Utils.format(requirement.requirement().requirementPerTick * recipeTime),
            Utils.format(requirement.requirement().requirementPerTick)
        )
    );
    return tooltip;
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    this.recipeTime = recipe.getRecipeTotalTickTime();
    builder
        .addSlot(RecipeIngredientRole.RENDER_ONLY, getPosition().x(), getPosition().y())
        .setCustomRenderer(CustomIngredientTypes.LONG, this)
        .addIngredients(CustomIngredientTypes.LONG, ingredients());
  }
}
