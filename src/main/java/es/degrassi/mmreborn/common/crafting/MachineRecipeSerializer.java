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

//  @Override
//  public @NotNull MachineRecipe fromNetwork(FriendlyByteBuf buffer) {
//    ResourceLocation recipeId = buffer.readResourceLocation();
//    MMRLogger.INSTANCE.info("Receiving recipe: {} from server.", recipeId);
//    DataResult<MachineRecipe.MachineRecipeBuilder> result = MachineRecipe.CODEC.read(NbtOps.INSTANCE, buffer.readNbt());
//    if (result.result().isPresent()) {
//      MMRLogger.INSTANCE.info("Sucessfully received recipe: {} from server.", recipeId);
//      return result.result().get().build();
//    } else if (result.error().isPresent()) {
//      MMRLogger.INSTANCE.error("Error while parsing recipe json: {}, skipping...\n{}", recipeId, result.error().get().message());
//      throw new IllegalArgumentException("Error while receiving Custom Machine Recipe from server: " + recipeId + " error: " + result.error().get().message());
//    }
//    throw new IllegalStateException("No success nor error when receiving Custom Machine Recipe: " + recipeId + "from server. This can't happen");
//  }
//
//  @Override
//  public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull MachineRecipe recipe) {
//    MMRLogger.INSTANCE.info("Sending recipe: {} to clients", recipe.getId());
//    DataResult<Tag> result = MachineRecipe.CODEC.encodeStart(NbtOps.INSTANCE, new MachineRecipe.MachineRecipeBuilder(recipe));
//    if (result.result().isPresent()) {
//      MMRLogger.INSTANCE.info("Sucessfully send recipe: {} to clients.", recipe.getId());
//      buffer.writeNbt(result.result().get());
//      return;
//    } else if (result.error().isPresent()) {
//      MMRLogger.INSTANCE.error("Error while sending recipe: {} to clients.%n{}", recipe.getId(), result.error().get().message());
//      throw new IllegalArgumentException("Error while sending Custom Machine Recipe to clients: " + " error: " + result.error().get().message());
//    }
//    throw new IllegalStateException("No success nor error when sending Custom Machine Recipe: " + "to clients. This can't happen");
//  }
}
