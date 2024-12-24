package es.degrassi.mmreborn.common.integration.kubejs.builder;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;

public class ModifierBuilderJS {
  private BlockIngredient ingredient;
  private final List<RecipeModifier> modifiers = new LinkedList<>();
  private BlockPos position;

  @HideFromJS
  private ModifierBuilderJS() {}

  public static ModifierBuilderJS create() {
    return new ModifierBuilderJS();
  }

  public ModifierBuilderJS ingredient(JsonElement ingredient) {
    this.ingredient = BlockIngredient.CODEC.read(JsonOps.INSTANCE, ingredient).getOrThrow();
    return this;
  }

  /**
   *
   * @param x relative to X position of controller
   * @param y relative to Y position of controller
   * @param z relative to Z position of controller
   * @return position relative to controller
   */
  public ModifierBuilderJS position(int x, int y, int z) {
    this.position = new BlockPos(x, y, z);
    return this;
  }

  public ModifierBuilderJS addModifier(RecipeModifierBuilderJS modifier) {
    this.modifiers.add(modifier.build());
    return this;
  }

  public ModifierReplacement build() {
    return new ModifierReplacement(ingredient, modifiers, position);
  }

  public static class RecipeModifierBuilderJS {
    private RequirementType<?> target = RequirementTypeRegistration.DURATION.get();
    private IOType mode = IOType.INPUT;
    private float modifier;
    private int operation;
    private boolean chance;

    @HideFromJS
    private RecipeModifierBuilderJS() {}

    public static RecipeModifierBuilderJS create() {
      return new RecipeModifierBuilderJS();
    }

    public RecipeModifierBuilderJS target(ResourceLocation target) {
      this.target = ModularMachineryReborn.getRequirementRegistrar().get(target);
      if (this.target == null) throw new IllegalArgumentException("Invalid recipe target");
      return this;
    }

    public RecipeModifierBuilderJS addition() {
      this.operation = 0;
      return this;
    }

    public RecipeModifierBuilderJS multiply() {
      this.operation = 1;
      return this;
    }

    public RecipeModifierBuilderJS input() {
      this.mode = IOType.INPUT;
      return this;
    }

    public RecipeModifierBuilderJS output() {
      this.mode = IOType.OUTPUT;
      return this;
    }

    public RecipeModifierBuilderJS modifier(float modifier) {
      this.modifier = modifier;
      return this;
    }

    public RecipeModifierBuilderJS affectsChance() {
      this.chance = true;
      return this;
    }

    public RecipeModifierBuilderJS notAffectsChance() {
      this.chance = false;
      return this;
    }

    public RecipeModifier build() {
      return new RecipeModifier(target, mode, modifier, operation, chance);
    }
  }
}
