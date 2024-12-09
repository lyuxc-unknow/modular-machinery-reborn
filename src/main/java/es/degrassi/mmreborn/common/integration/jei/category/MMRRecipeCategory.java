package es.degrassi.mmreborn.common.integration.jei.category;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.integration.jei.JeiComponentRegistry;
import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.Registration;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.gui.elements.DrawableWrappedText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MMRRecipeCategory implements IRecipeCategory<MachineRecipe> {
  private DynamicMachine machine;
  private final String title;
  private final IDrawable background, icon;

  public final int initialX = 8, gap = 8;
  @Getter
  protected int width = 256, height = 256;

  public MMRRecipeCategory(DynamicMachine machine) {
    this.machine = machine;
    this.title = machine.getLocalizedName();
    this.background = MMRJeiPlugin.jeiHelpers.getGuiHelper().createBlankDrawable(256, 256);
    ItemStack stack = new ItemStack(ItemRegistration.CONTROLLER.get());
    stack.set(Registration.MACHINE_DATA, machine.getRegistryName());
    this.icon = MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
  }

  public void updateMachine(DynamicMachine machine) {
    this.machine = machine;
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
  public @Nullable IDrawable getIcon() {
    return icon;
  }

  @Override
  public void setRecipe(@NotNull IRecipeLayoutBuilder builder, MachineRecipe recipe, @NotNull IFocusGroup focuses) {
    this.width = recipe.getWidth();
    this.height = recipe.getHeight();

    recipe.getCraftingRequirements()
        .stream()
        .filter(component -> JeiComponentRegistry.hasJeiComponent(component.getRequirementType()))
        .map(component -> component.getRequirementType().castRequirement(component))
        .map(component -> JeiComponentRegistry.getJeiComponent(component.getRequirementType()).create(component))
        .forEach(requirement -> requirement.setRecipe(this, builder, recipe, focuses));
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, @NotNull MachineRecipe recipe, @NotNull IFocusGroup focuses) {
    builder.addAnimatedRecipeArrow(20)
        .setPosition(recipe.getProgressPosition().x(), recipe.getProgressPosition().y());

    final List<Component> textsToRender = new LinkedList<>();

    textsToRender.add(
        Component.translatable(
            "modular_machinery_reborn.jei.ingredient.duration",
            recipe.getRecipeTotalTickTime()
        )
    );

    Font font = Minecraft.getInstance().font;
    AtomicInteger nextHeight = new AtomicInteger(0);
    AtomicInteger toRemove = new AtomicInteger(0);
    textsToRender.forEach(component -> {
      nextHeight.set(recipe.getHeight() - gap - font.wordWrapHeight(component, recipe.getWidth() - 8) - toRemove.get());
      builder.addDrawable(new DrawableWrappedText(List.of(component), recipe.getWidth() - 8))
          .setPosition(initialX, nextHeight.get());
      toRemove.getAndAdd(font.wordWrapHeight(component, recipe.getWidth() - 8) + 2);
    });
  }
}
