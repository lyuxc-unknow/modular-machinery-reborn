package es.degrassi.mmreborn.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.math.MathUtils;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.gui.GuiGraphics;

public class ChemicalRenderer {
  public static void renderChemical(GuiGraphics graphics, int posX, int posY, int width, int height, ChemicalStack stack, long capacity) {
    if(!stack.isEmpty()) {
      RenderSystem.enableBlend();
      int desiredHeight = MathUtils.clampToInt((double)(height) * (double)stack.getAmount() / (double)capacity);
      if (desiredHeight < 1) {
        desiredHeight = 1;
      }

      if (desiredHeight > height) {
        desiredHeight = height;
      }

      Chemical chemical = stack.getChemical();
      MekanismRenderer.color(graphics, chemical);
      GuiUtils.drawTiledSprite(graphics, posX, posY, height, width, desiredHeight, MekanismRenderer.getSprite(chemical.getIcon()), 16, 16, 100, GuiUtils.TilingDirection.UP_RIGHT, false);
      MekanismRenderer.resetColor(graphics);
      RenderSystem.disableBlend();
    }
  }
}
