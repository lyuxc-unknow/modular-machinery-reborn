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
import java.util.Collection;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@Getter
@Setter
public class DynamicMachine {
  public static final NamedCodec<DynamicMachine> CODEC = NamedCodec.record(instance -> instance.group(
    DefaultCodecs.RESOURCE_LOCATION.fieldOf("registryName").forGetter(DynamicMachine::getRegistryName),
    NamedCodec.STRING.fieldOf("localizedName").forGetter(machine -> machine.localizedName),
    Structure.CODEC.fieldOf("structure").forGetter(machine -> machine.pattern),
    NamedCodec.BOOL.optionalFieldOf("requiresBlueprint", true).forGetter(machine -> machine.requiresBlueprint),
    DefaultCodecs.HEX.optionalFieldOf("color", Config.machineColor).forGetter(machine -> machine.definedColor)
  ).apply(instance, (registryName, localizedName, pattern, requiresBlueprint, color) -> {
    DynamicMachine machine = new DynamicMachine(registryName);
    machine.setPattern(pattern);
    machine.setLocalizedName(localizedName);
    machine.setRequiresBlueprint(requiresBlueprint);
    machine.setDefinedColor(color);
    return machine;
  }), "Dynamic Machine");

  public static final DynamicMachine DUMMY = new DynamicMachine(ModularMachineryReborn.rl("dummy"));

  @Nonnull
  private ResourceLocation registryName;
  private String localizedName = "";
  private Structure pattern = Structure.EMPTY;
  private int definedColor = Config.machineColor;

  private boolean requiresBlueprint = false;

  public DynamicMachine(@Nonnull ResourceLocation registryName) {
    this.registryName = registryName;
  }

  public void setRequiresBlueprint() {
    this.requiresBlueprint = true;
  }

  public boolean requiresBlueprint() {
    return requiresBlueprint;
  }

  @OnlyIn(Dist.CLIENT)
  public String getLocalizedName() {
    String localizationKey = registryName.getNamespace() + "." + registryName.getPath();
    return I18n.exists(localizationKey) ? I18n.get(localizationKey) :
      localizedName != null ? localizedName : localizationKey;
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
    json.addProperty("localizedName", localizedName);
    json.add("pattern", pattern.asJson());
    json.addProperty("definedColor", definedColor);
    json.addProperty("requiresBlueprint", requiresBlueprint);
    return json;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }
}
