package es.degrassi.mmreborn.common.crafting;

import com.google.common.collect.Iterables;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
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
  @Getter
  private MachineRecipe recipe;
  private final Map<ResourceLocation, CompoundTag> dataMap = new HashMap<>();
  private ResourceLocation futureRecipeId;

  private final MachineControllerEntity entity;
  @Getter
  private boolean initialized = false;

  public ActiveMachineRecipe(MachineRecipe recipe, MachineControllerEntity entity) {
    this.recipe = recipe;
    this.entity = entity;
  }

  public ActiveMachineRecipe(CompoundTag serialized, MachineControllerEntity entity) {
    this.entity = entity;
    this.futureRecipeId = ResourceLocation.tryParse(serialized.getString("id"));
  }

  public void init() {
    if (this.futureRecipeId == null || this.entity.getLevel() == null) return;
    this.initialized = true;
    this.recipe = entity
      .getLevel()
      .getRecipeManager()
      .getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get())
      .stream()
      .map(RecipeHolder::value)
      .filter(recipe -> recipe.getOwningMachineIdentifier() != null)
      .filter(recipe -> recipe.getOwningMachineIdentifier().equals(entity.getId()))
      .filter(recipe -> recipe.getId().equals(futureRecipeId))
      .findFirst()
      .orElse(null);

//    if (serialized.contains("data", Tag.TAG_LIST)) {
//      ListTag listData = serialized.getList("data", Tag.TAG_COMPOUND);
//      for (int i = 0; i < listData.size(); i++) {
//        CompoundTag tag = listData.getCompound(i);
//        String key = tag.getString("key");
//        CompoundTag data = tag.getCompound("data");
//        if (!key.isEmpty()) {
//          dataMap.put(ResourceLocation.parse(key), data);
//        }
//      }
//    }
  }

  public void reset() {
    entity.setRecipeTicks(-1);
    entity.setCraftingStatus(MachineControllerEntity.CraftingStatus.NO_RECIPE);
  }

  @Nonnull
  public MachineControllerEntity.CraftingStatus tick(RecipeCraftingContext context) {
    if (!initialized) init();
    //Skip per-tick logic until controller can finish the recipe
    if (this.isCompleted(context)) {
      return MachineControllerEntity.CraftingStatus.working();
    }

    RecipeCraftingContext.CraftingCheckResult check;
    if (!(check = context.ioTick(entity.getRecipeTicks())).isFailure()) {
      entity.setRecipeTicks(entity.getRecipeTicks() + 1);
      return MachineControllerEntity.CraftingStatus.working();
    } else {
      entity.setRecipeTicks(-1);
      return MachineControllerEntity.CraftingStatus.failure(
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
    int time = this.recipe.getRecipeTotalTickTime();
    //Not sure which a user will use... let's try both.
    time = Math.round(RecipeModifier.applyModifiers(context.getModifiers(RequirementTypeRegistration.DURATION.get()), RequirementTypeRegistration.DURATION.get(), null, time, false));
    return entity.getRecipeTicks() >= time;
  }

  public void start(RecipeCraftingContext context) {
    entity.setRecipeTicks(0);
    entity.setCraftingStatus(MachineControllerEntity.CraftingStatus.working());
    context.startCrafting();
  }

  public void complete(RecipeCraftingContext context) {
    context.finishCrafting();
  }

  public CompoundTag serialize() {
    CompoundTag tag = new CompoundTag();
    if (this.recipe != null)
      tag.putString("id", this.recipe.getId().toString());

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
