package es.degrassi.mmreborn.client.requirement;

import com.mojang.blaze3d.platform.Lighting;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.runtime.EmiDrawContext;
import es.degrassi.mmreborn.common.crafting.requirement.emi.StackHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public interface ItemRendering extends StackHolder {
  int RENDER_ICON = 1;
  int RENDER_AMOUNT = 2;
  int RENDER_REMAINDER = 8;

  int getWidth();
  int getHeight();

  default void drawStack(GuiGraphics draw, int x, int y, int flags) {
    EmiDrawContext context = EmiDrawContext.wrap(draw);
    int xOff = (getWidth() - 16) / 2;
    int yOff = (getHeight() - 16) / 2;
    ItemStack stack = getStack().getItemStack();if ((flags & RENDER_ICON) != 0) {
      Lighting.setupFor3DItems();
      draw.renderFakeItem(stack, x + xOff, y + yOff);
      draw.renderItemDecorations(Minecraft.getInstance().font, stack, x + xOff, y + yOff, "");
    }
    if ((flags & RENDER_AMOUNT) != 0) {
      String count = "";
      if (getStack().getAmount() != 1) {
        count += getStack().getAmount();
      }
      EmiRenderHelper.renderAmount(context, x + xOff, y + yOff, EmiPort.literal(count));
    }
    if ((flags & RENDER_REMAINDER) != 0) {
      EmiRender.renderRemainderIcon(getStack(), context.raw(), x + xOff, y + yOff);
    }
  }
}
