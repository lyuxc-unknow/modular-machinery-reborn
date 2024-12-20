package es.degrassi.mmreborn.api.codec;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.machine.MachineJsonReloadListener;
import io.netty.buffer.ByteBuf;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DefaultCodecs {

  public static final NamedCodec<ResourceLocation> RESOURCE_LOCATION = NamedCodec.STRING.comapFlatMap(DefaultCodecs::decodeResourceLocation, ResourceLocation::toString, "Resource location");
  public static final NamedCodec<Character> CHARACTER = NamedCodec.STRING.comapFlatMap(DefaultCodecs::decodeCharacter, Object::toString, "Character");

  public static final NamedCodec<SoundEvent> SOUND_EVENT = RESOURCE_LOCATION.xmap(SoundEvent::createVariableRangeEvent, SoundEvent::getLocation, "Sound event");

  public static final NamedCodec<Direction> DIRECTION = NamedCodec.enumCodec(Direction.class);

  public static <A> Codec<Optional<A>> optionalEmptyMap(final Codec<A> pCodec) {
    return new Codec<>() {
      @Override
      public <T> DataResult<Pair<Optional<A>, T>> decode(DynamicOps<T> p_330879_, T p_330924_) {
        return isEmptyMap(p_330879_, p_330924_)
            ? DataResult.success(Pair.of(Optional.empty(), p_330924_))
            : pCodec.decode(p_330879_, p_330924_).map(p_337591_ -> p_337591_.mapFirst(Optional::of));
      }

      private static <T> boolean isEmptyMap(DynamicOps<T> p_338754_, T p_338581_) {
        Optional<MapLike<T>> optional = p_338754_.getMap(p_338581_).result();
        return optional.isPresent() && optional.get().entries().findAny().isEmpty();
      }

      public <T> DataResult<T> encode(Optional<A> p_338508_, DynamicOps<T> p_331521_, T p_331876_) {
        return p_338508_.isEmpty() ? DataResult.success(p_331521_.emptyMap()) : pCodec.encode(p_338508_.get(), p_331521_, p_331876_);
      }
    };
  }

  public static final Codec<FluidStack> OPTIONAL_FLUID_CODEC = optionalEmptyMap(FluidStack.CODEC)
      .xmap(stack -> stack.orElse(FluidStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
  public static final NamedCodec<FluidStack> FLUID_OR_STACK = NamedCodec.either(RegistrarCodec.FLUID, NamedCodec.of(OPTIONAL_FLUID_CODEC), "FluidStack").xmap(either -> either.map(fluid -> new FluidStack(fluid, 1000), Function.identity()), Either::right, "Fluid Stack");
  public static final NamedCodec<ItemStack> ITEM_OR_STACK = NamedCodec.either(RegistrarCodec.ITEM, NamedCodec.of(ItemStack.OPTIONAL_CODEC), "ItemStack").xmap(either -> either.map(Item::getDefaultInstance, Function.identity()), Either::right, "Item Stack");

  private static final MapCodec<Ingredient.ItemValue> INGREDIENT_ITEM_VALUE_MAP_CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(ITEM_OR_STACK.codec().fieldOf("item").forGetter(Ingredient.ItemValue::item))
          .apply(instance, Ingredient.ItemValue::new)
  );

  private static final MapCodec<Ingredient.TagValue> INGREDIENT_TAG_VALUE_MAP_CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(Ingredient.TagValue::tag))
          .apply(instance, Ingredient.TagValue::new)
  );

  private static final MapCodec<Ingredient.Value> INGREDIENT_VALUE_MAP_CODEC =
      NeoForgeExtraCodecs.xor(INGREDIENT_ITEM_VALUE_MAP_CODEC, INGREDIENT_TAG_VALUE_MAP_CODEC)
          .xmap(either -> either.map(itemValue -> itemValue, tagValue -> tagValue), value -> {
            if (value instanceof Ingredient.TagValue ingredient$tagvalue) {
              return Either.right(ingredient$tagvalue);
            } else if (value instanceof Ingredient.ItemValue ingredient$itemvalue) {
              return Either.left(ingredient$itemvalue);
            } else {
              throw new UnsupportedOperationException("This is neither an item value nor a tag value.");
            }
          });

  private static final MapCodec<Ingredient> INGREDIENT_MAP_CODEC = NeoForgeExtraCodecs.<IngredientType<?>,
          ICustomIngredient, Ingredient.Value>dispatchMapOrElse(
          NeoForgeRegistries.INGREDIENT_TYPES.byNameCodec(),
          ICustomIngredient::getType,
          IngredientType::codec,
          INGREDIENT_VALUE_MAP_CODEC)
      .xmap(either -> either.map(ICustomIngredient::toVanilla, v -> Ingredient.fromValues(Stream.of(v))), ingredient -> {
        if (!ingredient.isCustom()) {
          var values = ingredient.getValues();
          if (values.length == 1) {
            return Either.right(values[0]);
          }
          // Convert vanilla ingredients with 2+ values to a CompoundIngredient. Empty ingredients are not allowed here.
          return Either.left(new CompoundIngredient(Stream.of(ingredient.getValues()).map(v -> Ingredient.fromValues(Stream.of(v))).toList()));
        }
        return Either.left(ingredient.getCustomIngredient());
      })
      .validate(ingredient -> {
        if (!ingredient.isCustom() && ingredient.getValues().length == 0) {
          return DataResult.error(() -> "Cannot serialize empty ingredient using the map codec");
        }
        return DataResult.success(ingredient);
      });

  public static final NamedCodec<Ingredient> INGREDIENT = NamedCodec.of(INGREDIENT_MAP_CODEC.codec(), "Ingredient");

  public static final NamedCodec<SizedIngredient> SIZED_INGREDIENT_WITH_NBT = NamedCodec.record(sizedIngredientInstance ->
      sizedIngredientInstance.group(
          INGREDIENT.fieldOf("ingredient").forGetter(SizedIngredient::ingredient),
          NamedCodec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(SizedIngredient::count)
      ).apply(sizedIngredientInstance, SizedIngredient::new), "Sized ingredient with nbt"
  );

  public static final NamedCodec<AABB> BOX = NamedCodec.DOUBLE_STREAM.comapFlatMap(stream -> {
    double[] arr = stream.toArray();
    if (arr.length == 3)
      return DataResult.success(new AABB(arr[0], arr[1], arr[2], arr[0], arr[1], arr[2]));
    else if (arr.length == 6)
      return DataResult.success(new AABB(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]));
    else
      return DataResult.error(() -> Arrays.toString(arr) + " is not an array of 3 or 6 elements");
  }, aabb -> DoubleStream.of(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ), "Box");
  public static final StreamCodec<ByteBuf, AABB> STREAM_BOX = ByteBufCodecs.fromCodec(BOX.codec());

  public static final NamedCodec<BlockPos[]> PAIR_BLOCK_POS_CODEC = NamedCodec.INT_STREAM.comapFlatMap(stream -> {
    int[] arr = stream.toArray();
    if (arr.length == 6)
      return DataResult.success(new BlockPos[]{ new BlockPos(arr[0], arr[1], arr[2]),
          new BlockPos(arr[3], arr[4], arr[5]) });
    return DataResult.error(() -> Arrays.toString(arr) + " is not an array of 6 elements");
  }, arr -> IntStream.of(arr[0].getX(), arr[0].getY(), arr[0].getZ(), arr[1].getX(), arr[1].getY(), arr[1].getZ()), "BlockPos box");

  public static final StreamCodec<ByteBuf, BlockPos[]> STREAM_PAIR_BLOCK_POS_CODEC = ByteBufCodecs.fromCodec(PAIR_BLOCK_POS_CODEC.codec());

  public static final NamedCodec<Integer> HEX = NamedCodec.STRING.comapFlatMap(DefaultCodecs::decodeHexColor, DefaultCodecs::encodeHexColor, "Hex color");

  public static <T> NamedCodec<TagKey<T>> tagKey(ResourceKey<Registry<T>> registry) {
    return RESOURCE_LOCATION.xmap(rl -> TagKey.create(registry, rl), TagKey::location, "Tag: " + registry.location());
  }

  public static <T> NamedCodec<TagKey<T>> registryKey(Registry<T> registry) {
    return NamedCodec.STRING.comapFlatMap(s -> {
      if (s.startsWith("#")) {
        try {
          TagKey<T> key = TagKey.create(registry.key(), ResourceLocation.parse(s.substring(1)));
          if (MachineJsonReloadListener.context != null && MachineJsonReloadListener.context.getTag(key).isEmpty())
            return DataResult.error(() -> "Invalid tag: " + s);
          return DataResult.success(key);
        } catch (ResourceLocationException e) {
          return DataResult.error(e::getMessage);
        }
      }
      return DataResult.error(() -> "Invalid tag, must start with #");
    }, key -> "#" + key.location(), "Value or Tag: " + registry.key().location());
  }

  public static <T> NamedCodec<Either<TagKey<T>, Holder<T>>> registryValueOrTag(Registry<T> registry) {
    return NamedCodec.STRING.comapFlatMap(s -> {
      if (s.startsWith("#")) {
        try {
          TagKey<T> key = TagKey.create(registry.key(), ResourceLocation.parse(s.substring(1)));
          if (MachineJsonReloadListener.context != null && MachineJsonReloadListener.context.getTag(key).isEmpty())
            return DataResult.error(() -> "Invalid tag: " + s);
          return DataResult.success(Either.left(key));
        } catch (ResourceLocationException e) {
          return DataResult.error(e::getMessage);
        }
      } else {
        try {
          Optional<Holder.Reference<T>> ref = registry.getHolder(registry.getId(ResourceLocation.parse(s)));
          return ref.map(reference -> DataResult.success(Either.<TagKey<T>, Holder<T>>right(reference))).orElse(DataResult.error(() -> "Invalid item: " + s));
        } catch (ResourceLocationException e) {
          return DataResult.error(e::getMessage);
        }
      }
    }, either -> either.map(key -> "#" + key.location(), holder -> holder.unwrapKey().get().location().toString()), "Value or Tag: " + registry.key().location());
  }

  private static DataResult<ResourceLocation> decodeResourceLocation(String encoded) {
    try {
      return DataResult.success(ResourceLocation.parse(encoded));
    } catch (ResourceLocationException e) {
      return DataResult.error(e::getMessage);
    }
  }

  private static DataResult<Character> decodeCharacter(String encoded) {
    if (encoded.length() != 1)
      return DataResult.error(() -> "Invalid character : \"" + encoded + "\" must be a single character !");
    return DataResult.success(encoded.charAt(0));
  }

  private static final List<Character> validHex = Lists.charactersOf("0123456789AaBbCcDdEeFf");

  private static DataResult<Integer> decodeHexColor(String encoded) {
    if (!encoded.startsWith("#"))
      return DataResult.error(() -> "Invalid hex color format, must starts with '#'");
    if (encoded.length() != 9)
      return DataResult.error(() -> "Invalid length : \"" + encoded + "\" must be 9 characters !(#FFFFFFFF [alpha alpha red red green green blue blue])");
    char[] chars = encoded.substring(1).toCharArray();
    for (char c : chars) {
      if (!validHex.contains(c))
        return DataResult.error(() -> "Invalid character: \"" + c + "\", valid characters are: " + validHex);
    }
    return DataResult.success(Config.toInt(encoded, 0xffffffff));
  }

  public static String encodeHexColor(Integer color) {
    return String.format("#%08x", color);
  }
}
