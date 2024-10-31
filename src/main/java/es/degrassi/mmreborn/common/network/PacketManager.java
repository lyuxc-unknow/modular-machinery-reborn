package es.degrassi.mmreborn.common.network;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.network.server.SMachineUpdatePacket;
import es.degrassi.mmreborn.common.network.server.SOpenFilePacket;
import es.degrassi.mmreborn.common.network.server.SUpdateCraftingStatusPacket;
import es.degrassi.mmreborn.common.network.server.SUpdateMachineColorPacket;
import es.degrassi.mmreborn.common.network.server.SUpdateRecipePacket;
import es.degrassi.mmreborn.common.network.server.component.SUpdateEnergyComponentPacket;
import es.degrassi.mmreborn.common.network.server.component.SUpdateFluidComponentPacket;
import es.degrassi.mmreborn.common.network.server.component.SUpdateItemComponentPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ModularMachineryReborn.MODID, bus = EventBusSubscriber.Bus.MOD)
public class PacketManager {
  @SubscribeEvent
  public static void register(final RegisterPayloadHandlersEvent event) {
    final PayloadRegistrar registrar = event.registrar(ModularMachineryReborn.MODID);
    registrar.playToClient(SOpenFilePacket.TYPE, SOpenFilePacket.CODEC, SOpenFilePacket::handle);
    registrar.playToClient(SMachineUpdatePacket.TYPE, SMachineUpdatePacket.CODEC, SMachineUpdatePacket::handle);
    registrar.playToClient(SUpdateEnergyComponentPacket.TYPE, SUpdateEnergyComponentPacket.CODEC, SUpdateEnergyComponentPacket::handle);
    registrar.playToClient(SUpdateFluidComponentPacket.TYPE, SUpdateFluidComponentPacket.CODEC, SUpdateFluidComponentPacket::handle);
    registrar.playToClient(SUpdateItemComponentPacket.TYPE, SUpdateItemComponentPacket.CODEC, SUpdateItemComponentPacket::handle);
    registrar.playToClient(SUpdateCraftingStatusPacket.TYPE, SUpdateCraftingStatusPacket.CODEC, SUpdateCraftingStatusPacket::handle);
    registrar.playToClient(SUpdateRecipePacket.TYPE, SUpdateRecipePacket.CODEC, SUpdateRecipePacket::handle);
    registrar.playToClient(SUpdateMachineColorPacket.TYPE, SUpdateMachineColorPacket.CODEC, SUpdateMachineColorPacket::handle);
  }
}
