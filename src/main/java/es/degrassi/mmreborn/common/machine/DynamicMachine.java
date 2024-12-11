package es.degrassi.mmreborn.common.machine;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.Structure;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.crafting.ActiveMachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.util.MachineModelLocation;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

@Getter
@Setter
public class DynamicMachine {
  public static final NamedCodec<DynamicMachine> CODEC = NamedCodec.record(instance -> instance.group(
      DefaultCodecs.RESOURCE_LOCATION.fieldOf("registryName").forGetter(DynamicMachine::getRegistryName),
      NamedCodec.STRING.optionalFieldOf("localizedName").forGetter(machine -> machine.localizedName),
      Structure.CODEC.fieldOf("structure").forGetter(machine -> machine.pattern),
      DefaultCodecs.HEX.optionalFieldOf("color", Config.machineColor).forGetter(machine -> machine.definedColor),
      MachineModelLocation.CODEC.optionalFieldOf("controller", MachineModelLocation.DEFAULT).forGetter(DynamicMachine::getControllerModel)
  ).apply(instance, (registryName, localizedName, pattern, color, controllerModel) -> {
    DynamicMachine machine = new DynamicMachine(registryName);
    machine.setPattern(pattern);
    machine.setLocalizedName(localizedName);
    machine.setDefinedColor(color);
    machine.setControllerModel(controllerModel);
    return machine;
  }), "Dynamic Machine");

  public static final DynamicMachine DUMMY = new DynamicMachine(ModularMachineryReborn.rl("dummy"));

  @Nonnull
  private ResourceLocation registryName;
  private Optional<String> localizedName;
  private Structure pattern = Structure.EMPTY;
  private int definedColor = Config.machineColor;
  private MachineModelLocation controllerModel;

  public DynamicMachine(@Nonnull ResourceLocation registryName) {
    this.registryName = registryName;
  }

  public String getLocalizedName() {
    return getName().getString();
  }

  public Component getName() {
    String localizationKey = registryName.getNamespace() + "." + registryName.getPath();
    return Component.translatableWithFallback(localizationKey, localizedName.orElse(localizationKey));
  }

  public int getMachineColor() {
    return definedColor;
  }

  public RecipeCraftingContext createContext(
      ActiveMachineRecipe activeRecipe,
      MachineControllerEntity controller,
      Collection<MachineComponent<?>> taggedComponents
  ) {
    if (!activeRecipe.getRecipe().getOwningMachineIdentifier().equals(getRegistryName())) {
      throw new IllegalArgumentException("Tried to create context for a recipe that doesn't belong to the referenced machine!");
    }
    RecipeCraftingContext context = new RecipeCraftingContext(activeRecipe, controller);
    taggedComponents.forEach(context::addComponent);
    return context;
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("registryName", registryName.toString());
    json.addProperty("localizedName", localizedName.orElse("null"));
    json.add("pattern", pattern.asJson());
    json.addProperty("definedColor", definedColor);
    return json;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }
}
