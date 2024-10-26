package es.degrassi.mmreborn.common.integration.jei.category;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFluid;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementItem;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiComponent;
import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmreborn.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.Registration;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.gui.elements.DrawableBlank;
import mezz.jei.common.gui.elements.DrawableText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.FastColor;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMRRecipeCategory implements IRecipeCategory<MachineRecipe> {
  private final DynamicMachine machine;
  private final String title;
  private final IDrawable background, icon;
  private final List<Component> textsToRender = new LinkedList<>();
  private final AtomicInteger maxHeight = new AtomicInteger(0);

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

    List<ComponentValue> processedInputComponents = new LinkedList<>();
    List<ComponentValue> processedOutputComponents = new LinkedList<>();
    textsToRender.clear();
    maxHeight.set(initialY);
    textsToRender.add(
      Component.translatable(
        "modular_machinery_reborn.jei.ingredient.duration",
        recipe.getRecipeTotalTickTime()
      )
    );

    inputRequirements.stream().filter(req -> req instanceof RequirementEnergy).map(req -> (RequirementEnergy) req).map(RequirementEnergy::jeiComponent).forEach(component -> {
      processedInputComponents.stream().filter(entry -> entry.x >= x.get()
          && y.get() >= entry.y
          && y.get() <= entry.height + entry.y
        )
        .min(Comparator.comparingInt(entry -> entry.x))
        .ifPresent(entry -> x.set(entry.x + entry.width + gapX));
      processedInputComponents.stream().filter(entry -> entry.y >= y.get()
          && x.get() >= entry.x
          && x.get() <= entry.width + entry.y
        )
        .min(Comparator.comparingInt(entry -> entry.y))
        .ifPresent(entry -> y.set(entry.y + entry.height + gapY));
      processedInputComponents.add(new ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
      builder
        .addSlot(RecipeIngredientRole.RENDER_ONLY, x.get() + 1, y.get() + 1)
        .setCustomRenderer(CustomIngredientTypes.ENERGY, component)
        .addIngredients(CustomIngredientTypes.ENERGY, component.ingredients());
      x.getAndAdd(gapX);
      x.getAndAdd(component.getWidth());
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
      processedInputComponents.stream().filter(entry -> entry.x >= x.get()
          && y.get() >= entry.y
          && y.get() <= entry.height + entry.y
        )
        .min(Comparator.comparingInt(entry -> entry.x))
        .ifPresent(entry -> x.set(entry.x + entry.width + gapX));
      processedInputComponents.stream().filter(entry -> entry.y >= y.get()
          && x.get() >= entry.x
          && x.get() <= entry.width + entry.y
        )
        .min(Comparator.comparingInt(entry -> entry.y))
        .ifPresent(entry -> y.set(entry.y + entry.height + gapY));
      processedInputComponents.add(new ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
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
    final AtomicReference<ComponentValue> firstItem = new AtomicReference<>(null);
    x.getAndIncrement();
    y.getAndIncrement();
    inputRequirements.stream().filter(req -> req instanceof RequirementItem).map(req -> (RequirementItem) req).map(RequirementItem::jeiComponent).forEach(component -> {
      if (firstItem.get() == null)
        firstItem.set(new ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
      processedInputComponents.add(new ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
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
      processedOutputComponents.stream().filter(entry -> entry.x >= x.get()
          && y.get() >= entry.y
          && y.get() <= entry.height + entry.y
        )
        .min(Comparator.comparingInt(entry -> entry.x))
        .ifPresent(entry -> x.set(entry.x + entry.height + gapX));
      processedOutputComponents.stream().filter(entry -> entry.y >= y.get()
          && x.get() >= entry.x
          && x.get() <= entry.width + entry.x
        )
        .min(Comparator.comparingInt(entry -> entry.y))
        .ifPresent(entry -> y.set(entry.y + entry.height + gapY));
      processedOutputComponents.add(new ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
      builder
        .addSlot(RecipeIngredientRole.RENDER_ONLY, x.get() + 1, y.get() + 1)
        .setCustomRenderer(CustomIngredientTypes.ENERGY, component)
        .addIngredients(CustomIngredientTypes.ENERGY, component.ingredients());
      x.getAndAdd(gapX);
      x.getAndAdd(component.getWidth());
      textsToRender.add(
        Component.translatable(
          "modular_machinery_reborn.jei.ingredient.energy.total.output",
          component.ingredients().get(0) * recipe.getRecipeTotalTickTime(),
          component.ingredients().get(0)
        )
      );
      if (y.get() + component.getHeight() > maxHeight.get()) maxHeight.set(y.get() + component.getHeight() + gapY);
      if (x.get() >= (getMaxWidth() / 2 - 11 - gapX)) {
        x.set(initialXOut);
        y.getAndAdd(component.getHeight() + gapY);
      }
    });
    outputRequirements.stream().filter(req -> req instanceof RequirementFluid).map(req -> (RequirementFluid) req).map(RequirementFluid::jeiComponent).forEach(component -> {
      processedOutputComponents.stream().filter(entry -> entry.x >= x.get()
          && y.get() >= entry.y
          && y.get() <= entry.height + entry.y
        )
        .min(Comparator.comparingInt(entry -> entry.x))
        .ifPresent(entry -> x.set(entry.x + entry.height + gapX));
      processedOutputComponents.stream().filter(entry -> entry.y >= y.get()
          && x.get() >= entry.x
          && x.get() <= entry.width + entry.x
        )
        .min(Comparator.comparingInt(entry -> entry.y))
        .ifPresent(entry -> y.set(entry.y + entry.height + gapY));
      processedOutputComponents.add(new ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
      builder
        .addInputSlot(x.get() + 1, y.get() + 1)
        .setOverlay(
          MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawable(component.texture(), component.getUOffset(), component.getVOffset(), component.getWidth() + 2, component.getHeight() + 2),
          -1,
          -1
        )
        .setFluidRenderer(component.getRequirement().amount, true, component.getWidth(), component.getHeight())
        .addFluidStack(component.getRequirement().required.asFluidStack().getFluid(), component.getRequirement().amount);
      x.getAndAdd(gapX);
      x.getAndAdd(component.getWidth());
      textsToRender.add(Component.translatable("modular_machinery_reborn.jei.ingredient.fluid.output", component.ingredients().get(0).getHoverName(), component.ingredients().get(0).getAmount()));
      if (y.get() + component.getHeight() > maxHeight.get()) maxHeight.set(y.get() + component.getHeight() + gapY);
      if (x.get() >= (getMaxWidth() / 2 - 11 - gapX)) {
        x.set(initialX);
        y.getAndAdd(component.getHeight() + gapY);
      }
    });
    firstItem.set(null);
    x.getAndIncrement();
    y.getAndIncrement();
    outputRequirements.stream().filter(req -> req instanceof RequirementItem).map(req -> (RequirementItem) req).map(RequirementItem::jeiComponent).forEach(component -> {
      if (firstItem.get() == null)
        firstItem.set(new ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
      processedOutputComponents.add(new ComponentValue(x.get(), y.get(), component.getWidth(), component.getHeight()));
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
  }

  @Override
  public void draw(MachineRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
    IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, MachineRecipe recipe, @NotNull IFocusGroup focuses) {
    builder.addAnimatedRecipeArrow(recipe.getRecipeTotalTickTime())
      .setPosition(getMaxWidth() / 2, 8);
    Font font = Minecraft.getInstance().font;
    textsToRender.forEach(component -> {
      font.split(component, getMaxWidth() - 16).forEach(string -> {
        builder.addDrawable(
          new DrawableText(string.toString(), font.width(string), font.lineHeight, FastColor.ABGR32.color(1, 0, 0, 0)) {
            @Override
            public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
              super.draw(guiGraphics, xOffset, yOffset);
            }
          }
//          MMRJeiPlugin.jeiHelpers.getGuiHelper().createBlankDrawable() {
//
//          }
        );
//          .drawString(font, string, 8, maxHeight.get(), FastColor.ABGR32.color(1, 0, 0, 0));
        maxHeight.getAndAdd(font.lineHeight + 3);
      });
//      builder.addText(component, getMaxWidth() - 16, 256 - maxHeight.get())
//        .setPosition(8, maxHeight.get());
//      maxHeight.getAndAdd(Minecraft.getInstance().font.wordWrapHeight(component, getMaxWidth() - 16));
    });
  }

  private record ComponentValue(int x, int y, int width, int height) {}
}
