package es.degrassi.mmreborn.api.client.screen;

import net.minecraft.client.gui.GuiGraphics;

public interface TooltipRender {
  void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY);
}
