package es.degrassi.mmreborn.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

import java.util.Collections;
import java.util.List;

public class ItemIngredient implements IIngredient<ItemStack> {

  private final ItemStack item;

  public ItemIngredient(ItemStack item) {
    this.item = item;
  }

  @Override
  public List<ItemStack> getAll() {
    return Collections.singletonList(this.item);
  }

  @Override
  public IIngredient<ItemStack> copy() {
    return new ItemIngredient(item.copy());
  }

  @Override
  public IIngredient<ItemStack> copyWithRotation(Rotation rotation) {
    return new ItemIngredient(item.copy());
  }

  @Override
  public boolean test(ItemStack item) {
    return ItemStack.isSameItemSameComponents(this.item, item);
  }

  @Override
  public String toString() {
    return ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, this.item).result().map(JsonElement::toString).orElse("");
  }

  public ItemStack getStack(int amount) {
    return item.copyWithCount(amount);
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("item", item.toString());
    return json;
  }
}
