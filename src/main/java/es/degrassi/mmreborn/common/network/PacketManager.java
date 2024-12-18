package es.degrassi.mmreborn.common.network;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.network.client.CBreakStructurePacket;
import es.degrassi.mmreborn.common.network.client.CExperienceButtonClickedPacket;
import es.degrassi.mmreborn.common.network.client.CPlaceStructurePacket;
import es.degrassi.mmreborn.common.network.server.SAddControllerRenderer;
import es.degrassi.mmreborn.common.network.server.SLootTablesPacket;
import es.degrassi.mmreborn.common.network.server.SMachineUpdatePacket;
import es.degrassi.mmreborn.common.network.server.SOpenFilePacket;
import es.degrassi.mmreborn.common.network.server.SRemoveControllerRenderer;
import es.degrassi.mmreborn.common.network.server.SSyncMachinesPacket;
import es.degrassi.mmreborn.common.network.server.SSyncPauseStatePacket;
import es.degrassi.mmreborn.common.network.server.SUpdateCraftingStatusPacket;
import es.degrassi.mmreborn.common.network.server.SUpdateMachineColorPacket;
import es.degrassi.mmreborn.common.network.server.SUpdateRecipePacket;
import es.degrassi.mmreborn.common.network.server.component.SUpdateEnergyComponentPacket;
import es.degrassi.mmreborn.common.network.server.component.SUpdateExperienceComponentPacket;
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
    // TO CLIENT
    registrar.playToClient(SLootTablesPacket.TYPE, SLootTablesPacket.CODEC, SLootTablesPacket::handle);
    registrar.playToClient(SOpenFilePacket.TYPE, SOpenFilePacket.CODEC, SOpenFilePacket::handle);
    registrar.playToClient(SMachineUpdatePacket.TYPE, SMachineUpdatePacket.CODEC, SMachineUpdatePacket::handle);
    registrar.playToClient(SUpdateEnergyComponentPacket.TYPE, SUpdateEnergyComponentPacket.CODEC, SUpdateEnergyComponentPacket::handle);
    registrar.playToClient(SUpdateExperienceComponentPacket.TYPE, SUpdateExperienceComponentPacket.CODEC, SUpdateExperienceComponentPacket::handle);
    registrar.playToClient(SUpdateFluidComponentPacket.TYPE, SUpdateFluidComponentPacket.CODEC, SUpdateFluidComponentPacket::handle);
    registrar.playToClient(SUpdateItemComponentPacket.TYPE, SUpdateItemComponentPacket.CODEC, SUpdateItemComponentPacket::handle);
    registrar.playToClient(SUpdateCraftingStatusPacket.TYPE, SUpdateCraftingStatusPacket.CODEC, SUpdateCraftingStatusPacket::handle);
    registrar.playToClient(SUpdateRecipePacket.TYPE, SUpdateRecipePacket.CODEC, SUpdateRecipePacket::handle);
    registrar.playToClient(SUpdateMachineColorPacket.TYPE, SUpdateMachineColorPacket.CODEC, SUpdateMachineColorPacket::handle);
    registrar.playToClient(SSyncMachinesPacket.TYPE, SSyncMachinesPacket.CODEC, SSyncMachinesPacket::handle);
    registrar.playToClient(SSyncPauseStatePacket.TYPE, SSyncPauseStatePacket.CODEC, SSyncPauseStatePacket::handle);
    registrar.playToClient(SAddControllerRenderer.TYPE, SAddControllerRenderer.CODEC, SAddControllerRenderer::handle);
    registrar.playToClient(SRemoveControllerRenderer.TYPE, SRemoveControllerRenderer.CODEC, SRemoveControllerRenderer::handle);

    // TO SERVER
    registrar.playToServer(CPlaceStructurePacket.TYPE, CPlaceStructurePacket.CODEC, CPlaceStructurePacket::handle);
    registrar.playToServer(CBreakStructurePacket.TYPE, CBreakStructurePacket.CODEC, CBreakStructurePacket::handle);
    registrar.playToServer(CExperienceButtonClickedPacket.TYPE, CExperienceButtonClickedPacket.CODEC, CExperienceButtonClickedPacket::handle);
  }
}
