package es.degrassi.mmreborn.common.crafting.requirement.emi;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.screen.EmiScreenManager;
import es.degrassi.mmreborn.client.requirement.ItemRendering;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.util.Utils;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EmiItemComponent extends EmiComponent<ItemStack, RequirementItem> implements SlotTooltip, ItemRendering {
  private int item;
  @Getter
  private int width = 16, height = 16;
  @Getter
  private EmiRecipe recipe;
  public EmiItemComponent(RequirementItem requirement) {
    super(requirement, 36, 0);
  }

  public void recipeContext(EmiRecipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public EmiStack getStack() {
    return EmiStack.of(ingredients().get(item));
  }

  @Override
  public List<ItemStack> ingredients() {
    return Arrays.stream(requirement.getIngredient().getItems()).map(ItemStack::copy).toList();
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    item = (int) (System.currentTimeMillis() / 1000 % ingredients().size());
    width += 2;
    height += 2;
    super.render(guiGraphics, mouseX, mouseY);
    width -= 2;
    height -= 2;
    drawStack(guiGraphics, 1, 1, -1);
  }

  @Override
  public List<Component> getTooltip() {
    List<Component> list = Lists.newArrayList();
    String chance = Utils.decimalFormat(requirement.chance * 100);
    if (requirement.chance > 0 && requirement.chance < 1)
      list.add(Component.translatable("modular_machinery_reborn.ingredient.chance." + requirement.getActionType().name().toLowerCase(Locale.ROOT), chance, "%"));
    else if (requirement.chance == 0)
      list.add(Component.translatable("modular_machinery_reborn.ingredient.chance.not_consumed"));
    else if (requirement.chance == 1)
      list.add(Component.translatable("modular_machinery_reborn.jei.ingredient.item." + requirement.getActionType().name().toLowerCase(Locale.ROOT)));
    if (getStack().isEmpty()) {
      return list;
    }
    list.addAll(getStack().getTooltipText());
    return list;
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int button) {
    if (slotInteraction(bind -> bind.matchesMouse(button))) {
      return true;
    }
    return EmiScreenManager.stackInteraction(new EmiStackInteraction(getStack(), getRecipe(), true),
        bind -> bind.matchesMouse(button));
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (slotInteraction(bind -> bind.matchesKey(keyCode, scanCode))) {
      return true;
    }
    return EmiScreenManager.stackInteraction(new EmiStackInteraction(getStack(), getRecipe(), true),
        bind -> bind.matchesKey(keyCode, scanCode));
  }
}
