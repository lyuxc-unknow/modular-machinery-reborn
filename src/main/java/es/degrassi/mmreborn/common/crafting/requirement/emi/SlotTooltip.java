package es.degrassi.mmreborn.common.crafting.requirement.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiResolutionRecipe;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.config.HelpLevel;
import dev.emi.emi.runtime.EmiFavorites;
import dev.emi.emi.screen.tooltip.EmiTooltip;
import dev.emi.emi.screen.tooltip.RecipeCostTooltipComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.List;

public interface SlotTooltip extends RecipeHolder {
  default void addSlotTooltip(List<ClientTooltipComponent> list) {
    if (getStack().getChance() != 1) {
      list.add(EmiTooltip.chance((getRecipe() != null ? "produce" : "consume"), getStack().getChance()));
    }
    EmiRecipe recipe = getRecipe();
    if (recipe != null) {
      if (recipe.getId() != null && EmiConfig.showRecipeIds) {
        list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.literal(recipe.getId().toString(), ChatFormatting.GRAY))));
      }
      if (canResolve() && EmiConfig.helpLevel.has(HelpLevel.NORMAL)) {
        if (EmiConfig.viewRecipes.isBound()) {
          list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.resolve.resolve", EmiConfig.viewRecipes.getBindText()))));
        }
        if (EmiConfig.defaultStack.isBound()) {
          list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.resolve.default", EmiConfig.defaultStack.getBindText()))));
        }
      } else if (EmiConfig.favorite.isBound() && EmiConfig.helpLevel.has(HelpLevel.NORMAL) && EmiFavorites.canFavorite(getStack(), getRecipe())) {
        list.add(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.favorite_recipe", EmiConfig.favorite.getBindText()))));
      }
      if (EmiConfig.showCostPerBatch && recipe.supportsRecipeTree() && !(recipe instanceof EmiResolutionRecipe)) {
        RecipeCostTooltipComponent rctc = new RecipeCostTooltipComponent(recipe);
        if (rctc.shouldDisplay()) {
          list.add(rctc);
        }
      }
    }
  }
}
