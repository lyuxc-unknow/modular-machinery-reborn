package es.degrassi.mmreborn.common.integration.mekanism;

import net.neoforged.bus.api.IEventBus;

public class MekanismRegistration {
  public static void register(final IEventBus bus) {
    ComponentRegistration.register(bus);
    CreativeTabsRegistration.register(bus);
    RequirementTypeRegistration.register(bus);
    BlockRegistration.register(bus);
    ItemRegistration.register(bus);
    EntityRegistration.register(bus);
    ContainerRegistration.register(bus);
  }
}
