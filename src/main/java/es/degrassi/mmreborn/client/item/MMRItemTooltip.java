package es.degrassi.mmreborn.client.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class MMRItemTooltip implements ClientTooltipComponent {
  private final ItemStack stack;
  private final Component text;

  private final int textWidth;
  private final int textHeight;

  public MMRItemTooltip(ItemStack stack, Component text) {
    this.stack = stack;
    this.text = text;
    this.textWidth = Minecraft.getInstance().font.width("100x [#modular_machinery_reborn:energyoutputhatch]");
    this.textHeight = Minecraft.getInstance().font.wordWrapHeight(text, textWidth);
  }

  @Override
  public int getHeight() {
    return Math.max(textHeight, 18);
  }

  @Override
  public int getWidth(Font font) {
    return 16 + textWidth + 8;
  }

  @Override
  public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
    guiGraphics.renderItem(stack, x, y);
    guiGraphics.renderItemDecorations(font, stack, x, y);
    guiGraphics.drawWordWrap(font, text, x + 16 + 4, y + 2, textWidth, -1);
  }
}
