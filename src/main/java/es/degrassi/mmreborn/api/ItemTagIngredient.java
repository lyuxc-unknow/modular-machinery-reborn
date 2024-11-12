package es.degrassi.mmreborn.api;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.common.util.Utils;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

public class ItemTagIngredient implements IIngredient<ItemStack> {

  private final TagKey<Item> tag;

  private ItemTagIngredient(TagKey<Item> tag) {
    this.tag = tag;
  }

  public static ItemTagIngredient create(String s) throws IllegalArgumentException {
    if(s.startsWith("#"))
      s = s.substring(1);
    if(!Utils.isResourceNameValid(s))
      throw new IllegalArgumentException(String.format("Invalid tag id : %s", s));
    TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(s));
    return new ItemTagIngredient(tag);
  }

  public static ItemTagIngredient create(ResourceLocation s) throws IllegalArgumentException {
    return new ItemTagIngredient(TagKey.create(Registries.ITEM, s));
  }

  public static ItemTagIngredient create(TagKey<Item> tag) throws IllegalArgumentException {
    return new ItemTagIngredient(tag);
  }

  @Override
  public List<ItemStack> getAll() {
    return TagUtil.getItems(this.tag).toList().stream().map(Item::getDefaultInstance).toList();
  }

  @Override
  public IIngredient<ItemStack> copy() {
    return new ItemTagIngredient(tag);
  }

  @Override
  public IIngredient<ItemStack> copyWithRotation(Rotation rotation) {
    return new ItemTagIngredient(tag);
  }

  @Override
  public boolean test(ItemStack item) {
    return item.is(this.tag);//TagUtil.getItems(this.tag).anyMatch(Predicate.isEqual(item));
  }

  @Override
  public String toString() {
    return "#" + getTag();
  }

  public ResourceLocation getTag() {
    return tag.location();
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("tag", getTag().toString());
    return json;
  }
}
