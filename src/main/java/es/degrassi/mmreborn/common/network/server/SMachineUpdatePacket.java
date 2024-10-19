package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.util.MMRLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SMachineUpdatePacket(ResourceLocation machine, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SMachineUpdatePacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_machine"));

  @Override
  public @NotNull Type<SMachineUpdatePacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SMachineUpdatePacket> CODEC = StreamCodec.composite(
    ResourceLocation.STREAM_CODEC,
    SMachineUpdatePacket::machine,
    BlockPos.STREAM_CODEC,
    SMachineUpdatePacket::pos,
    SMachineUpdatePacket::new
  );

  public static void handle(SMachineUpdatePacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof MachineControllerEntity entity) {
          handleRefreshCustomMachineTilePacket(packet.pos, packet.machine);
        }
      });
  }

  public static void handleRefreshCustomMachineTilePacket(BlockPos pos, ResourceLocation machine) {
    if(Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if(tile instanceof MachineControllerEntity machineTile) {
        machineTile.setId(machine);
        machineTile.refreshClientData();
        Minecraft.getInstance().level.sendBlockUpdated(pos, machineTile.getBlockState(), machineTile.getBlockState(), Block.UPDATE_ALL);
      }
    }
  }
}
