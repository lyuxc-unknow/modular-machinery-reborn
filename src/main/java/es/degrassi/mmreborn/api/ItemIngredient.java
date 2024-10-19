package es.degrassi.mmreborn.api;

import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

public class ItemIngredient implements IIngredient<Item> {

  private final Item item;

  public ItemIngredient(Item item) {
    this.item = item;
  }

  @Override
  public List<Item> getAll() {
    return Collections.singletonList(this.item);
  }

  @Override
  public IIngredient<Item> copy() {
    return new ItemIngredient(item);
  }

  @Override
  public IIngredient<Item> copyWithRotation(Rotation rotation) {
    return new ItemIngredient(item);
  }

  @Override
  public boolean test(Item item) {
    return this.item == item;
  }

  @Override
  public String toString() {
    return BuiltInRegistries.ITEM.getKey(this.item).toString();
  }

  public ItemStack getStack(int amount) {
    return new ItemStack(item, amount);
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("item", BuiltInRegistries.ITEM.getKey(this.item).toString());
    return json;
  }
}
