package es.degrassi.mmreborn.api;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.level.block.Rotation;

public interface IIngredient<O> extends Predicate<O> {
  NamedCodec<IIngredient<PartialBlockState>> BLOCK = NamedCodec.either(BlockIngredient.CODEC, BlockTagIngredient.CODEC, "Block Ingredient").flatComapMap(
    either -> either.map(Function.identity(), Function.identity()),
    ingredient -> {
      if(ingredient instanceof BlockIngredient ing)
        return DataResult.success(Either.left(ing));
      else if(ingredient instanceof BlockTagIngredient ing)
        return DataResult.success(Either.right(ing));
      return DataResult.error(() -> String.format("Block Ingredient : %s is not a block nor a tag !", ingredient));
    },
    "Block ingredient"
  );

  List<O> getAll();

  IIngredient<O> copy();
  IIngredient<O> copyWithRotation(Rotation rotation);
}
