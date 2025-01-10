package es.degrassi.mmreborn.common.manager.crafting;

import es.degrassi.mmreborn.api.crafting.CraftingContext;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import lombok.Setter;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class MachineRecipeFinder {

  private final MachineControllerEntity tile;
  private final int baseCooldown;
  private final CraftingContext.Mutable mutableCraftingContext;
  private List<RecipeChecker<MachineRecipe>> recipes;
  private List<RecipeChecker<MachineRecipe>> okToCheck;
  @Setter
  private boolean componentChanged = true;

  private int recipeCheckCooldown;

  public MachineRecipeFinder(MachineControllerEntity tile, CraftingContext.Mutable mutableCraftingContext) {
    this.tile = tile;
    this.baseCooldown = MMRConfig.get().checkRecipeTicks.get();
    this.mutableCraftingContext = mutableCraftingContext;
  }

  public void init() {
    if (tile.getLevel() == null)
      throw new IllegalStateException("Broken machine " + tile.getFoundMachine().getRegistryName() + "doesn't have a world");
    this.recipes = tile.getLevel().getRecipeManager()
        .getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get())
        .stream()
        .filter(recipe -> recipe.value().getOwningMachineIdentifier().equals(tile.getId()))
        .sorted((holder1, holder2) -> Comparator.comparingInt(MachineRecipe::getConfiguredPriority).reversed().compare(holder1.value(), holder2.value()))
        .map(RecipeChecker::new)
        .toList();
    this.okToCheck = new ArrayList<>();
    this.recipeCheckCooldown = tile.getLevel().random.nextInt(this.baseCooldown);
  }

  public Optional<RecipeHolder<MachineRecipe>> findRecipe(boolean immediately) {
    if (tile.getLevel() == null)
      return Optional.empty();

    if (immediately || this.recipeCheckCooldown-- <= 0) {
      this.recipeCheckCooldown = this.baseCooldown;
      if (this.componentChanged || immediately) {
        this.okToCheck.clear();
        this.okToCheck.addAll(this.recipes);
      }
      Iterator<RecipeChecker<MachineRecipe>> iterator = this.okToCheck.iterator();
      while (iterator.hasNext()) {
        RecipeChecker<MachineRecipe> checker = iterator.next();
        if (!this.componentChanged && checker.isInventoryRequirementsOnly() && !immediately)
          continue;
        if (checker.check(this.tile, this.mutableCraftingContext.setRecipe(checker.getRecipe().value(), checker.getRecipe().id()), this.componentChanged || immediately)) {
          setComponentChanged(false);
          return Optional.of(checker.getRecipe());
        }
        if (!checker.isInventoryRequirementsOk())
          iterator.remove();
      }
      setComponentChanged(false);
    }
    return Optional.empty();
  }
}
