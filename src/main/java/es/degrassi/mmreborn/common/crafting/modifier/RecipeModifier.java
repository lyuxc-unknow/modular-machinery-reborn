package es.degrassi.mmreborn.common.crafting.modifier;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RecipeModifier {
  public static final NamedCodec<RecipeModifier> CODEC = NamedCodec.record(instance -> instance.group(
      DefaultCodecs.RESOURCE_LOCATION.optionalFieldOf("target", ModularMachineryReborn.rl("duration")).forGetter(modifier -> ModularMachineryReborn.getRequirementRegistrar().getKey(modifier.target)),
      NamedCodec.enumCodec(IOType.class).optionalFieldOf("mode", IOType.INPUT).forGetter(RecipeModifier::getIOTarget),
      NamedCodec.FLOAT.fieldOf("modifier").forGetter(RecipeModifier::getModifier),
      NamedCodec.intRange(0, 1).fieldOf("operation").forGetter(RecipeModifier::getOperation),
      NamedCodec.BOOL.optionalFieldOf("chance", false).forGetter(RecipeModifier::affectsChance)
  ).apply(instance, (reqId, mode, modifier, operation, chance) -> new RecipeModifier(
      Objects.requireNonNullElse(ModularMachineryReborn.getRequirementRegistrar().get(reqId), RequirementTypeRegistration.DURATION.get()),
      mode,
      modifier,
      operation,
      chance
  )), "Recipe Modifier");

  private static final List<RequirementType<?>> blacklist = new LinkedList<>();

  static {
    addToBlacklist(RequirementTypeRegistration.DIMENSION.get());
    addToBlacklist(RequirementTypeRegistration.BIOME.get());
    addToBlacklist(RequirementTypeRegistration.WEATHER.get());
    addToBlacklist(RequirementTypeRegistration.TIME.get());
    addToBlacklist(RequirementTypeRegistration.CHUNKLOAD.get());
  }

  public static void addToBlacklist(RequirementType<?> requirementType) {
    if (blacklist.contains(requirementType)) return;
    blacklist.add(requirementType);
  }

  public static final int OPERATION_ADD = 0;
  public static final int OPERATION_MULTIPLY = 1;

  @Getter
  protected final RequirementType<?> target;
  protected final IOType ioTarget;
  @Getter
  protected final float modifier;
  @Getter
  protected final int operation;
  protected final boolean chance;

  public RecipeModifier(RequirementType<?> target, IOType ioTarget, float modifier, int operation, boolean affectsChance) {
    if (blacklist.contains(target)) throw new IllegalArgumentException(ModularMachineryReborn.getRequirementRegistrar().getKey(target) + " is not valid for a recipe modifier");
    this.target = target;
    this.ioTarget = ioTarget;
    this.modifier = modifier;
    this.operation = operation;
    this.chance = affectsChance;
  }

  public IOType getIOTarget() {
    return ioTarget;
  }

  public boolean affectsChance() {
    return chance;
  }

  public static float applyModifiers(RecipeCraftingContext context, ComponentRequirement<?, ?> in, float value, boolean isChance) {
    RequirementType<?> target = in.getRequirementType();
    return applyModifiers(context.getModifiers(target), target, in.getActionType(), value, isChance);
  }

  public static float applyModifiers(Collection<RecipeModifier> modifiers, ComponentRequirement<?, ?> in, float value, boolean isChance) {
    return applyModifiers(modifiers, in.getRequirementType(), in.getActionType(), value, isChance);
  }

  public static float applyModifiers(Collection<RecipeModifier> modifiers, RequirementType<?> target, IOType ioType, float value, boolean isChance) {
    List<RecipeModifier> applicable = modifiers
      .stream()
      .filter(mod -> mod.getTarget().equals(target))
      .filter(mod -> mod.getIOTarget() == ioType)
      .filter(mod -> mod.affectsChance() == isChance)
      .toList();
    float add = OPERATION_ADD;
    float mul = OPERATION_MULTIPLY;
    for (RecipeModifier mod : applicable) {
      if (mod.getOperation() == 0) {
        add += mod.getModifier();
      } else if (mod.getOperation() == 1) {
        mul *= mod.getModifier();
      } else {
        throw new RuntimeException("Unknown modifier operation: " + mod.getOperation());
      }
    }
    return (value + add) * mul;
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    ResourceLocation key = ModularMachineryReborn.getRequirementRegistrar().getKey(target);
    if (key == null)
      key = ModularMachineryReborn.rl("duration");
    json.addProperty("target", key.toString());
    json.addProperty("mode", ioTarget.getSerializedName());
    json.addProperty("modifier", modifier);
    json.addProperty("operation", operation());
    json.addProperty("chance", chance);
    return json;
  }

  private String getTargetValue() {
    return ModularMachineryReborn.getRequirementRegistrar().getKey(target).getPath();
  }

  private String operation() {
    return operation == 0 ? "add" : operation == 1 ? "multiply" : "";
  }

  public Component getDescription() {
    if (target == RequirementTypeRegistration.DURATION.get() || target == RequirementTypeRegistration.LOOT_TABLE.get())
      return Component.translatable("mmr.recipe.modifier." + getTargetValue() + "." + operation(), modifier);
    return Component.translatable("mmr.recipe.modifier." + getTargetValue() + "." + operation(), modifier, ioTarget.getSerializedName(), chance);
  }
}
