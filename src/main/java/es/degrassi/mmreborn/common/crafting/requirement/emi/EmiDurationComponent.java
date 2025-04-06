package es.degrassi.mmreborn.common.crafting.requirement.emi;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.widget.AnimatedTextureWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.api.integration.emi.Direction;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementDuration;
import es.degrassi.mmreborn.common.integration.emi.recipe.MMREmiRecipe;
import es.degrassi.mmreborn.common.machine.component.DurationComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiDurationComponent extends EmiComponent<Integer, RecipeRequirement<DurationComponent,
    RequirementDuration>> {
  private int duration;
  private final Direction direction;
  private final int ticks;
  private final AnimatedTextureWidget progress;
  private final boolean inverted;
  public EmiDurationComponent(RecipeRequirement<DurationComponent, RequirementDuration> requirement, int msPerCycle,
                              Direction direction, boolean inverted) {
    super(requirement, 45, 1, false);
    this.direction = direction;
    this.ticks = msPerCycle;
    this.progress = createProgress();
    this.inverted = inverted;
  }

  @Override
  public @Nullable ResourceLocation texture() {
    return EmiRenderHelper.WIDGETS;
  }

  private AnimatedTextureWidget createProgress() {
    return new AnimatedTextureWidget(EmiRenderHelper.WIDGETS,
        0,
        0,
        22,
        15,
        45,
        17,
        ticks,
        direction.horizontal(),
        direction.endToStart(),
        inverted
    );
  }

  @Override
  public List<Integer> ingredients() {
    return List.of(duration);
  }

  @Override
  public int getWidth() {
    return progress.getBounds().width();
  }

  @Override
  public int getHeight() {
    return progress.getBounds().height();
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    super.render(guiGraphics, mouseX, mouseY);
    progress.render(guiGraphics, mouseX, mouseY, 0);
  }

  @Override
  public List<Component> getTooltip() {
    List<Component> tooltip = super.getTooltip();
    tooltip.add(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.duration",
        duration
    ));
    return tooltip;
  }

  @Override
  public void addWidgets(WidgetHolder widgets, MMREmiRecipe recipe) {
    this.duration = recipe.getRecipe().getRecipeTotalTickTime();
    super.addWidgets(widgets, recipe);
  }
}
