package es.degrassi.mmreborn.client.requirement;

import dev.emi.emi.runtime.EmiDrawContext;
import es.degrassi.mmreborn.common.crafting.requirement.emi.Position;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

public interface ChanceRendering extends Position {
  Minecraft CLIENT = Minecraft.getInstance();
  float getChance();
  IOType getActionType();

  default void drawChance(GuiGraphics graphics, boolean top) {
    Component component = Component.empty();
    String c = Utils.decimalFormat(getChance() * 100);
    if (getChance() > 0 && getChance() < 1)
      component = Component.translatable("modular_machinery_reborn.ingredient.chance", c, "%");
    else if (getChance() == 0)
      component = Component.translatable("modular_machinery_reborn.ingredient.chance.nc");
    int xOff = (getWidth() - 16) / 2 + 2;
    int yOff = (getHeight() - 16) / 2;
    int modifier = top ? -1 : 1;
    yOff *= modifier;
    int modified = top ? 1 : 2;
    yOff += modified;
    EmiDrawContext context = EmiDrawContext.wrap(graphics);
    renderAmount(context, xOff, yOff, top, component);
  }

  private static void renderAmount(EmiDrawContext context, int x, int y, boolean top, Component amount) {
    context.push();
    context.matrices().translate(0, 0, 200);
    context.matrices().scale(0.75f, 0.75f, 0.75f);
    int tx = x + 17 - Math.min(14, CLIENT.font.width(amount));
    y += top ? 0 : CLIENT.font.lineHeight * 3/2;
    context.drawTextWithShadow(amount, tx, y, Config.chanceColor);
    context.pop();
  }

  default void addChanceTooltips(List<Component> list) {
    String chance = Utils.decimalFormat(getChance() * 100);
    if (getChance() > 0 && getChance() < 1)
      list.add(Component.translatable("modular_machinery_reborn.ingredient.chance." + getActionType().name().toLowerCase(Locale.ROOT), chance, "%"));
    else if (getChance() == 0)
      list.add(Component.translatable("modular_machinery_reborn.ingredient.chance.not_consumed"));
    else if (getChance() == 1)
      list.add(Component.translatable("modular_machinery_reborn.jei.ingredient.item." + getActionType().name().toLowerCase(Locale.ROOT)));
  }
}
