package es.degrassi.mmreborn.common.machine;

import es.degrassi.mmreborn.ModularMachineryReborn;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class MachineHatchType {
  public static final ResourceKey<Registry<MachineHatchType>> REGISTRY_KEY =
      ResourceKey.createRegistryKey(ModularMachineryReborn.rl("hatch_type"));

  protected MachineHatchType() {}

  public static MachineHatchType create() {
    return new MachineHatchType();
  }

  public ResourceLocation getId() {
    return ModularMachineryReborn.getMachineHatchTypeRegistrar().getKey(this);
  }
}
