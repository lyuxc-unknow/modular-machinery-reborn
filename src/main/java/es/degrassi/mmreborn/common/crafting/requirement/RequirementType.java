package es.degrassi.mmreborn.common.crafting.requirement;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import javax.annotation.Nullable;
import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

@Getter
public class RequirementType<V extends ComponentRequirement<?, V>> {
  public static final ResourceKey<Registry<RequirementType<? extends ComponentRequirement<?, ?>>>> REGISTRY_KEY = ResourceKey.createRegistryKey(ModularMachineryReborn.rl("requirement_type"));

  private final NamedCodec<V> codec;
  private final String requiresModid;

  private RequirementType(NamedCodec<V> codec, String requiresModid) {
    this.codec = codec;
    this.requiresModid = requiresModid;
  }

  public static <X, C extends ComponentRequirement<X, C>> RequirementType<C> create(NamedCodec<C> codec) {
    return new RequirementType<>(codec, null);
  }
  public static <X, C extends ComponentRequirement<X, C>> RequirementType<C> create(NamedCodec<C> codec, String requiresModid) {
    return new RequirementType<>(codec, requiresModid);
  }

  @Nullable
  public String requiresModid() {
    return requiresModid;
  }

  public RequirementType<V> getType() {
    return this;
  }

  public String requirementName() {
    return "";
  }

  @Override
  public String toString() {
    return "RequirementType{" +
      "type=" + codec.name() +
      '}';
  }
}

