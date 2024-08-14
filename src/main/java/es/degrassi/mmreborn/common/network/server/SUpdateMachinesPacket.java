package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.CreativeTabsRegistration;
import es.degrassi.mmreborn.common.util.MMRLogger;
import io.netty.handler.codec.EncoderException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateMachinesPacket(Map<ResourceLocation, DynamicMachine> machines) implements CustomPacketPayload {
  public static final Type<SUpdateMachinesPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_machines"));

  public SUpdateMachinesPacket(FriendlyByteBuf friendlyByteBuf) {
    this(readMachinesFromBuffer(friendlyByteBuf));
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateMachinesPacket> CODEC = new StreamCodec<>() {

    @Override
    public void encode(RegistryFriendlyByteBuf buf, SUpdateMachinesPacket packet) {
      buf.writeInt(packet.machines.size());
      packet.machines.values()
        .forEach(machine -> {
          try {
            DynamicMachine.CODEC.toNetwork(machine, buf);
          } catch (EncoderException e) {
            MMRLogger.INSTANCE.error("Something wrong happens, {}", e.getMessage());
          }
        });
    }

    @Override
    public SUpdateMachinesPacket decode(RegistryFriendlyByteBuf pBuffer) {
      return new SUpdateMachinesPacket(pBuffer);
    }
  };

  private static Map<ResourceLocation, DynamicMachine> readMachinesFromBuffer(FriendlyByteBuf buffer) {
    final int capacity = buffer.readInt();
    Map<ResourceLocation, DynamicMachine> machines = new HashMap<>(capacity);
    for (int i = 0; i < capacity; i++) {
      DynamicMachine machine = DynamicMachine.CODEC.fromNetwork(buffer);
      machines.put(machine.getRegistryName(), machine);
    }
    return machines;
  }

  public static void handle(SUpdateMachinesPacket packet, IPayloadContext context) {
    if(context.flow().isClientbound())
      context.enqueueWork(() -> {
        ModularMachineryReborn.MACHINES.clear();
        ModularMachineryReborn.MACHINES.putAll(packet.machines);
        Minecraft mc = Minecraft.getInstance();
        CreativeModeTab.ItemDisplayParameters params = new CreativeModeTab.ItemDisplayParameters(mc.player.connection.enabledFeatures(), mc.player.canUseGameMasterBlocks() && mc.options.operatorItemsTab().get(), mc.level.registryAccess());
        CreativeTabsRegistration.MODULAR_MACHINERY_REBORN_TAB.get().buildContents(params);
      });
  }
  @Override
  public Type<SUpdateMachinesPacket> type() {
    return TYPE;
  }
}
