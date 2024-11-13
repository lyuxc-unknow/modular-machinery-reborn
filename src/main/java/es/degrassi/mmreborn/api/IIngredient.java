package es.degrassi.mmreborn.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapLike;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.util.MMRLogger;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

import java.util.List;
import java.util.function.Predicate;

public interface IIngredient<O> extends Predicate<O> {

  NamedCodec<IIngredient<ItemStack>> ITEM = new NamedCodec<>() {
    @Override
    public <T> DataResult<Pair<IIngredient<ItemStack>, T>> decode(DynamicOps<T> ops, T input) {
      MMRLogger.INSTANCE.info(input);
      DataResult<MapLike<T>> mapResult = ops.getMap(input);
      if(mapResult.result().isPresent()) {
        MapLike<T> map = mapResult.result().get();
        T item = map.get("item");
        if(item != null)
          return DefaultCodecs.SIZED_INGREDIENT_WITH_NBT.read(ops, item).map(i -> Pair.of(new ItemIngredient(i.getItems()[0]), ops.empty()));
        T tag = map.get("tag");
        if(tag != null)
          return DefaultCodecs.tagKey(Registries.ITEM).read(ops, tag).map(t -> Pair.of(ItemTagIngredient.create(t), ops.empty()));
        return DataResult.error(() -> "Couldn't get an item ingredient from: " + map);
      }
      DataResult<String> result = ops.getStringValue(input);
      if(result.result().isPresent()) {
        String s = result.result().get();
        if(s.startsWith("#")) {
          try {
            return DataResult.success(Pair.of(ItemTagIngredient.create(s), ops.empty()));
          } catch (IllegalArgumentException e) {
            return DataResult.error(e::getMessage);
          }
        }

        try {
          DataResult<ItemStack> r = NamedCodec.of(ItemStack.CODEC).read(JsonOps.INSTANCE, new Gson().fromJson(s, JsonObject.class));
          if (r.result().isPresent()) {
            ItemStack stack = r.result().get();
            return DataResult.success(Pair.of(new ItemIngredient(stack), ops.empty()));
          }
        } catch (IllegalArgumentException e) {
          return DataResult.error(() -> "Invalid item stack: " + e.getMessage());
        }

        /*try {
          return DataResult.success(Pair.of(new ItemIngredient(BuiltInRegistries.ITEM.get(ResourceLocation.parse(s)).getDefaultInstance()), ops.empty()));
        } catch (ResourceLocationException e) {
          return DataResult.error(() -> "Invalid item ID: " + e.getMessage());
        }*/
      }
      return DataResult.error(() -> "Unable to get an item ingredient from: " + input);
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, IIngredient<ItemStack> input, T prefix) {
      if(input instanceof ItemIngredient ingredient)
        return ops.mergeToPrimitive(prefix, ops.createString(ingredient.toString()));
      else if(input instanceof ItemTagIngredient ingredient)
        return ops.mergeToPrimitive(prefix, ops.createString(ingredient.toString()));
      return DataResult.error(() -> String.format("Item Ingredient: %s is not an item or a tag !", input));
    }

    @Override
    public String name() {
      return "Item ingredient";
    }
  };

  List<O> getAll();

  IIngredient<O> copy();
  IIngredient<O> copyWithRotation(Rotation rotation);

  JsonObject asJson();
}
