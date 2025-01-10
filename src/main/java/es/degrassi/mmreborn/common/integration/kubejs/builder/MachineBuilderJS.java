package es.degrassi.mmreborn.common.integration.kubejs.builder;

import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.event.KubeEvent;
import es.degrassi.mmreborn.api.Structure;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.Sounds;
import es.degrassi.mmreborn.common.manager.crafting.MachineStatus;
import es.degrassi.mmreborn.common.util.MachineModelLocation;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MachineBuilderJS {
  @NotNull
  private final ResourceLocation id;
  private String name;
  private String color;
  private Integer intColor;
  private Structure structure;
  private MachineModelLocation controllerModel;
  private final List<ModifierReplacement> modifiers;
  private final Map<MachineStatus, Sounds> sounds;

  public MachineBuilderJS(@NotNull ResourceLocation id) {
    this.id = id;
    modifiers = new LinkedList<>();
    sounds = new EnumMap<>(MachineStatus.class);
  }

  public MachineBuilderJS name(String name) {
    this.name = name;
    return this;
  }

  public MachineBuilderJS color(String color) {
    this.color = color;
    return this;
  }

  public MachineBuilderJS color(Integer color) {
    this.intColor = color;
    return this;
  }

  public MachineBuilderJS structure(StructureBuilderJS structure) {
    this.structure = structure.build();
    return this;
  }

  public MachineBuilderJS controllerModel(MachineModelLocation modelLocation) {
    this.controllerModel = modelLocation;
    return this;
  }

  public MachineBuilderJS addModifier(ModifierBuilderJS modifier) {
    this.modifiers.add(modifier.build());
    return this;
  }

  public MachineBuilderJS sound(MachineStatus status, Sounds sounds) {
    this.sounds.put(status, sounds);
    return this;
  }

  public DynamicMachine build() {
    if (structure == null)
      structure = Structure.EMPTY;
    DynamicMachine machine = new DynamicMachine(id, sounds);
    machine.setPattern(structure);
    machine.setControllerModel(Objects.requireNonNullElse(controllerModel, MachineModelLocation.DEFAULT));
    machine.setLocalizedName(Optional.ofNullable(name));
    if (intColor != null)
      machine.setDefinedColor(intColor);
    else if(color != null)
      machine.setDefinedColor(DefaultCodecs.HEX.decode(JsonOps.INSTANCE, new JsonPrimitive(color)).result().orElse(new Pair<>(Config.toInt(MMRConfig.get().general_casing_color.get(), 0xFF4900), null)).getFirst());

    machine.setModifiers(modifiers);

    return machine;
  }

  @Getter
  public static class MachineKubeEvent implements KubeEvent {
    private final List<MachineBuilderJS> builders = new LinkedList<>();

    public MachineBuilderJS create(ResourceLocation id) {
      MachineBuilderJS builder = new MachineBuilderJS(id);
      builders.add(builder);
      return builder;
    }
  }
}
