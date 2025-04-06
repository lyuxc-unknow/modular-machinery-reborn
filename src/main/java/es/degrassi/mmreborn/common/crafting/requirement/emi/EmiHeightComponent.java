package es.degrassi.mmreborn.common.crafting.requirement.emi;

import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.client.requirement.ItemRendering;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementHeight;
import es.degrassi.mmreborn.common.machine.component.HeightComponent;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.util.IntRange;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class EmiHeightComponent extends EmiComponent<IntRange, RecipeRequirement<HeightComponent, RequirementHeight>> implements ItemRendering {
  public EmiHeightComponent(RecipeRequirement<HeightComponent, RequirementHeight> requirement) {
    super(requirement, 0, 0, false);
  }

  @Override
  @Nullable
  public ResourceLocation texture() {
    return null;
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
  public List<IntRange> ingredients() {
    return Collections.singletonList(requirement.requirement().height());
  }

  public EmiStack getStack() {
    return EmiStack.of(ItemRegistration.HEIGHT_METER.toStack());
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    super.render(guiGraphics, mouseX, mouseY);
    drawStack(guiGraphics, 0, 0, -1);
  }

  @Override
  public List<Component> getTooltip() {
    return List.of(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.height",
        requirement.requirement().height().toFormattedString()
    ));
  }
}
