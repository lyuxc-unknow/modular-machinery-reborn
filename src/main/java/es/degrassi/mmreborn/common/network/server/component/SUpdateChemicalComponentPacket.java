package es.degrassi.mmreborn.common.network.server.component;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.base.ChemicalTankEntity;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateChemicalComponentPacket(ChemicalStack chemical, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateChemicalComponentPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_chemical"));

  @Override
  public Type<SUpdateChemicalComponentPacket> type() {
    return TYPE;
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateChemicalComponentPacket> CODEC = StreamCodec.composite(
      ChemicalStack.OPTIONAL_STREAM_CODEC,
      SUpdateChemicalComponentPacket::chemical,
      BlockPos.STREAM_CODEC,
      SUpdateChemicalComponentPacket::pos,
      SUpdateChemicalComponentPacket::new
  );

  public static void handle(SUpdateChemicalComponentPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof ChemicalTankEntity entity) {
          entity.getTank().setStack(packet.chemical);
        }
      });
  }
}
