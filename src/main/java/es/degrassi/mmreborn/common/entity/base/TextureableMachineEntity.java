package es.degrassi.mmreborn.common.entity.base;

import com.mojang.datafixers.util.Pair;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.machine.MachineHatchType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface TextureableMachineEntity {
  NamedCodec<Pair<Optional<ResourceLocation>, Optional<ResourceLocation>>> CODEC = NamedCodec.record(instance2 -> instance2.group(
      DefaultCodecs.RESOURCE_LOCATION.optionalFieldOf("base_texture").forGetter(Pair::getFirst),
      DefaultCodecs.RESOURCE_LOCATION.optionalFieldOf("overlay_texture").forGetter(Pair::getSecond)
  ).apply(instance2, Pair::of), "Textures Pair");

  ResourceLocation getMachineBaseTexture();

  void setMachineBaseTexture(ResourceLocation newTexture);

  ResourceLocation getMachineOverlayTexture();

  void setMachineOverlayTexture(ResourceLocation newTexture);

  void setRequestModelUpdate(boolean request);

  boolean isRequestModelUpdate();

  MachineHatchType getHatchType();

  void resetTextures();
}
