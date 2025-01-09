package es.degrassi.mmreborn.common.machine;

import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.util.MMRSoundType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public record Sounds(
    SoundEvent ambientSound,
    MMRSoundType interaction
) {
  public static final Sounds DEFAULT = new Sounds(SoundEvent.createVariableRangeEvent(ResourceLocation.parse("")), MMRSoundType.DEFAULT);

  public static final NamedCodec<Sounds> CODEC = NamedCodec.record(soundInstance -> soundInstance.group(
      DefaultCodecs.SOUND_EVENT.optionalFieldOf("ambient", SoundEvent.createVariableRangeEvent(ResourceLocation.parse(""))).forGetter(Sounds::ambientSound),
      MMRSoundType.CODEC.optionalFieldOf("interaction", MMRSoundType.DEFAULT).forGetter(Sounds::interaction)
  ).apply(soundInstance, Sounds::new), "Sounds codec");
}
