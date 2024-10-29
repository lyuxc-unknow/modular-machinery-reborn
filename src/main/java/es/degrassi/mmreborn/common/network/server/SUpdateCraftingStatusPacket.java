package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.container.ControllerContainer;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateCraftingStatusPacket(MachineControllerEntity.CraftingStatus status, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateCraftingStatusPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_crafting_status"));

  @Override
  public Type<SUpdateCraftingStatusPacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateCraftingStatusPacket> CODEC = StreamCodec.composite(
    MachineControllerEntity.CraftingStatus.STREAM_CODEC,
    SUpdateCraftingStatusPacket::status,
    BlockPos.STREAM_CODEC,
    SUpdateCraftingStatusPacket::pos,
    SUpdateCraftingStatusPacket::new
  );

  public static void handle(SUpdateCraftingStatusPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof MachineControllerEntity entity) {
          entity.setCraftingStatus(packet.status);
          if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.containerMenu instanceof ControllerContainer menu &&
          menu.getEntity().getBlockPos().equals(packet.pos)) {
            entity.setCraftingStatus(packet.status);
          }
        }
      });
  }
}
