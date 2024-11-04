package es.degrassi.mmreborn.common.crafting.requirement.jei;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementFluid;
import java.util.List;

import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class JeiFluidComponent extends JeiComponent<FluidStack, RequirementFluid> {
  public JeiFluidComponent(RequirementFluid requirement) {
    super(requirement, 0, 0);
  }

  @Override
  public int getWidth() {
    return 16;
  }

  @Override
  public int getHeight() {
    return 61;
  }

  @Override
  public List<FluidStack> ingredients() {
    return Lists.newArrayList(requirement.required.asFluidStack());
  }

  @Override
  @SuppressWarnings("removal")
  public @NotNull List<Component> getTooltip(@NotNull FluidStack ingredient, @NotNull TooltipFlag tooltipFlag) {
    List<Component> tooltip = super.getTooltip(ingredient, tooltipFlag);
    String mode = requirement.getActionType().isInput() ? "input" : "output";
    tooltip.add(Component.translatable("modular_machinery_reborn.jei.ingredient.fluid." + mode, ingredient.getHoverName(), ingredient.getAmount()));
    if(requirement.chance < 1F && requirement.chance >= 0F) {
      String keyNever = requirement.getActionType().isInput() ? "tooltip.machinery.chance.in.never" : "tooltip.machinery.chance.out.never";
      String keyChance = requirement.getActionType().isInput() ? "tooltip.machinery.chance.in" : "tooltip.machinery.chance.out";

      String chanceStr = String.valueOf(Mth.floor(requirement.chance * 100F));
      if(requirement.chance == 0F) {
        tooltip.add(Component.translatable(keyNever));
      } else {
        if(requirement.chance < 0.01F) {
          chanceStr = "< 1";
        }
        chanceStr += "%";
        tooltip.add(Component.translatable(keyChance, chanceStr));
      }
    }
    return tooltip;
  }

  @Override
  public void setRecipeInput(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    category.updateByProcessed(category.processedInputComponents, getWidth(), getHeight(), true);
    builder
      .addInputSlot(category.x.get() + 1, category.y.get() + 1)
      .setOverlay(
        MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawable(texture(), getUOffset(), getVOffset(), getWidth() + 2, getHeight() + 2),
        -1,
        -1
      )
      .setFluidRenderer(getRequirement().amount, false, getWidth(), getHeight())
      .addFluidStack(getRequirement().required.asFluidStack().getFluid(), getRequirement().amount);
    category.x.getAndAdd(category.gapX);
    category.x.getAndAdd(getWidth());
    category.updateMaxHeightInput(this, true);
  }

  @Override
  public void setRecipeOutput(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    category.updateByProcessed(category.processedOutputComponents, getWidth(), getHeight(), true);
    builder
      .addOutputSlot(category.x.get() + 1, category.y.get() + 1)
      .setOverlay(
        MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawable(texture(), getUOffset(), getVOffset(), getWidth() + 2, getHeight() + 2),
        -1,
        -1
      )
      .setFluidRenderer(getRequirement().amount, false, getWidth(), getHeight())
      .addFluidStack(getRequirement().required.asFluidStack().getFluid(), getRequirement().amount);
    category.x.getAndAdd(category.gapX);
    category.x.getAndAdd(getWidth());
    category.updateMaxHeightOutput(this, true);
  }
}
