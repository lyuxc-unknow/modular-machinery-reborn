package es.degrassi.mmreborn.common.crafting.requirement.jei;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

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
    return Lists.newArrayList(new ItemStack(requirement.ingredient.getAll().get(0), requirement.amount));
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    builder.addSlot(role(), getPosition().x(), getPosition().y())
        .addItemStacks(ingredients())
        .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("modular_machinery_reborn.jei.ingredient.item.input")))
        .setStandardSlotBackground();
  }
}
