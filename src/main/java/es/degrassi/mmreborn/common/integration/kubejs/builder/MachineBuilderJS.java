package es.degrassi.mmreborn.common.integration.kubejs.builder;

import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.event.KubeEvent;
import es.degrassi.mmreborn.api.Structure;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class MachineBuilderJS {
  @NotNull
  private final ResourceLocation id;
  private String name;
  private String color;
  private Integer intColor;
  private Structure structure;

  public MachineBuilderJS(@NotNull ResourceLocation id) {
    this.id = id;
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

  public DynamicMachine build() {
    if (structure == null)
      throw new IllegalArgumentException("Structure is invalid");
    DynamicMachine machine = new DynamicMachine(id);
    machine.setPattern(structure);
    if (name != null)
      machine.setLocalizedName(name);
    if (intColor != null)
      machine.setDefinedColor(intColor);
    else if(color != null)
      machine.setDefinedColor(DefaultCodecs.HEX.decode(JsonOps.INSTANCE, new JsonPrimitive(color)).result().orElse(new Pair<>(MMRConfig.get().general.general_casing_color, null)).getFirst());
    return machine;
  }

  @SuppressWarnings("LombokGetterMayBeUsed")
  public static class MachineKubeEvent implements KubeEvent {
    private final List<MachineBuilderJS> builders = new LinkedList<>();

    public MachineBuilderJS create(String id) {
      try {
        ResourceLocation machineId = ResourceLocation.parse(id);
        MachineBuilderJS builder = new MachineBuilderJS(machineId);
        builders.add(builder);
        return builder;
      } catch (Exception ignored) {}
      throw new IllegalArgumentException("Invalid machine id");
    }

    public List<MachineBuilderJS> getBuilders() {
      return builders;
    }
  }
}
