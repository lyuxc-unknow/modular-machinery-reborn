package es.degrassi.mmreborn.common.crafting;

import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
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

  @Nonnull
  public Map<ResourceLocation, CompoundTag> getData() {
    return dataMap;
  }

  @Nonnull
  public CompoundTag getOrCreateData(ResourceLocation key) {
    return dataMap.computeIfAbsent(key, k -> new CompoundTag());
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
