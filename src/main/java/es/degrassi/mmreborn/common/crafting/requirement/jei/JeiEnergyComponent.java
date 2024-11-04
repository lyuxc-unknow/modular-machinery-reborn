package es.degrassi.mmreborn.common.crafting.requirement.jei;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmreborn.common.util.Utils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class JeiEnergyComponent extends JeiComponent<Long, RequirementEnergy> {
  private int width = 16;
  private int height = 61;
  private int recipeTime;
  public JeiEnergyComponent(RequirementEnergy requirement) {
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
    return Lists.newArrayList(requirement.getRequiredEnergyPerTick());
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
    String mode = requirement.getActionType().isInput() ? "input" : "output";
    tooltip.add(
        Component.translatable(
            "modular_machinery_reborn.jei.ingredient.energy.total." + mode,
            Utils.format(ingredient * recipeTime),
            Utils.format(ingredient)
        )
    );
    return tooltip;
  }

  @Override
  public void setRecipeInput(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    addEnergyComponent(category, builder, category.processedInputComponents);
    recipeTime = recipe.getRecipeTotalTickTime();
    category.updateMaxHeightInput(this, true);
  }

  @Override
  public void setRecipeOutput(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    addEnergyComponent(category, builder, category.processedOutputComponents);
    recipeTime = recipe.getRecipeTotalTickTime();
    category.updateMaxHeightOutput(this, true);
  }

  private void addEnergyComponent(MMRRecipeCategory category, @NotNull IRecipeLayoutBuilder builder, List<MMRRecipeCategory.ComponentValue> processedComponents) {
    category.updateByProcessed(processedComponents, getWidth(), getHeight(), true);
    builder
      .addSlot(RecipeIngredientRole.RENDER_ONLY, category.x.get() + 1, category.y.get() + 1)
      .setCustomRenderer(CustomIngredientTypes.ENERGY, this)
      .addIngredients(CustomIngredientTypes.ENERGY, ingredients());
    category.x.getAndAdd(category.gapX);
    category.x.getAndAdd(getWidth());
  }
}
