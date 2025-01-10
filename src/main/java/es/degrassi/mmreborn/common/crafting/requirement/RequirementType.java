package es.degrassi.mmreborn.common.crafting.requirement;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

@Getter
public class RequirementType<T extends IRequirement<?>> {

  public static final ResourceKey<Registry<RequirementType<? extends IRequirement<?>>>> REGISTRY_KEY =
      ResourceKey.createRegistryKey(ModularMachineryReborn.rl("requirement_type"));

  public static <T extends IRequirement<?>> RequirementType<T> world(NamedCodec<T> codec) {
    return new RequirementType<>(codec, true);
  }

  public static <T extends IRequirement<?>> RequirementType<T> inventory(NamedCodec<T> codec) {
    return new RequirementType<>(codec, false);
  }

  private final NamedCodec<T> codec;
  private final boolean worldRequirement;

  /**
   * A constructor for {@link RequirementType}.
   * Use {@link RequirementType#world(NamedCodec)} instead.
   */
  private RequirementType(NamedCodec<T> codec, boolean worldRequirement) {
    this.codec = codec;
    this.worldRequirement = worldRequirement;
  }

  @Nullable
  public ResourceLocation getId() {
    return ModularMachineryReborn.getRequirementRegistrar().getKey(this);
  }

  /**
   * Used to display the name of this requirement to the player, either in a gui or in the log.
   * @return A text component representing the name of this requirement.
   */
  public Component getName() {
    if(getId() == null)
      return Component.literal("unknown");
    return Component.translatable("requirement." + getId().getNamespace() + "." + getId().getPath());
  }
}
