package es.degrassi.mmreborn.common.integration.jei.category;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiComponent;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MMRRecipeCategory implements IRecipeCategory<MachineRecipe> {
  private final DynamicMachine machine;
  private final String title;
  private final IDrawable background, icon;

  public final int gapX = 8, gapY = 8;
  public final int initialX = 8, initialXOut = getArrowEndPos() + gapX, initialY = 8;
  public final AtomicInteger x = new AtomicInteger(0), y = new AtomicInteger(0);
  public final AtomicInteger maxHeight = new AtomicInteger(initialY);
  public final List<Component> textsToRender = new LinkedList<>();
  public final List<MMRRecipeCategory.ComponentValue> processedInputComponents = new LinkedList<>();
  public final List<MMRRecipeCategory.ComponentValue> processedOutputComponents = new LinkedList<>();
  public final AtomicReference<MMRRecipeCategory.ComponentValue> firstItem = new AtomicReference<>(null);

  public List<ComponentRequirement<?, ?>> requirements;
  public List<ComponentRequirement<?, ?>> inputRequirements;
  public List<ComponentRequirement<?, ?>> outputRequirements;

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
    inputRequirements = new ArrayList<>(requirements.stream().filter(req -> req.getActionType().isInput()).toList()).stream().sorted().toList();
    outputRequirements = new ArrayList<>(requirements.stream().filter(req -> !req.getActionType().isInput()).toList()).stream().sorted().toList();
    x.set(initialX);
    y.set(initialY);
    processedInputComponents.clear();
    processedOutputComponents.clear();
    textsToRender.clear();
    maxHeight.set(x.get());

    textsToRender.add(
      Component.translatable(
        "modular_machinery_reborn.jei.ingredient.duration",
        recipe.getRecipeTotalTickTime()
      )
    );

    firstItem.set(null);
    inputRequirements.forEach(component -> component.jeiComponent().setRecipeInput(this, builder, recipe, focuses));

    x.set(initialXOut);
    y.set(initialY);

    firstItem.set(null);
    outputRequirements.forEach(component -> component.jeiComponent().setRecipeOutput(this, builder, recipe, focuses));

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

  public void updateMaxHeightInput(JeiComponent<?, ?> component, boolean useGaps) {
    if (y.get() + component.getHeight() > maxHeight.get()) maxHeight.set(y.get() + component.getHeight() + gapY(useGaps));
    if (x.get() >= (getArrowPos() - gapX)) {
      x.set(initialX);
      y.getAndAdd(component.getHeight() + gapY(useGaps));
    }
  }

  public void updateMaxHeightOutput(JeiComponent<?, ?> component, boolean useGaps) {
    if (y.get() + component.getHeight() > maxHeight.get()) maxHeight.set(y.get() + component.getHeight() + gapY(useGaps));
    if (x.get() >= (getMaxWidth() - gapX)) {
      x.set(initialXOut);
      y.getAndAdd(component.getHeight() + gapY(useGaps));
    }
  }

  public int gapY(boolean useGaps) {
    return useGaps ? gapY : 0;
  }

  public int gapX(boolean useGaps) {
    return useGaps ? gapX : 0;
  }

  public void updateByProcessed(List<ComponentValue> processedComponents, int width, int height, boolean useGaps) {
    processedComponents.stream().filter(entry -> entry.x >= x.get()
        && y.get() >= entry.y
        && y.get() <= entry.height + entry.y
      )
      .min(Comparator.comparingInt(entry -> entry.x))
      .ifPresent(entry -> x.set(entry.x + entry.height + gapX(useGaps)));
    processedComponents.stream().filter(entry -> entry.y >= y.get()
        && x.get() >= entry.x
        && x.get() <= entry.width + entry.x
      )
      .min(Comparator.comparingInt(entry -> entry.y))
      .ifPresent(entry -> y.set(entry.y + entry.height + gapY(useGaps)));
    processedComponents.add(new ComponentValue(x.get(), y.get(), width, height));
  }

  public int getArrowPos() {
    return getMaxWidth() / 2 - getHalfArrowWidth();
  }

  public int getArrowEndPos() {
    return getMaxWidth() / 2 + getHalfArrowWidth() * 3;
  }

  public int getHalfArrowWidth() {
    return MMRJeiPlugin.jeiHelpers.getGuiHelper().getRecipeArrowFilled().getWidth() / 4;
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, @NotNull MachineRecipe recipe, @NotNull IFocusGroup focuses) {
    builder.addAnimatedRecipeArrow(20)
      .setPosition(getArrowPos(), 8);
  }

  public record ComponentValue(int x, int y, int width, int height) {}
}
