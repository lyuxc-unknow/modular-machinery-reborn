package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.component.ComponentBiome;
import es.degrassi.mmreborn.common.crafting.component.ComponentChunkload;
import es.degrassi.mmreborn.common.crafting.component.ComponentDimension;
import es.degrassi.mmreborn.common.crafting.component.ComponentEnergy;
import es.degrassi.mmreborn.common.crafting.component.ComponentFluid;
import es.degrassi.mmreborn.common.crafting.component.ComponentItem;
import es.degrassi.mmreborn.common.crafting.component.ComponentTime;
import es.degrassi.mmreborn.common.crafting.component.ComponentWeather;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ComponentRegistration {

  public static final DeferredRegister<ComponentType> MACHINE_COMPONENTS = DeferredRegister.create(ComponentType.REGISTRY_KEY, ModularMachineryReborn.MODID);
  public static final Registry<ComponentType> COMPONENTS_REGISTRY = MACHINE_COMPONENTS.makeRegistry(builder -> {});

  public static final Supplier<ComponentType> COMPONENT_ITEM = MACHINE_COMPONENTS.register("item", ComponentItem::new);
  public static final Supplier<ComponentType> COMPONENT_FLUID = MACHINE_COMPONENTS.register("fluid", ComponentFluid::new);
  public static final Supplier<ComponentType> COMPONENT_ENERGY = MACHINE_COMPONENTS.register("energy", ComponentEnergy::new);
  public static final Supplier<ComponentType> COMPONENT_DIMENSION = MACHINE_COMPONENTS.register("dimension", ComponentDimension::new);
  public static final Supplier<ComponentType> COMPONENT_BIOME = MACHINE_COMPONENTS.register("biome", ComponentBiome::new);
  public static final Supplier<ComponentType> COMPONENT_WEATHER = MACHINE_COMPONENTS.register("weather", ComponentWeather::new);
  public static final Supplier<ComponentType> COMPONENT_TIME = MACHINE_COMPONENTS.register("time", ComponentTime::new);
  public static final Supplier<ComponentType> COMPONENT_CHUNKLOAD = MACHINE_COMPONENTS.register("chunkload", ComponentChunkload::new);

  public static void register(final IEventBus bus) {
    MACHINE_COMPONENTS.register(bus);
  }
}
