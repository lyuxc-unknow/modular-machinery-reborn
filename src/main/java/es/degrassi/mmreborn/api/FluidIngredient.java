package es.degrassi.mmreborn.api;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.RegistrarCodec;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.material.Fluid;

public class FluidIngredient implements IIngredient<Fluid> {
  private static final NamedCodec<FluidIngredient> CODEC_FOR_DATAPACK = RegistrarCodec.FLUID.xmap(FluidIngredient::new, ingredient -> ingredient.fluid, "Fluid ingredient");
  private static final NamedCodec<FluidIngredient> CODEC_FOR_KUBEJS = RegistrarCodec.FLUID.fieldOf("fluid").xmap(FluidIngredient::new, ingredient -> ingredient.fluid, "Fluid ingredient");
  public static final NamedCodec<FluidIngredient> CODEC = NamedCodec.either(CODEC_FOR_DATAPACK, CODEC_FOR_KUBEJS, "Fluid Ingredient")
    .xmap(either -> either.map(Function.identity(), Function.identity()), Either::left, "Fluid ingredient");

  private final Fluid fluid;

  public FluidIngredient(Fluid fluid) {
    this.fluid = fluid;
  }

  @Override
  public List<Fluid> getAll() {
    return Collections.singletonList(this.fluid);
  }

  @Override
  public IIngredient<Fluid> copy() {
    return new FluidIngredient(fluid);
  }

  @Override
  public IIngredient<Fluid> copyWithRotation(Rotation rotation) {
    return new FluidIngredient(fluid);
  }

  @Override
  public JsonObject asJson() {
    return new JsonObject();
  }

  @Override
  public boolean test(Fluid fluid) {
    return this.fluid == fluid;
  }

  @Override
  public String toString() {
    return BuiltInRegistries.FLUID.getKey(this.fluid).toString();
  }
}
