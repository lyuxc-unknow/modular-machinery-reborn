package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateMachineColorPacket(Integer color, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateMachineColorPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_machine_color"));

  @Override
  public Type<SUpdateMachineColorPacket> type() {
    return TYPE;
  }

  public SUpdateMachineColorPacket(FriendlyByteBuf friendlyByteBuf) {
    this(friendlyByteBuf.readInt(), friendlyByteBuf.readBlockPos());
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateMachineColorPacket> CODEC = StreamCodec.composite(
    ByteBufCodecs.INT,
    SUpdateMachineColorPacket::color,
    BlockPos.STREAM_CODEC,
    SUpdateMachineColorPacket::pos,
    SUpdateMachineColorPacket::new
  );

  public static void handle(SUpdateMachineColorPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof MachineControllerEntity entity) {
          entity.setMachineColor(packet.color);
        }
      });
  }
}
