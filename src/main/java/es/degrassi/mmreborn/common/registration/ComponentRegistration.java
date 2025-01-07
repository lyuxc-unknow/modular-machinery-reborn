package es.degrassi.mmreborn.common.registration;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ComponentRegistration {

  public static final DeferredRegister<ComponentType> MACHINE_COMPONENTS =
      DeferredRegister.create(ComponentType.REGISTRY_KEY, ModularMachineryReborn.MODID);
  public static final Registry<ComponentType> COMPONENTS_REGISTRY = MACHINE_COMPONENTS.makeRegistry(builder -> {});

  public static final Supplier<ComponentType> COMPONENT_ITEM = MACHINE_COMPONENTS.register("item",
      ComponentType::create);
  public static final Supplier<ComponentType> COMPONENT_DURATION = MACHINE_COMPONENTS.register("duration",
      ComponentType::create);
  public static final Supplier<ComponentType> COMPONENT_FLUID = MACHINE_COMPONENTS.register("fluid",
      ComponentType::create);
  public static final Supplier<ComponentType> COMPONENT_ENERGY = MACHINE_COMPONENTS.register("energy",
      ComponentType::create);
  public static final Supplier<ComponentType> COMPONENT_DIMENSION = MACHINE_COMPONENTS.register("dimension",
      ComponentType::create);
  public static final Supplier<ComponentType> COMPONENT_BIOME = MACHINE_COMPONENTS.register("biome",
      ComponentType::create);
  public static final Supplier<ComponentType> COMPONENT_WEATHER = MACHINE_COMPONENTS.register("weather",
      ComponentType::create);
  public static final Supplier<ComponentType> COMPONENT_TIME = MACHINE_COMPONENTS.register("time",
      ComponentType::create);
  public static final Supplier<ComponentType> COMPONENT_CHUNKLOAD = MACHINE_COMPONENTS.register("chunkload",
      ComponentType::create);
  public static final Supplier<ComponentType> COMPONENT_EXPERIENCE = MACHINE_COMPONENTS.register("experience",
      ComponentType::create);

  public static void register(final IEventBus bus) {
    MACHINE_COMPONENTS.register(bus);
  }
}
