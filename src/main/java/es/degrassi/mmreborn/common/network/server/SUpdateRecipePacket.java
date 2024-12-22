package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.container.ControllerContainer;
import es.degrassi.mmreborn.common.crafting.ActiveMachineRecipe;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateRecipePacket(ResourceLocation recipe, Integer ticks, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateRecipePacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_recipe"));

  @Override
  public Type<SUpdateRecipePacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateRecipePacket> CODEC = StreamCodec.composite(
    ResourceLocation.STREAM_CODEC,
    SUpdateRecipePacket::recipe,
    ByteBufCodecs.INT,
    SUpdateRecipePacket::ticks,
    BlockPos.STREAM_CODEC,
    SUpdateRecipePacket::pos,
    SUpdateRecipePacket::new
  );

  @SuppressWarnings("unchecked")
  public static void handle(SUpdateRecipePacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof MachineControllerEntity entity) {
          RecipeHolder<MachineRecipe> recipe = (RecipeHolder<MachineRecipe>) context.player().level().getRecipeManager().byKey(packet.recipe).orElse(null);
          entity.getCraftingManager().setActiveRecipe(new ActiveMachineRecipe(recipe, entity));
          entity.getCraftingManager().setRecipeTicks(packet.ticks);
          if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.containerMenu instanceof ControllerContainer menu &&
          menu.getEntity().getBlockPos().equals(packet.pos)) {
            entity.getCraftingManager().setActiveRecipe(new ActiveMachineRecipe(recipe, entity));
            entity.getCraftingManager().setRecipeTicks(packet.ticks);
          }
        }
      });
  }
}
