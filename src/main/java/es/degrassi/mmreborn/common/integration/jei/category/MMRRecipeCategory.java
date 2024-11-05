package es.degrassi.mmreborn.common.integration.jei.category;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.Registration;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.gui.elements.DrawableWrappedText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MMRRecipeCategory implements IRecipeCategory<MachineRecipe> {
  private final DynamicMachine machine;
  private final String title;
  private final IDrawable background, icon;

  public final int initialX = 8, gap = 8;

  public List<ComponentRequirement<?, ?>> requirements;

  public MMRRecipeCategory(DynamicMachine machine) {
    this.machine = machine;
    this.title = machine.getLocalizedName();
    this.background = MMRJeiPlugin.jeiHelpers.getGuiHelper().createBlankDrawable(getMaxWidth(), 256);
    ItemStack stack = new ItemStack(ItemRegistration.CONTROLLER.get());
    stack.set(Registration.MACHINE_DATA, machine.getRegistryName());
    this.icon = MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
  }

  public int getMaxWidth() {
    Minecraft minecraft = Minecraft.getInstance();
    Font fontRenderer = minecraft.font;
    Component maxSmeltCountText = createSmeltCountText();
    int maxStringWidth = fontRenderer.width(maxSmeltCountText.getString());
    int textPadding = 20;
    return 18 + textPadding + maxStringWidth;
  }

  private static Component createSmeltCountText() {
    NumberFormat numberInstance = NumberFormat.getNumberInstance();
    numberInstance.setMaximumFractionDigits(2);
    return Component.translatable("gui.jei.category.fuel.smeltCount", numberInstance.format(10000000 * 200 / 200f));
  }

  @Nullable
  @Override
  @SuppressWarnings("removal")
  public IDrawable getBackground() {
    return background;
  }

  @Override
  public @NotNull RecipeType<MachineRecipe> getRecipeType() {
    return RecipeType.create(ModularMachineryReborn.MODID, machine.getRegistryName().getPath(), MachineRecipe.class);
  }

  @Override
  public @NotNull Component getTitle() {
    return Component.literal(title);
  }

  @Override
  public int getWidth() {
    return background.getWidth();
  }

  @Override
  public int getHeight() {
    return background.getHeight();
  }

  @Override
  public @Nullable IDrawable getIcon() {
    return icon;
  }

  @Override
  public void setRecipe(@NotNull IRecipeLayoutBuilder builder, MachineRecipe recipe, @NotNull IFocusGroup focuses) {
    requirements = recipe.getCraftingRequirements();
    final List<Component> textsToRender = new LinkedList<>();

    textsToRender.add(
      Component.translatable(
        "modular_machinery_reborn.jei.ingredient.duration",
        recipe.getRecipeTotalTickTime()
      )
    );

    requirements.forEach(component -> component.jeiComponent().setRecipe(this, builder, recipe, focuses));
    AtomicInteger maxHeight = new AtomicInteger(0);
    requirements.forEach(component -> {
      if (component.getPosition().y() + component.jeiComponent().getHeight() >= maxHeight.get())
        maxHeight.set(component.getPosition().y() + component.jeiComponent().getHeight() + gap);
    });

    Font font = Minecraft.getInstance().font;
    textsToRender.forEach(component -> {
      builder.addSlot(RecipeIngredientRole.RENDER_ONLY, initialX, maxHeight.get())
        .setBackground(
          new DrawableWrappedText(List.of(component), getMaxWidth() - 16),
          0,
          0
        );
      maxHeight.getAndAdd(font.wordWrapHeight(component, getMaxWidth() - 16) + 2);
    });
  }

  public int getArrowPos() {
    return getMaxWidth() / 2 - MMRJeiPlugin.jeiHelpers.getGuiHelper().getRecipeArrowFilled().getWidth() / 4;
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, @NotNull MachineRecipe recipe, @NotNull IFocusGroup focuses) {
    builder.addAnimatedRecipeArrow(20)
      .setPosition(getArrowPos(), 8);
  }
}
