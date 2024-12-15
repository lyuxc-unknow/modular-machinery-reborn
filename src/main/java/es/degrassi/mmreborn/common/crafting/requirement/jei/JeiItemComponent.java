package es.degrassi.mmreborn.common.crafting.requirement.jei;

import com.mojang.datafixers.util.Pair;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedSizedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.integration.jei.category.drawable.DrawableWrappedText;
import es.degrassi.mmreborn.common.util.Utils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
    Component component = Component.empty();
    String chance = Utils.decimalFormat(requirement.chance * 100);
    if (requirement.chance > 0 && requirement.chance < 1)
      component = Component.translatable("modular_machinery_reborn.ingredient.chance", chance, "%").withColor(Config.chanceColor);
    else if (requirement.chance == 0)
      component = Component.translatable("modular_machinery_reborn.ingredient.chance.nc").withColor(Config.chanceColor);
    Font font = Minecraft.getInstance().font;
    recipe.chanceTexts.add(
        Pair.of(
            new PositionedSizedRequirement(
                getPosition().x(),
                getPosition().y(),
                getWidth(),
                font.wordWrapHeight(component, getWidth())
            ),
            new DrawableWrappedText(List.of(component), getWidth() + 2, true)
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.TRANSLATEX, getPosition().x())
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.TRANSLATEY, getPosition().y())
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.SCALE, 0.75)
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.TRANSLATEZ, 500)
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.TRANSLATEX, (double) (getWidth() - 16) / 2)
                .transform(DrawableWrappedText.Operation.ADD, DrawableWrappedText.State.TRANSLATEX, 17)
                .transform(DrawableWrappedText.Operation.REMOVE, DrawableWrappedText.State.TRANSLATEX, Math.min(14, font.width(component)))
                .transform(DrawableWrappedText.Operation.SET, DrawableWrappedText.State.TRANSLATEY, (double) (getHeight() - 16) / 2)
                .transform(DrawableWrappedText.Operation.MULTIPLY, DrawableWrappedText.State.TRANSLATEY, -1)
                .transform(DrawableWrappedText.Operation.REMOVE, DrawableWrappedText.State.TRANSLATEY, 2)
        )
    );

    builder.addSlot(role(), getPosition().x(), getPosition().y())
        .addItemStacks(ingredients())
        .addRichTooltipCallback((view, tooltip) -> {
          if (requirement.chance > 0 && requirement.chance < 1)
            tooltip.add(Component.translatable("modular_machinery_reborn.ingredient.chance." + requirement.getActionType().name().toLowerCase(Locale.ROOT), chance, "%"));
          else if (requirement.chance == 0)
            tooltip.add(Component.translatable("modular_machinery_reborn.ingredient.chance.not_consumed"));
          else if (requirement.chance == 1)
            tooltip.add(Component.translatable("modular_machinery_reborn.jei.ingredient.item." + requirement.getActionType().name().toLowerCase(Locale.ROOT)));
        })
        .setStandardSlotBackground();
  }
}
