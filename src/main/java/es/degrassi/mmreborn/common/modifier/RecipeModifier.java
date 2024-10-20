package es.degrassi.mmreborn.common.modifier;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import es.degrassi.mmreborn.common.crafting.IntegrationTypeHelper;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.MMRLogger;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RecipeModifier {

  public static final String IO_INPUT = "input";
  public static final String IO_OUTPUT = "output";

  public static final int OPERATION_ADD = 0;
  public static final int OPERATION_MULTIPLY = 1;

  @Nullable
  protected final RequirementType<?> target;
  protected final IOType ioTarget;
  @Getter
  protected final float modifier;
  @Getter
  protected final int operation;
  protected final boolean chance;

  public RecipeModifier(@Nullable RequirementType<?> target, IOType ioTarget, float modifier, int operation, boolean affectsChance) {
    this.target = target;
    this.ioTarget = ioTarget;
    this.modifier = modifier;
    this.operation = operation;
    this.chance = affectsChance;
  }

  @Nullable
  public RequirementType<?> getTarget() {
    return target;
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
      .filter(mod -> mod.getTarget() != null)
      .filter(mod -> mod.getTarget().equals(target))
      .filter(mod -> ioType == null || mod.getIOTarget() == ioType)
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

  public static class Deserializer implements JsonDeserializer<RecipeModifier> {

    @Override
    public RecipeModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject part = json.getAsJsonObject();
      if (!part.has("io") || !part.get("io").isJsonPrimitive() || !part.getAsJsonPrimitive("io").isString()) {
        throw new JsonParseException("'io' string-tag not found when deserializing recipemodifier!");
      }
      String ioTarget = part.getAsJsonPrimitive("io").getAsString();
      IOType ioType = IOType.getByString(ioTarget);
      if (ioType == null) {
        throw new JsonParseException("Unknown machine iotype: " + ioTarget);
      }
      if (!part.has("target") || !part.get("target").isJsonPrimitive() || !part.getAsJsonPrimitive("target").isString()) {
        throw new JsonParseException("'target' string-tag not found when deserializing recipemodifier!");
      }
      String targetStr = part.getAsJsonPrimitive("target").getAsString();
      RequirementType<?> target = RequirementTypeRegistration.MACHINE_REQUIREMENTS
        .getEntries()
        .stream()
        .filter(req -> req.getId().equals(ResourceLocation.parse(targetStr)))
        .findFirst()
        .map(DeferredHolder::get)
        .orElse(null);
      if (target == null) {
        target = IntegrationTypeHelper.searchRequirementType(targetStr);
        if (target != null) {
          MMRLogger.INSTANCE.info("[Modular Machinery]: Deprecated requirement name '{}'! Consider using {}", targetStr, target.requirementName());
        }
      }
      if (!part.has("multiplier") || !part.get("multiplier").isJsonPrimitive() || !part.getAsJsonPrimitive("multiplier").isNumber()) {
        throw new JsonParseException("'multiplier' float-tag not found when deserializing recipemodifier!");
      }
      float multiplier = part.getAsJsonPrimitive("multiplier").getAsFloat();
      if (!part.has("operation") || !part.get("operation").isJsonPrimitive() || !part.getAsJsonPrimitive("operation").isNumber()) {
        throw new JsonParseException("'operation' int-tag not found when deserializing recipemodifier!");
      }
      int operation = part.getAsJsonPrimitive("operation").getAsInt();
      if (operation < 0 || operation > 1) {
        throw new JsonParseException("There are currently only operation 0 and 1 available (add and multiply)! Found: " + operation);
      }
      boolean affectsChance = false;
      if (part.has("affectChance")) {
        if (!part.get("affectChance").isJsonPrimitive() || !part.getAsJsonPrimitive("affectChance").isBoolean()) {
          throw new JsonParseException("'affectChance', if defined, needs to be either true or false!");
        }
        affectsChance = part.getAsJsonPrimitive("affectChance").getAsBoolean();
      }
      return new RecipeModifier(target, ioType, multiplier, operation, affectsChance);
    }
  }

}
