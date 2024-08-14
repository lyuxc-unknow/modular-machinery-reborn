package es.degrassi.mmreborn.common.network.client;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CPlaceStructurePacket(DynamicMachine machine, BlockPos pos,
                                    BlockState machineState) implements CustomPacketPayload {

  public static final Type<CPlaceStructurePacket> TYPE = new Type<>(ModularMachineryReborn.rl("place_structure"));


  public CPlaceStructurePacket(FriendlyByteBuf friendlyByteBuf) {
    this(friendlyByteBuf.readJsonWithCodec(DynamicMachine.CODEC.codec()), friendlyByteBuf.readBlockPos(), friendlyByteBuf.readJsonWithCodec(BlockState.CODEC));
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, CPlaceStructurePacket> CODEC = new StreamCodec<>() {
    @Override
    public CPlaceStructurePacket decode(RegistryFriendlyByteBuf pBuffer) {
      return new CPlaceStructurePacket(pBuffer);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf pBuffer, CPlaceStructurePacket packet) {
      pBuffer.writeJsonWithCodec(DynamicMachine.CODEC.codec(), packet.machine);
      pBuffer.writeBlockPos(packet.pos);
      pBuffer.writeJsonWithCodec(BlockState.CODEC, packet.machineState);
    }
  };

  public static void handle(CPlaceStructurePacket packet, IPayloadContext context) {
    if (context.flow().isServerbound())
      context.enqueueWork(() -> {
//        ModularMachineryRebornClient.renderHelper.startPreview(
//          DynamicMachineRenderContext.createContext(
//            packet.machine,
//            packet.pos,
//            packet.machineState
//          )
//        );
//        PoseStack pose = new PoseStack();
//        pose.translate(packet.pos.getX(), packet.pos.getY(), packet.pos.getZ());
//        MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
//        source.endLastBatch();
//        source.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
//        source.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
//        source.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
//        source.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));
//        ModularMachineryRebornClient.renderHelper.placePreview(pose, source, packet.pos);
      });
  }

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
