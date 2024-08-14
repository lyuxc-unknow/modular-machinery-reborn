package es.degrassi.mmreborn.common.crafting;

import com.google.common.collect.Iterables;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ActiveMachineRecipe {
  @Getter
  private final MachineRecipe recipe;
  private final Map<ResourceLocation, CompoundTag> dataMap = new HashMap<>();
  @Getter
  private int tick = 0;

  private final MachineControllerEntity entity;

  public ActiveMachineRecipe(MachineRecipe recipe, MachineControllerEntity entity) {
    this.recipe = recipe;
    this.entity = entity;
  }

  public ActiveMachineRecipe(CompoundTag serialized, MachineControllerEntity entity) {
    this.entity = entity;
//    this.recipe = MachineRecipe.RECIPES
//      .values()
//      .stream()
//      .reduce(new LinkedList<>(), (acc, recipes) -> {
//        acc.addAll(recipes);
//        return acc;
//      })
//      .stream()
//      .filter(recipe -> recipe.getId().equals(ResourceLocation.tryParse(serialized.getString("recipeName"))))
//      .findFirst()
//      .orElse(null);
    this.recipe = Optional
      .ofNullable(entity.getLevel())
      .flatMap(level -> level
      .getRecipeManager()
      .getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get())
      .stream()
      .map(RecipeHolder::value)
      .filter(recipe -> recipe.getId().equals(ResourceLocation.tryParse(serialized.getString("recipeName"))))
      .findFirst()).orElse(null);

    this.tick = serialized.getInt("tick");
    if (serialized.contains("data", Tag.TAG_LIST)) {
      ListTag listData = serialized.getList("data", Tag.TAG_COMPOUND);
      for (int i = 0; i < listData.size(); i++) {
        CompoundTag tag = listData.getCompound(i);
        String key = tag.getString("key");
        CompoundTag data = tag.getCompound("data");
        if (!key.isEmpty()) {
          dataMap.put(ResourceLocation.parse(key), data);
        }
      }
    }
  }

  public void reset() {
    this.tick = 0;
  }

  @Nonnull
  public MachineControllerEntity.CraftingStatus tick(MachineControllerEntity ctrl, RecipeCraftingContext context) {
    //Skip per-tick logic until controller can finish the recipe
    if (this.isCompleted(ctrl, context)) {
      return MachineControllerEntity.CraftingStatus.working();
    }

    RecipeCraftingContext.CraftingCheckResult check;
    if (!(check = context.ioTick(tick)).isFailure()) {
      this.tick++;
      return MachineControllerEntity.CraftingStatus.working();
    } else {
      this.tick = 0;
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

  public boolean isCompleted(MachineControllerEntity controller, RecipeCraftingContext context) {
    int time = this.recipe.getRecipeTotalTickTime();
    //Not sure which a user will use... let's try both.
    time = Math.round(RecipeModifier.applyModifiers(context.getModifiers(RequirementTypeRegistration.DURATION.get()), RequirementTypeRegistration.DURATION.get(), null, time, false));
    return this.tick >= time;
  }

  public void start(RecipeCraftingContext context) {
    context.startCrafting();
  }

  public void complete(RecipeCraftingContext completionContext) {
    completionContext.finishCrafting();
  }

  public CompoundTag serialize() {
    CompoundTag tag = new CompoundTag();
    tag.putInt("tick", this.tick);
    tag.putString("recipeName", this.recipe.getId().toString());

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
