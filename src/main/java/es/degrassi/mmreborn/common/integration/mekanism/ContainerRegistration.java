package es.degrassi.mmreborn.common.integration.mekanism;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.container.ChemicalHatchContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ContainerRegistration {
  public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, ModularMachineryReborn.MODID);
  public static final DeferredHolder<MenuType<?>, MenuType<ChemicalHatchContainer>> CHEMICAL_HATCH = CONTAINERS.register("chemical_hatch", () -> IMenuTypeExtension.create(ChemicalHatchContainer::new));
  public static void register(IEventBus bus) {
    CONTAINERS.register(bus);
  }
}
