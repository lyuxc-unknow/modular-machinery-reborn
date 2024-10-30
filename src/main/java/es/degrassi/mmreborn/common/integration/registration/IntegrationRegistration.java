package es.degrassi.mmreborn.common.integration.registration;

import es.degrassi.mmreborn.common.integration.mekanism.MekanismRegistration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

public class IntegrationRegistration {
  public static void register(final IEventBus bus) {
    if (ModList.get().isLoaded("mekanism"))
      MekanismRegistration.register(bus);
  }
}
