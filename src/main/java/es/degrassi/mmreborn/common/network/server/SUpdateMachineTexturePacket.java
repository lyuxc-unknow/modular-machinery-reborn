package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.base.TextureableMachineEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateMachineTexturePacket(ResourceLocation texture, boolean base, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateMachineTexturePacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_machine_texture"));

  @Override
  public Type<SUpdateMachineTexturePacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateMachineTexturePacket> CODEC = StreamCodec.composite(
    ResourceLocation.STREAM_CODEC,
    SUpdateMachineTexturePacket::texture,
    ByteBufCodecs.BOOL,
    SUpdateMachineTexturePacket::base,
    BlockPos.STREAM_CODEC,
    SUpdateMachineTexturePacket::pos,
    SUpdateMachineTexturePacket::new
  );

  public static void handle(SUpdateMachineTexturePacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        BlockEntity be = context.player().level().getBlockEntity(packet.pos);
        if (be instanceof TextureableMachineEntity entity) {
          if (packet.base) {
            entity.setMachineBaseTexture(packet.texture);
          } else {
            entity.setMachineOverlayTexture(packet.texture);
          }
          be.requestModelDataUpdate();
          //context.player().level().setBlockAndUpdate(be.getBlockPos(), be.getBlockState());
          be.setChanged();
        }
      });
  }
}
