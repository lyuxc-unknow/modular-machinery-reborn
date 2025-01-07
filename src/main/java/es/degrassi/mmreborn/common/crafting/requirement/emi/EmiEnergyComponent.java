package es.degrassi.mmreborn.common.crafting.requirement.emi;

import com.google.common.collect.Lists;
import dev.emi.emi.api.widget.WidgetHolder;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import es.degrassi.mmreborn.common.integration.emi.recipe.MMREmiRecipe;
import es.degrassi.mmreborn.common.machine.component.EnergyComponent;
import es.degrassi.mmreborn.common.util.Utils;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.LinkedList;
import java.util.List;

@Getter
public class EmiEnergyComponent extends EmiComponent<Long, RecipeRequirement<EnergyComponent, RequirementEnergy>> {
  private int width = 16;
  private int height = 52;
  private int recipeTime;

  public EmiEnergyComponent(RecipeRequirement<EnergyComponent, RequirementEnergy> requirement) {
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
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    width += 2;
    height += 2;
    super.render(guiGraphics, mouseX, mouseY);
    width -= 2;
    height -= 2;
  }

  @Override
  public List<Component> getTooltip() {
    List<Component> tooltip = new LinkedList<>();
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
  public void addWidgets(WidgetHolder widgets, MMREmiRecipe recipe) {
    this.recipeTime = recipe.getRecipe().getRecipeTotalTickTime();
    super.addWidgets(widgets, recipe);
  }
}
