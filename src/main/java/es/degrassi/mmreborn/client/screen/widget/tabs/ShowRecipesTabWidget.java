package es.degrassi.mmreborn.client.screen.widget.tabs;

import dev.emi.emi.api.EmiApi;
import es.degrassi.mmreborn.common.integration.emi.MMREmiPlugin;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.util.Mods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ShowRecipesTabWidget extends TabWidget {
  public ShowRecipesTabWidget(@Nullable ResourceLocation icon, DynamicMachine machine) {
    super(0, 0, icon, (mouseX, mouseY, button) -> {
      if (Mods.isEMILoaded()) {
        EmiApi.displayRecipeCategory(MMREmiPlugin.categories.get(machine));
      }
    });
  }

  @Override
  protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
  }

  @Override
  public void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    Component component = Mods.isEMILoaded() ? Component.translatable("emi.tooltip.show.recipes") : null;
    if (component != null)
      guiGraphics.renderTooltip(
          Minecraft.getInstance().font,
          List.of(component.getVisualOrderText()),
          x,
          y
      );
  }
}
