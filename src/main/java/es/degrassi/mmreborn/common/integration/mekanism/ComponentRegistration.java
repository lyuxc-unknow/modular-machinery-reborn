package es.degrassi.mmreborn.common.integration.mekanism;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.component.ComponentChemical;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ComponentRegistration {

  public static final DeferredRegister<ComponentType> MACHINE_COMPONENTS = DeferredRegister.create(ComponentType.REGISTRY_KEY, ModularMachineryReborn.MODID);

  public static final Supplier<ComponentType> COMPONENT_CHEMICAL = MACHINE_COMPONENTS.register("chemical", ComponentChemical::new);

  public static void register(final IEventBus bus) {
    MACHINE_COMPONENTS.register(bus);
  }
}
