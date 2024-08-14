package es.degrassi.mmreborn.common.crafting;

import es.degrassi.mmreborn.ModularMachineryReborn;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public abstract class ComponentType {
  public static final ResourceKey<Registry<ComponentType>> REGISTRY_KEY = ResourceKey.createRegistryKey(ModularMachineryReborn.rl("component_type"));

  //Should return the mod's modid if this component is dependent on some other mod
  //Return null if no other mod/only vanilla is required.
  @Nullable
  public String requiresModid() {
    return null;
  }
}
