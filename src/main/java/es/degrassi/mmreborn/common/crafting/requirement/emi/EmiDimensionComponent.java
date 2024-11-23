package es.degrassi.mmreborn.common.crafting.requirement.emi;

import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementDimension;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;

public class EmiDimensionComponent extends EmiComponent<ResourceLocation, RequirementDimension> implements ItemRendering {
  public EmiDimensionComponent(RequirementDimension requirement) {
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
  public List<ResourceLocation> ingredients() {
    return Lists.newArrayList(requirement.filter().iterator());
  }

  public EmiStack getStack() {
    return EmiStack.of(ItemRegistration.DIMENSIONAL_DETECTOR.toStack());
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    super.render(guiGraphics, mouseX, mouseY);
    drawStack(guiGraphics, 0, 0, -1);
  }

  @Override
  public List<Component> getTooltip() {
    StringBuilder dimensions = new StringBuilder();
    ingredients().forEach(dimension -> dimensions.append(dimension.toString()).append(","));
    int index = dimensions.lastIndexOf(",");
    if (index >= dimensions.length() - 1)
      dimensions.deleteCharAt(index);
    return List.of(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.dimension." + requirement.blacklist(),
        dimensions.toString()
    ));
  }
}
