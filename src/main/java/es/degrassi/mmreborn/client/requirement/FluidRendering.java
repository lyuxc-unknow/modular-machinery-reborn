package es.degrassi.mmreborn.client.requirement;

import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.platform.EmiAgnos;
import es.degrassi.mmreborn.common.crafting.requirement.emi.Position;
import es.degrassi.mmreborn.common.crafting.requirement.emi.StackHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.material.Fluid;

public interface FluidRendering extends StackHolder, Position {
  default void renderFluid(GuiGraphics graphics) {
    graphics.pose().pushPose();
    if (getStack().getKey() instanceof Fluid fluid) {
      FluidEmiStack fes = new FluidEmiStack(fluid, getStack().getComponentChanges(), getStack().getAmount());
      boolean floaty = EmiAgnos.isFloatyFluid(fes);
      int x = 1;
      int y = 1;
      int w = getWidth();
      int h = getHeight();
      int filledHeight = Math.max(1, (int) Math.min(h, (fes.getAmount() * h / getStack().getAmount())));
      int sy = floaty ? y : y + h;
      for (int oy = 0; oy < filledHeight; oy += 16) {
        int rh = Math.min(16, filledHeight - oy);
        for (int ox = 0; ox < w; ox += 16) {
          int rw = Math.min(16, w - ox);
          if (floaty) {
            EmiAgnos.renderFluid(fes, graphics.pose(), x + ox, sy + oy, 0, 0, 0, rw, rh);
          } else {
            EmiAgnos.renderFluid(fes, graphics.pose(), x + ox, sy + (oy + rh) * -1, 0, 0, 16 - rh, rw, rh);
          }
        }
      }
    }
    graphics.pose().popPose();
  }
}
