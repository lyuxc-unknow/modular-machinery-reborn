package es.degrassi.mmreborn.common.crafting.requirement.jei;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

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
  public void setRecipeInput(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    if (category.firstItem.get() == null) {
      category.firstItem.set(new MMRRecipeCategory.ComponentValue(category.x.get(), category.y.get(), getWidth(), getHeight()));
    }
    category.processedInputComponents.add(new MMRRecipeCategory.ComponentValue(category.x.get(), category.y.get(), getWidth(), getHeight()));
    builder
      .addInputSlot(category.x.get() + 1, category.y.get() + 1)
      .addItemStacks(ingredients())
      .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("modular_machinery_reborn.jei.ingredient.item.input")))
      .setStandardSlotBackground();
    category.x.getAndAdd(getWidth());
    if (category.y.get() + getHeight() > category.maxHeight.get()) category.maxHeight.set(category.y.get() + getHeight());
    if (category.x.get() >= (category.getArrowPos() - category.gapX)) {
      category.x.set(category.firstItem.get().x());
      category.y.getAndAdd(getHeight());
    }
  }

  @Override
  public void setRecipeOutput(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    if (category.firstItem.get() == null) {
      category.firstItem.set(new MMRRecipeCategory.ComponentValue(category.x.get(), category.y.get(), getWidth(), getHeight()));
    }
    category.processedOutputComponents.add(new MMRRecipeCategory.ComponentValue(category.x.get(), category.y.get(), getWidth(), getHeight()));
    builder
      .addOutputSlot(category.x.get() + 1, category.y.get() + 1)
      .addItemStacks(ingredients())
      .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("modular_machinery_reborn.jei.ingredient.item.output")))
      .setStandardSlotBackground();
    category.x.getAndAdd(getWidth());
    if (category.y.get() + getHeight() > category.maxHeight.get()) category.maxHeight.set(category.y.get() + getHeight());
    if (category.x.get() >= category.getMaxWidth() - category.gapX) {
      category.x.set(category.firstItem.get().x());
      category.y.getAndAdd(getHeight());
    }
  }
}
