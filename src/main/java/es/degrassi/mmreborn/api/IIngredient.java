package es.degrassi.mmreborn.api;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Rotation;

import java.util.List;
import java.util.function.Predicate;

public interface IIngredient<O> extends Predicate<O> {
  List<O> getAll();

  IIngredient<O> copy();

  IIngredient<O> copyWithRotation(Rotation rotation);

  JsonObject asJson();
}
