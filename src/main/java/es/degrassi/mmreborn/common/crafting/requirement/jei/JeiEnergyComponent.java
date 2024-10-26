package es.degrassi.mmreborn.common.crafting.requirement.jei;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class JeiEnergyComponent extends JeiComponent<Long, RequirementEnergy> {
  private int width = 16;
  private int height = 61;
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
    tooltip.add(Component.translatable("modular_machinery_reborn.jei.ingredient.energy." + mode, ingredient));
    return tooltip;
  }
}
