package es.degrassi.mmreborn.common.modifier;

import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.machine.IOType;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;

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

}
