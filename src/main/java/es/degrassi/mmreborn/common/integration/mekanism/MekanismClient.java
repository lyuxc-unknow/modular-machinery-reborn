package es.degrassi.mmreborn.common.integration.mekanism;

import es.degrassi.mmreborn.client.ModularMachineryRebornClient;
import es.degrassi.mmreborn.client.screen.ChemicalHatchScreen;
import es.degrassi.mmreborn.common.entity.base.ChemicalTankEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class MekanismClient {
  public static ChemicalTankEntity getClientSideChemicalHatchEntity(BlockPos pos) {
    if(Minecraft.getInstance().level != null) {
      BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(pos);
      if(tile instanceof ChemicalTankEntity controller)
        return controller;
    }
    throw new IllegalStateException("Trying to open a Fluid Hatch container without clicking on a Custom Machine block");
  }

  @SubscribeEvent
  public void registerMenuScreens(final RegisterMenuScreensEvent event) {
    event.register(ContainerRegistration.CHEMICAL_HATCH.get(), ChemicalHatchScreen::new);
  }
  
  @SubscribeEvent
  public void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
    event.register(
      ModularMachineryRebornClient::blockColor,

        BlockRegistration.CHEMICAL_INPUT_HATCH_TINY.get(),
        BlockRegistration.CHEMICAL_INPUT_HATCH_SMALL.get(),
        BlockRegistration.CHEMICAL_INPUT_HATCH_NORMAL.get(),
        BlockRegistration.CHEMICAL_INPUT_HATCH_REINFORCED.get(),
        BlockRegistration.CHEMICAL_INPUT_HATCH_BIG.get(),
        BlockRegistration.CHEMICAL_INPUT_HATCH_HUGE.get(),
        BlockRegistration.CHEMICAL_INPUT_HATCH_LUDICROUS.get(),
        BlockRegistration.CHEMICAL_INPUT_HATCH_VACUUM.get(),

        BlockRegistration.CHEMICAL_OUTPUT_HATCH_TINY.get(),
        BlockRegistration.CHEMICAL_OUTPUT_HATCH_SMALL.get(),
        BlockRegistration.CHEMICAL_OUTPUT_HATCH_NORMAL.get(),
        BlockRegistration.CHEMICAL_OUTPUT_HATCH_REINFORCED.get(),
        BlockRegistration.CHEMICAL_OUTPUT_HATCH_BIG.get(),
        BlockRegistration.CHEMICAL_OUTPUT_HATCH_HUGE.get(),
        BlockRegistration.CHEMICAL_OUTPUT_HATCH_LUDICROUS.get(),
        BlockRegistration.CHEMICAL_OUTPUT_HATCH_VACUUM.get()
    );
  }

  @SubscribeEvent
  public void registerItemColors(final RegisterColorHandlersEvent.Item event) {
    event.register(
        ModularMachineryRebornClient::itemColor,

        ItemRegistration.CHEMICAL_INPUT_HATCH_TINY.get(),
        ItemRegistration.CHEMICAL_INPUT_HATCH_SMALL.get(),
        ItemRegistration.CHEMICAL_INPUT_HATCH_NORMAL.get(),
        ItemRegistration.CHEMICAL_INPUT_HATCH_REINFORCED.get(),
        ItemRegistration.CHEMICAL_INPUT_HATCH_BIG.get(),
        ItemRegistration.CHEMICAL_INPUT_HATCH_HUGE.get(),
        ItemRegistration.CHEMICAL_INPUT_HATCH_LUDICROUS.get(),
        ItemRegistration.CHEMICAL_INPUT_HATCH_VACUUM.get(),

        ItemRegistration.CHEMICAL_OUTPUT_HATCH_TINY.get(),
        ItemRegistration.CHEMICAL_OUTPUT_HATCH_SMALL.get(),
        ItemRegistration.CHEMICAL_OUTPUT_HATCH_NORMAL.get(),
        ItemRegistration.CHEMICAL_OUTPUT_HATCH_REINFORCED.get(),
        ItemRegistration.CHEMICAL_OUTPUT_HATCH_BIG.get(),
        ItemRegistration.CHEMICAL_OUTPUT_HATCH_HUGE.get(),
        ItemRegistration.CHEMICAL_OUTPUT_HATCH_LUDICROUS.get(),
        ItemRegistration.CHEMICAL_OUTPUT_HATCH_VACUUM.get()
    );
  }
}
