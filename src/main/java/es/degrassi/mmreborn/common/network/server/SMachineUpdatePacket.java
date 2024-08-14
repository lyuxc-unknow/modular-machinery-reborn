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

public record SMachineUpdatePacket(DynamicMachine machine, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SMachineUpdatePacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_machine"));

  @Override
  public Type<SMachineUpdatePacket> type() {
    return TYPE;
  }
  public SMachineUpdatePacket(FriendlyByteBuf friendlyByteBuf) {
    this(friendlyByteBuf.readJsonWithCodec(DynamicMachine.CODEC.codec()), friendlyByteBuf.readBlockPos());
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SMachineUpdatePacket> CODEC = StreamCodec.composite(
    ByteBufCodecs.fromCodecWithRegistries(DynamicMachine.CODEC.codec()),
    SMachineUpdatePacket::machine,
    BlockPos.STREAM_CODEC,
    SMachineUpdatePacket::pos,
    SMachineUpdatePacket::new
  );

  public static void handle(SMachineUpdatePacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof MachineControllerEntity entity) {
          entity.setMachine(packet.machine);
        }
      });
  }
}
