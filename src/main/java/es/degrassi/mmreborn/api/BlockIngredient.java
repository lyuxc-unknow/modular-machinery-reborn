package es.degrassi.mmreborn.api;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.util.MMRLogger;
import es.degrassi.mmreborn.common.util.Utils;
import lombok.Getter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BlockIngredient implements IIngredient<PartialBlockState> {
  public static final BlockIngredient AIR = new BlockIngredient(PartialBlockState.AIR);
  public static final BlockIngredient ANY = new BlockIngredient(PartialBlockState.ANY);
  public static final BlockIngredient MACHINE = new BlockIngredient(PartialBlockState.MACHINE);

//  public static final NamedCodec<BlockIngredient> CODEC = PartialBlockState.CODEC_LIST.xmap(BlockIngredient::new, BlockIngredient::getAll, "Block ingredient");

  public static final NamedCodec<BlockIngredient> TAG_CODEC = NamedCodec.STRING.comapFlatMap(string -> {
    try {
      return DataResult.success(BlockIngredient.create(string));
    } catch (IllegalArgumentException e) {
      return DataResult.error(e::getMessage);
    }
  }, BlockIngredient::toString, "BlockIngredient with tag");

  public static final NamedCodec<BlockIngredient> CODEC = NamedCodec.either(
      PartialBlockState.CODEC,
      TAG_CODEC,
      "Block Ingredient"
  ).listOf().flatComapMap(
      list -> {
        List<BlockIngredient> ings = Lists.newArrayList();
        list.forEach(either -> ings.add(either.map(BlockIngredient::new, Function.identity())));
        AtomicReference<BlockIngredient> ing = new AtomicReference<>(null);
        ings.iterator().forEachRemaining(i -> {
          if (ing.get() == null) {
            ing.set(i);
            return;
          }
          ing.set(ing.get().merge(i));
        });
        return ing.get();
      },
      ing -> {
        List<Either<PartialBlockState, BlockIngredient>> list = Lists.newArrayList();
        if (ing.isTag) {
          list.add(Either.right(ing));
          return DataResult.success(list);
        } else if (!ing.getAll().isEmpty()) {
          ing.getAll().iterator().forEachRemaining(state -> list.add(Either.left(state)));
          return DataResult.success(list);
        }
        return DataResult.error(() -> "Block Ingredient is not tag or contains an empty block array!");
      },
      "Block Ingredient"
  );

  private final Supplier<List<PartialBlockState>> partialBlockStates;
  @Getter
  private final boolean isTag;

  public BlockIngredient(TagKey<Block> tag) {
    this.isTag = true;
    this.partialBlockStates = Suppliers.memoize(() -> TagUtil.getBlocks(tag).map(PartialBlockState::new).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf)));
  }

  public BlockIngredient(PartialBlockState partialBlockState) {
    this(List.of(partialBlockState));
  }

  public BlockIngredient(List<PartialBlockState> partialBlockState) {
    this(partialBlockState, false);
  }

  public BlockIngredient(List<PartialBlockState> partialBlockState, boolean isTag) {
    partialBlockStates = Suppliers.memoize(() -> partialBlockState);
    this.isTag = isTag;
  }

  public static BlockIngredient create(String s) throws IllegalArgumentException {
    if(s.startsWith("#"))
      s = s.substring(1);
    if(!Utils.isResourceNameValid(s))
      throw new IllegalArgumentException(String.format("Invalid tag id : %s", s));
    TagKey<Block> tag = TagKey.create(Registries.BLOCK, ResourceLocation.parse(s));
    return new BlockIngredient(tag);
  }

  public BlockIngredient copy() {
    return new BlockIngredient(partialBlockStates.get().stream().map(PartialBlockState::copy).toList());
  }

  @Override
  public List<PartialBlockState> getAll() {
    return this.partialBlockStates.get();
  }

  @Override
  public boolean test(PartialBlockState partialBlockState) {
    return this.partialBlockStates.get().stream().anyMatch(state -> state.getBlockState() == partialBlockState.getBlockState());
  }

  @Override
  public String toString() {
    return asJson().toString();
  }

  public BlockIngredient copyWithRotation(Rotation rotation) {
    return new BlockIngredient(getAll().stream().map(state -> state.copyWithRotation(rotation)).toList());
  }

  public BlockIngredient merge(BlockIngredient other) {
    List<PartialBlockState> ingredients = Lists.newArrayList();
    ingredients.addAll(getAll());
    ingredients.addAll(other.getAll());
    return new BlockIngredient(ingredients, isTag || other.isTag);
  }

  public BlockIngredient merge(TagKey<Block> tag) {
    return merge(new BlockIngredient(tag));
  }


  @Override
  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("tag", isTag);
    JsonArray array = new JsonArray();
    getAll().forEach(state -> array.add(state.toString()));
    json.add("states", array);
    return json;
  }

  public static BlockIngredient of(Object o) {
    return (BlockIngredient) o;
  }
}
