package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.registration.CreativeTabsRegistration;
import es.degrassi.mmreborn.common.util.MMRLogger;
import io.netty.handler.codec.EncoderException;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record SSyncMachinesPacket(Map<ResourceLocation, DynamicMachine> machines) implements CustomPacketPayload {
  public static final Type<SSyncMachinesPacket> TYPE = new Type<>(ModularMachineryReborn.rl("sync_machines"));

  public static final StreamCodec<RegistryFriendlyByteBuf, SSyncMachinesPacket> CODEC = new StreamCodec<>() {
    @Override
    public SSyncMachinesPacket decode(RegistryFriendlyByteBuf buf) {
      Map<ResourceLocation, DynamicMachine> map = new HashMap<>();
      int size = buf.readInt();
      for(int i = 0; i < size; i++) {
        try {
          ResourceLocation location = ResourceLocation.STREAM_CODEC.decode(buf);
          DynamicMachine machine;
          machine = DynamicMachine.CODEC.fromNetwork(buf);
          machine.setRegistryName(location);
          map.put(location, machine);
        } catch (EncoderException e) {
          MMRLogger.INSTANCE.error(e);
        }
      }
      return new SSyncMachinesPacket(map);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, SSyncMachinesPacket packet) {
      buf.writeInt(packet.machines.size());
      packet.machines
          .values()
          .forEach(machine -> {
            try {
              ResourceLocation.STREAM_CODEC.encode(buf, machine.getRegistryName());
              DynamicMachine.CODEC.toNetwork(machine, buf);
            } catch (EncoderException e) {
              MMRLogger.INSTANCE.error(e);
            }
          });
    }
  };

  @Override
  public Type<SSyncMachinesPacket> type() {
    return TYPE;
  }

  public static void handle(SSyncMachinesPacket packet, IPayloadContext context) {
    if(context.flow().isClientbound()) {
      ModularMachineryReborn.MACHINES.clear();
      ModularMachineryReborn.MACHINES.putAll(packet.machines);
      Minecraft mc = Minecraft.getInstance();
      CreativeModeTab.ItemDisplayParameters params = new CreativeModeTab.ItemDisplayParameters(mc.player.connection.enabledFeatures(), mc.player.canUseGameMasterBlocks() && mc.options.operatorItemsTab().get(), mc.level.registryAccess());
      CreativeTabsRegistration.MODULAR_MACHINERY_REBORN_TAB.get().buildContents(params);
      if(ModList.get().isLoaded("jei"))
        MMRJeiPlugin.reloadMachines(packet.machines);
    }
  }
}
