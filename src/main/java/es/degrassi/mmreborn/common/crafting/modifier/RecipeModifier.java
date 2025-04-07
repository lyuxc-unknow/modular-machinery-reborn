package es.degrassi.mmreborn.common.crafting.modifier;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RecipeModifier {
  public static final NamedCodec<RecipeModifier> CODEC = NamedCodec.record(instance -> instance.group(
      DefaultCodecs.RESOURCE_LOCATION.fieldOf("target").forGetter(modifier -> ModularMachineryReborn.getRequirementRegistrar().getKey(modifier.target)),
      NamedCodec.enumCodec(IOType.class).optionalFieldOf("mode", IOType.INPUT).forGetter(RecipeModifier::getIOTarget),
      NamedCodec.FLOAT.fieldOf("modifier").forGetter(RecipeModifier::getModifier),
      NamedCodec.enumCodec(ModifierOperation.class).fieldOf("operation").forGetter(RecipeModifier::getOperation),
      NamedCodec.BOOL.optionalFieldOf("chance", false).forGetter(RecipeModifier::affectsChance)
  ).apply(instance, (reqId, mode, modifier, operation, chance) -> new RecipeModifier(
      Objects.requireNonNullElse(ModularMachineryReborn.getRequirementRegistrar().get(reqId), RequirementTypeRegistration.SPEED.get()),
      mode,
      modifier,
      operation,
      chance
  )), "Recipe Modifier");

  public enum ModifierOperation {
    MULTIPLY, ADDITION;

    public boolean isAddition() {
      return this == ADDITION;
    }

    public boolean isMultiply() {
      return this == MULTIPLY;
    }
  }

  private static final List<RequirementType<?>> blacklist = Lists.newArrayList();

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
  protected final ModifierOperation operation;
  protected final boolean chance;

  public RecipeModifier(RequirementType<?> target, IOType ioTarget, float modifier, ModifierOperation operation, boolean affectsChance) {
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

  public static float applyModifiers(ICraftingContext context, RecipeRequirement<?, ?> in, float value, boolean isChance) {
    RequirementType<?> target = in.getType();
    return applyModifiers(context.getModifiers(target), target, in.requirement().getMode(), value, isChance);
  }

  public static float applyModifiers(Collection<RecipeModifier> modifiers, RecipeRequirement<?, ?> in, float value, boolean isChance) {
    return applyModifiers(modifiers, in.getType(), in.requirement().getMode(), value, isChance);
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
      if (mod.getOperation().isAddition()) {
        add += mod.getModifier();
      } else if (mod.getOperation().isMultiply()) {
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
      key = ModularMachineryReborn.rl("speed");
    json.addProperty("target", key.toString());
    json.addProperty("mode", ioTarget.getSerializedName());
    json.addProperty("modifier", modifier);
    json.addProperty("operation", operation());
    json.addProperty("chance", chance);
    return json;
  }

  public CompoundTag asTag() {
    CompoundTag tag = new CompoundTag();
    ResourceLocation key = ModularMachineryReborn.getRequirementRegistrar().getKey(target);
    if (key == null)
      key = ModularMachineryReborn.rl("speed");
    tag.putString("target", key.toString());
    tag.putString("mode", ioTarget.getSerializedName());
    tag.putFloat("modifier", modifier);
    tag.putString("operation", operation());
    tag.putBoolean("chance", chance);
    return tag;
  }

  private String getTargetValue() {
    return ModularMachineryReborn.getRequirementRegistrar().getKey(target).getPath();
  }

  private String operation() {
    return operation.isAddition() ? "add" : "multiply";
  }

  public Component getDescription() {
    if (target == RequirementTypeRegistration.SPEED.get() || target == RequirementTypeRegistration.LOOT_TABLE.get())
      return Component.translatable("mmr.recipe.modifier." + getTargetValue() + "." + operation(), modifier);
    return Component.translatable("mmr.recipe.modifier." + getTargetValue() + "." + operation(), modifier, ioTarget.getSerializedName(), chance);
  }
}
