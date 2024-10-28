package es.degrassi.mmreborn.common.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class MachineRecipeSerializer implements RecipeSerializer<MachineRecipe> {
  @Override
  public @NotNull MapCodec<MachineRecipe> codec() {
    return MachineRecipe.CODEC.mapCodec().xmap(MachineRecipe.MachineRecipeBuilder::build, MachineRecipe.MachineRecipeBuilder::new);
  }

  @Override
  public @NotNull StreamCodec<RegistryFriendlyByteBuf, MachineRecipe> streamCodec() {
    return ByteBufCodecs.fromCodecWithRegistries(codec().codec());
  }
}
