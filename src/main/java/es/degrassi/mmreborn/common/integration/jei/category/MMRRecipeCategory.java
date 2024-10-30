package es.degrassi.mmreborn.common.integration.jei.category;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementChemical;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFluid;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiEnergyComponent;
import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmreborn.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.Registration;
import mekanism.api.chemical.ChemicalStack;
import mekanism.client.recipe_viewer.jei.ChemicalStackRenderer;
import mekanism.client.recipe_viewer.jei.MekanismJEI;
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

  public MMRRecipeCategory(DynamicMachine machine) {
    this.machine = machine;
    this.title = machine.getLocalizedName();
    this.background = MMRJeiPlugin.jeiHelpers.getGuiHelper().createBlankDrawable(getMaxWidth(), 256);
    ItemStack stack = new ItemStack(ItemRegistration.CONTROLLER.get());
    stack.set(Registration.MACHINE_DATA, machine.getRegistryName());
    this.icon = MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
  }

  private static int getMaxWidth() {
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
    List<ComponentRequirement<?, ?>> requirements = recipe.getCraftingRequirements();
    List<ComponentRequirement<?, ?>> inputRequirements = new ArrayList<>(requirements.stream().filter(req -> req.getActionType().isInput()).toList());
    List<ComponentRequirement<?, ?>> outputRequirements = new ArrayList<>(requirements.stream().filter(req -> !req.getActionType().isInput()).toList());
    final int gapX = 8, gapY = 8;
    final int initialX = 8, initialXOut = getMaxWidth() / 2 + 22 + gapX, initialY = 8;

    AtomicInteger x = new AtomicInteger(initialX), y = new AtomicInteger(initialY);

    List<MMRRecipeCategory.ComponentValue> processedInputComponents = new LinkedList<>();
    List<MMRRecipeCategory.ComponentValue> processedOutputComponents = new LinkedList<>();
    final List<Component> textsToRender = new LinkedList<>();
    final AtomicInteger maxHeight = new AtomicInteger(initialY);
    textsToRender.add(
        Component.translatable(
            "modular_machinery_reborn.jei.ingredient.duration",
            recipe.getRecipeTotalTickTime()
        )
    );

    inputRequirements.stream().filter(req -> req instanceof RequirementEnergy).map(req -> (RequirementEnergy) req).map(RequirementEnergy::jeiComponent).forEach(component -> {
      addEnergyComponent(builder, gapX, x, y, processedInputComponents, component);
      textsToRender.add(
          Component.translatable(
              "modular_machinery_reborn.jei.ingredient.energy.total.input",
              component.ingredients().get(0) * recipe.getRecipeTotalTickTime(),
              component.ingredients().get(0)
          )
      );
      if (y.get() + component.getHeight() > maxHeight.get()) maxHeight.set(y.get() + component.getHeight() + gapY);
      if (x.get() >= (getMaxWidth() / 2 - gapX)) {
        x.set(initialX);
        y.getAndAdd(component.getHeight() + gapY);
      }
    });
    inputRequirements.stream().filter(req -> req instanceof RequirementFluid).map(req -> (RequirementFluid) req).map(RequirementFluid::jeiComponent).forEach(component -> {
      updateByProcessed(x, y, processedInputComponents, component.getWidth(), component.getHeight());
      builder
          .addInputSlot(x.get() + 1, y.get() + 1)
          .setOverlay(
              MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawable(component.texture(), component.getUOffset(), component.getVOffset(), component.getWidth() + 2, component.getHeight() + 2),
              -1,
              -1
          )
          .setFluidRenderer(component.getRequirement().amount, false, component.getWidth(), component.getHeight())
          .addFluidStack(component.getRequirement().required.asFluidStack().getFluid(), component.getRequirement().amount);
      x.getAndAdd(gapX);
      x.getAndAdd(component.getWidth());
      textsToRender.add(Component.translatable("modular_machinery_reborn.jei.ingredient.fluid.input", component.ingredients().get(0).getHoverName(), component.ingredients().get(0).getAmount()));
      if (y.get() + component.getHeight() > maxHeight.get()) maxHeight.set(y.get() + component.getHeight() + gapY);
      if (x.get() >= (getMaxWidth() / 2 - gapX)) {
        x.set(initialX);
        y.getAndAdd(component.getHeight() + gapY);
      }
    });
    inputRequirements.stream().filter(req -> req instanceof RequirementChemical).map(req -> (RequirementChemical) req).map(RequirementChemical::jeiComponent).forEach(component -> {
      updateByProcessed(x, y, processedInputComponents, component.getWidth(), component.getHeight());
      builder
          .addInputSlot(x.get() + 1, y.get() + 1)
          .setOverlay(
              MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawable(component.texture(), component.getUOffset(), component.getVOffset(), component.getWidth() + 2, component.getHeight() + 2),
              -1,
              -1
          )
          .setCustomRenderer(MekanismJEI.TYPE_CHEMICAL, new ChemicalStackRenderer(component.getRequirement().amount, component.getWidth(), component.getHeight()))
          .addIngredient(MekanismJEI.TYPE_CHEMICAL, new ChemicalStack(component.getRequirement().required.getChemical(), component.getRequirement().amount));
      x.getAndAdd(gapX);
      x.getAndAdd(component.getWidth());
      textsToRender.add(Component.translatable("modular_machinery_reborn.jei.ingredient.chemical.input", component.ingredients().get(0).getTextComponent(), component.ingredients().get(0).getAmount()));
      if (y.get() + component.getHeight() > maxHeight.get()) maxHeight.set(y.get() + component.getHeight() + gapY);
      if (x.get() >= (getMaxWidth() / 2 - gapX)) {
        x.set(initialX);
        y.getAndAdd(component.getHeight() + gapY);
      }
    });
    final AtomicReference<MMRRecipeCategory.ComponentValue> firstItem = new AtomicReference<>(null);
    x.getAndIncrement();
    y.getAndIncrement();
    inputRequirements.stream().filter(req -> req instanceof RequirementItem).map(req -> (RequirementItem) req).map(RequirementItem::jeiComponent).forEach(component -> {
      if (firstItem.get() == null)
        firstItem.set(new MMRRecipeCategory.ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
      processedInputComponents.add(new MMRRecipeCategory.ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
      builder
          .addInputSlot(x.get(), y.get())
          .addItemStacks(component.ingredients())
          .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("modular_machinery_reborn.jei.ingredient.item.input")))
          .setStandardSlotBackground();
      x.getAndAdd(component.getWidth());
      if (y.get() + component.getHeight() > maxHeight.get()) maxHeight.set(y.get() + component.getHeight() + gapY);
      if (x.get() >= (getMaxWidth() / 2 - gapX)) {
        x.set(firstItem.get().x);
        y.getAndAdd(component.getHeight());
      }
    });

    x.set(initialXOut);
    y.set(initialY);

    outputRequirements.stream().filter(req -> req instanceof RequirementEnergy).map(req -> (RequirementEnergy) req).map(RequirementEnergy::jeiComponent).forEach(component -> {
      addEnergyComponent(builder, gapX, x, y, processedOutputComponents, component);
      textsToRender.add(
          Component.translatable(
              "modular_machinery_reborn.jei.ingredient.energy.total.output",
              component.ingredients().get(0) * recipe.getRecipeTotalTickTime(),
              component.ingredients().get(0)
          )
      );
      updateMaxHeight(gapX, gapY, initialXOut, x, y, maxHeight, component.getHeight());
    });
    outputRequirements.stream().filter(req -> req instanceof RequirementFluid).map(req -> (RequirementFluid) req).map(RequirementFluid::jeiComponent).forEach(component -> {
      updateByProcessed(x, y, processedOutputComponents, component.getWidth(), component.getHeight());
      builder
          .addInputSlot(x.get() + 1, y.get() + 1)
          .setOverlay(
              MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawable(component.texture(), component.getUOffset(), component.getVOffset(), component.getWidth() + 2, component.getHeight() + 2),
              -1,
              -1
          )
          .setFluidRenderer(component.getRequirement().amount, false, component.getWidth(), component.getHeight())
          .addFluidStack(component.getRequirement().required.asFluidStack().getFluid(), component.getRequirement().amount);
      x.getAndAdd(gapX);
      x.getAndAdd(component.getWidth());
      textsToRender.add(Component.translatable("modular_machinery_reborn.jei.ingredient.fluid.output", component.ingredients().get(0).getHoverName(), component.ingredients().get(0).getAmount()));
      updateMaxHeight(gapX, gapY, initialX, x, y, maxHeight, component.getHeight());
    });
    outputRequirements.stream().filter(req -> req instanceof RequirementChemical).map(req -> (RequirementChemical) req).map(RequirementChemical::jeiComponent).forEach(component -> {
      updateByProcessed(x, y, processedOutputComponents, component.getWidth(), component.getHeight());
      builder
          .addInputSlot(x.get() + 1, y.get() + 1)
          .setOverlay(
              MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawable(component.texture(), component.getUOffset(), component.getVOffset(), component.getWidth() + 2, component.getHeight() + 2),
              -1,
              -1
          )
          .setCustomRenderer(MekanismJEI.TYPE_CHEMICAL, new ChemicalStackRenderer(component.getRequirement().amount, component.getWidth(), component.getHeight()))
          .addIngredient(MekanismJEI.TYPE_CHEMICAL, new ChemicalStack(component.getRequirement().required.getChemical(), component.getRequirement().amount));
      x.getAndAdd(gapX);
      x.getAndAdd(component.getWidth());
      textsToRender.add(Component.translatable("modular_machinery_reborn.jei.ingredient.chemical.output", component.ingredients().get(0).getTextComponent(), component.ingredients().get(0).getAmount()));
      updateMaxHeight(gapX, gapY, initialX, x, y, maxHeight, component.getHeight());
    });
    firstItem.set(null);
    x.getAndIncrement();
    y.getAndIncrement();
    outputRequirements.stream().filter(req -> req instanceof RequirementItem).map(req -> (RequirementItem) req).map(RequirementItem::jeiComponent).forEach(component -> {
      if (firstItem.get() == null)
        firstItem.set(new MMRRecipeCategory.ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
      processedOutputComponents.add(new MMRRecipeCategory.ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
      builder
          .addOutputSlot(x.get(), y.get())
          .addItemStacks(component.ingredients())
          .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("modular_machinery_reborn.jei.ingredient.item.output")))
          .setStandardSlotBackground();
      x.getAndAdd(component.getWidth());
      if (y.get() + component.getHeight() > maxHeight.get()) maxHeight.set(y.get() + component.getHeight() + gapY);
      if (x.get() >= getMaxWidth()) {
        x.set(firstItem.get().x);
        y.getAndAdd(component.getHeight());
      }
    });
    Font font = Minecraft.getInstance().font;
    textsToRender.forEach(component -> {
      builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 8, maxHeight.get())
          .setBackground(
              new DrawableWrappedText(List.of(component), getMaxWidth() - 16),
              0,
              0
          );
      maxHeight.getAndAdd(font.wordWrapHeight(component, getMaxWidth() - 16) + 2);
    });
  }

  private void addEnergyComponent(@NotNull IRecipeLayoutBuilder builder, int gapX, AtomicInteger x, AtomicInteger y, List<ComponentValue> processedComponents, JeiEnergyComponent component) {
    updateByProcessed(x, y, processedComponents, component.getWidth(), component.getHeight());
    builder
        .addSlot(RecipeIngredientRole.RENDER_ONLY, x.get() + 1, y.get() + 1)
        .setCustomRenderer(CustomIngredientTypes.ENERGY, component)
        .addIngredients(CustomIngredientTypes.ENERGY, component.ingredients());
    x.getAndAdd(gapX);
    x.getAndAdd(component.getWidth());
  }

  private void updateMaxHeight(int gapX, int gapY, int initialX, AtomicInteger x, AtomicInteger y, AtomicInteger maxHeight, int height) {
    if (y.get() + height > maxHeight.get()) maxHeight.set(y.get() + height + gapY);
    if (x.get() >= (getMaxWidth() / 2 - 11 - gapX)) {
      x.set(initialX);
      y.getAndAdd(height + gapY);
    }
  }

  private void updateByProcessed(AtomicInteger x, AtomicInteger y, List<ComponentValue> processedComponents, int width, int height) {
    processedComponents.stream().filter(entry -> entry.x >= x.get()
            && y.get() >= entry.y
            && y.get() <= entry.height + entry.y
        )
        .min(Comparator.comparingInt(entry -> entry.x))
        .ifPresent(entry -> x.set(entry.x + entry.height + 8));
    processedComponents.stream().filter(entry -> entry.y >= y.get()
            && x.get() >= entry.x
            && x.get() <= entry.width + entry.x
        )
        .min(Comparator.comparingInt(entry -> entry.y))
        .ifPresent(entry -> y.set(entry.y + entry.height + 8));
    processedComponents.add(new ComponentValue(x.get(), y.get(), width, height));
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, MachineRecipe recipe, @NotNull IFocusGroup focuses) {
    builder.addAnimatedRecipeArrow(20)
        .setPosition(getMaxWidth() / 2, 8);
  }

  private record ComponentValue(int x, int y, int width, int height) {}
}
