package es.degrassi.mmreborn.common.crafting;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ComponentType {
  public static final ResourceKey<Registry<ComponentType>> REGISTRY_KEY =
      ResourceKey.createRegistryKey(ModularMachineryReborn.rl("component_type"));

  protected ComponentType() {}

  public static ComponentType create() {
    return new ComponentType();
  }

  public ResourceLocation getId() {
    return ModularMachineryReborn.getComponentRegistrar().getKey(this);
  }
}
