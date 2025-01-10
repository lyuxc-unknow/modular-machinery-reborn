package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.network.IData;
import es.degrassi.mmreborn.client.container.ContainerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record SUpdateContainerPacket(int windowId, List<IData<?>> data) implements CustomPacketPayload {
  public static final Type<SUpdateContainerPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_container"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateContainerPacket> CODEC = StreamCodec.ofMember(SUpdateContainerPacket::write, SUpdateContainerPacket::read);

  @Override
  public Type<SUpdateContainerPacket> type() {
    return TYPE;
  }

  public void write(RegistryFriendlyByteBuf buf) {
    buf.writeInt(this.windowId);
    buf.writeShort(this.data.size());
    this.data.forEach(data -> data.writeData(buf));
  }

  public static SUpdateContainerPacket read(RegistryFriendlyByteBuf buf) {
    int windowId = buf.readInt();
    List<IData<?>> dataList = new ArrayList<>();
    short size = buf.readShort();
    for(short i = 0; i < size; i++) {
      IData<?> data = IData.readData(buf);
      if(data != null)
        dataList.add(data);
    }
    return new SUpdateContainerPacket(windowId, dataList);
  }

  public static void handle(SUpdateContainerPacket packet, IPayloadContext context) {
    if(context.flow().isClientbound())
      context.enqueueWork(() -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null && player.containerMenu instanceof ContainerBase<?> container && player.containerMenu.containerId == packet.windowId) {
          packet.data.forEach(container::handleData);
        }
      });
  }
}
