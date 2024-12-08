package es.degrassi.mmreborn.common.crafting;

import com.google.common.collect.Iterables;
import es.degrassi.mmreborn.common.crafting.helper.CraftingCheckResult;
import es.degrassi.mmreborn.common.crafting.helper.CraftingStatus;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ActiveMachineRecipe {
  private RecipeHolder<MachineRecipe> recipe;
  private final Map<ResourceLocation, CompoundTag> dataMap = new HashMap<>();
  private ResourceLocation futureRecipeId;

  private final MachineControllerEntity entity;
  @Getter
  private boolean initialized = false;

  public ActiveMachineRecipe(RecipeHolder<MachineRecipe> recipe, MachineControllerEntity entity) {
    this.recipe = recipe;
    this.entity = entity;
  }

  public ActiveMachineRecipe(CompoundTag serialized, MachineControllerEntity entity) {
    this.entity = entity;
    this.futureRecipeId = ResourceLocation.tryParse(serialized.getString("id"));
  }

  public RecipeHolder<MachineRecipe> getHolder() {
    return recipe;
  }

  public MachineRecipe getRecipe() {
    return recipe.value();
  }

  @SuppressWarnings("unchecked")
  public void init() {
    if (this.futureRecipeId == null || this.entity.getLevel() == null) return;
    this.initialized = true;
    this.recipe = (RecipeHolder<MachineRecipe>) entity
      .getLevel()
      .getRecipeManager()
      .byKey(futureRecipeId)
      .orElse(null);
    this.futureRecipeId = null;
  }

  public void reset() {
    entity.setRecipeTicks(-1);
    entity.setCraftingStatus(CraftingStatus.NO_RECIPE);
  }

  @Nonnull
  public CraftingStatus tick(RecipeCraftingContext context) {
    if (!initialized) init();
    //Skip per-tick logic until controller can finish the recipe
    if (this.isCompleted(context)) {
      return CraftingStatus.working();
    }

    CraftingCheckResult check;
    if (!(check = context.ioTick(entity.getRecipeTicks())).isFailure()) {
      entity.setRecipeTicks(entity.getRecipeTicks() + 1);
      return CraftingStatus.working();
    } else {
      entity.setRecipeTicks(-1);
      return CraftingStatus.failure(
        Iterables.getFirst(check.getUnlocalizedErrorMessages(), ""));
    }
  }

  @Nonnull
  public Map<ResourceLocation, CompoundTag> getData() {
    return dataMap;
  }

  @Nonnull
  public CompoundTag getOrCreateData(ResourceLocation key) {
    return dataMap.computeIfAbsent(key, k -> new CompoundTag());
  }

  public boolean isCompleted(RecipeCraftingContext context) {
    int time = this.recipe.value().getRecipeTotalTickTime();
    //Not sure which a user will use... let's try both.
    time = Math.round(RecipeModifier.applyModifiers(context.getModifiers(RequirementTypeRegistration.DURATION.get()), RequirementTypeRegistration.DURATION.get(), null, time, false));
    return entity.getRecipeTicks() >= time;
  }

  public void start(RecipeCraftingContext context) {
    entity.setRecipeTicks(0);
    entity.setCraftingStatus(CraftingStatus.working());
    context.startCrafting();
  }

  public void complete(RecipeCraftingContext context) {
    context.finishCrafting();
  }

  public CompoundTag serialize() {
    CompoundTag tag = new CompoundTag();
    if (this.recipe != null)
      tag.putString("id", this.recipe.id().toString());

    ListTag listData = new ListTag();
    for (Map.Entry<ResourceLocation, CompoundTag> dataEntry : this.dataMap.entrySet()) {
      CompoundTag tagData = new CompoundTag();
      tagData.putString("key", dataEntry.getKey().toString());
      tagData.put("data", dataEntry.getValue());
    }
    tag.put("data", listData);
    return tag;
  }
}
