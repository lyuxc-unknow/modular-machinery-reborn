package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.util.Utils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class JeiItemComponent extends JeiComponent<ItemStack, RequirementItem> {
  public JeiItemComponent(RequirementItem requirement) {
    super(requirement, 36, 0);
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
  public List<ItemStack> ingredients() {
    return Arrays.stream(requirement.getIngredient().getItems()).map(ItemStack::copy).toList();
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    builder.addSlot(role(), getPosition().x(), getPosition().y())
        .addItemStacks(ingredients())
        .addRichTooltipCallback((view, tooltip) -> {
          String chance = Utils.decimalFormat(requirement.chance * 100);
          if (requirement.chance > 0 && requirement.chance < 1)
            tooltip.add(Component.translatable("modular_machinery_reborn.ingredient.chance." + requirement.getActionType().name().toLowerCase(Locale.ROOT), chance, "%"));
          else if (requirement.chance == 0)
            tooltip.add(Component.translatable("modular_machinery_reborn.ingredient.chance.not_consumed"));
          else if (requirement.chance == 1)
            tooltip.add(Component.translatable("modular_machinery_reborn.jei" +
                ".ingredient.item." + requirement.getActionType().name().toLowerCase(Locale.ROOT)));
        })
        .setStandardSlotBackground();
  }
}
