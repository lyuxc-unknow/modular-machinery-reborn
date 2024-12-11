package es.degrassi.mmreborn.common.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.MMRLogger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class MachineRecipe implements Comparable<MachineRecipe>, Recipe<RecipeInput> {

  public static final NamedMapCodec<MachineRecipeBuilder> CODEC = NamedCodec.record(instance -> instance.group(
      DefaultCodecs.RESOURCE_LOCATION.fieldOf("machine").forGetter(MachineRecipeBuilder::getMachine),
      NamedCodec.intRange(1, Integer.MAX_VALUE).fieldOf("time").forGetter(MachineRecipeBuilder::getTime),
      ComponentRequirement.CODEC.listOf().fieldOf("requirements").forGetter(MachineRecipeBuilder::getRequirements),
      NamedCodec.INT.optionalFieldOf("priority", 0).forGetter(MachineRecipeBuilder::getPrio),
      NamedCodec.BOOL.optionalFieldOf("voidFailure", true).forGetter(MachineRecipeBuilder::isVoidF),
      NamedCodec.INT.optionalFieldOf("width", 256).forGetter(MachineRecipeBuilder::getWidth),
      NamedCodec.INT.optionalFieldOf("height", 256).forGetter(MachineRecipeBuilder::getHeight),
      PositionedRequirement.POSITION_CODEC.optionalFieldOf("progressPosition", new PositionedRequirement(74, 8)).forGetter(MachineRecipeBuilder::getProgressPosition)
  ).apply(instance, MachineRecipeBuilder::new), "Machine recipe");


  public final List<Component> textsToRender = new LinkedList<>();

  @Override
  public boolean matches(@NotNull RecipeInput container, @NotNull Level level) {
    return false;
  }

  @Override
  public @NotNull ItemStack assemble(@NotNull RecipeInput container, HolderLookup.@NotNull Provider registryAccess) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canCraftInDimensions(int i, int i1) {
    return false;
  }

  @Override
  public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registryAccess) {
    return ItemStack.EMPTY;
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return RecipeRegistration.RECIPE_SERIALIZER.get();
  }

  @Override
  public @NotNull RecipeType<?> getType() {
    return RecipeRegistration.RECIPE_TYPE.get();
  }

  private final ResourceLocation owningMachine;
  @Getter(AccessLevel.NONE)
  private final int tickTime;
  private final List<ComponentRequirement<?, ?>> recipeRequirements = Lists.newArrayList();
  private final int configuredPriority;
  private final boolean voidPerTickFailure;
  private final PositionedRequirement progressPosition;
  private final int width, height;

  public MachineRecipe(ResourceLocation owningMachine, int tickTime, int configuredPriority, boolean voidPerTickFailure, int width, int height, PositionedRequirement progressPosition) {
    this(owningMachine, tickTime, configuredPriority, voidPerTickFailure, false, width, height, progressPosition);
  }

  public MachineRecipe(ResourceLocation owningMachine, int tickTime, int configuredPriority, boolean voidPerTickFailure, boolean copy, int width, int height, PositionedRequirement progressPosition) {
    this.owningMachine = owningMachine;
    this.tickTime = tickTime;
    this.configuredPriority = configuredPriority;
    this.voidPerTickFailure = voidPerTickFailure;
    this.progressPosition = progressPosition;
    this.width = width;
    this.height = height;
  }

  public ResourceLocation getOwningMachineIdentifier() {
    return owningMachine;
  }

  public List<ComponentRequirement<?, ?>> getCraftingRequirements() {
    return Collections.unmodifiableList(recipeRequirements);
  }

  public void addRequirement(ComponentRequirement<?, ?> requirement) {
    if (requirement instanceof RequirementEnergy) {
      for (ComponentRequirement<?, ?> req : this.recipeRequirements) {
        if (req instanceof RequirementEnergy && req.getActionType() == requirement.getActionType()) {
          throw new IllegalStateException("Tried to add multiple energy requirements for the same ioType! Please only add one for each ioType!");
        }
      }
    }
    this.recipeRequirements.add(requirement);
  }

  public int getRecipeTotalTickTime() {
    return this.tickTime;
  }

  public boolean doesCancelRecipeOnPerTickFailure() {
    return this.voidPerTickFailure;
  }

  @Nullable
  public DynamicMachine getOwningMachine() {
    return ModularMachineryReborn.MACHINES.get(getOwningMachineIdentifier());
  }

  public MachineRecipe copy() {
    return copy(owningMachine, List.of());
  }

  public MachineRecipe copy(ResourceLocation newOwningMachineIdentifier, List<RecipeModifier> modifiers) {
    MachineRecipe copy = new MachineRecipe(
        newOwningMachineIdentifier,
        Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypeRegistration.DURATION.get(), null, this.getRecipeTotalTickTime(), false)),
        this.getConfiguredPriority(),
        this.doesCancelRecipeOnPerTickFailure(),
        true,
        this.width,
        this.height,
        this.progressPosition
    );

    for (ComponentRequirement<?, ?> requirement : this.getCraftingRequirements()) {
      copy.addRequirement(requirement.deepCopyModified(modifiers));
    }
    return copy;
  }

  @Override
  public int compareTo(MachineRecipe o) {
    return Integer.compare(buildWeight(), o.buildWeight());
  }

  private int buildWeight() {
    return configuredPriority;
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("owningMachine", owningMachine.toString());
    json.addProperty("tickTime", tickTime);
    JsonArray recipeRequirements = new JsonArray();
    this.recipeRequirements.forEach(req -> recipeRequirements.add(req.asJson()));
    json.add("recipeRequirements", recipeRequirements);
    json.addProperty("configuredPriority", configuredPriority);
    json.addProperty("voidPerTickFailure", voidPerTickFailure);
    json.add("progressPosition", progressPosition.asJson());
    return json;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }

  @Getter
  public static class MachineRecipeBuilder {
    private final ResourceLocation machine;
    private final PositionedRequirement progressPosition;
    private final int time;
    private final int width, height;
    private int prio;
    private final List<ComponentRequirement<?, ?>> requirements;
    private boolean voidF;

    public MachineRecipeBuilder(ResourceLocation machine, int time, int width, int height, PositionedRequirement progressPosition) {
      this.requirements = new LinkedList<>();
      this.machine = machine;
      this.time = time;
      this.progressPosition = progressPosition;
      this.width = width;
      this.height = height;
    }

    public void withPriority(int prio) {
      this.prio = prio;
    }

    public void shouldVoidOnFailure(boolean v) {
      this.voidF = v;
    }

    public void addRequirement(ComponentRequirement<?, ?> requirement) {
      requirements.add(requirement);
    }

    public MachineRecipeBuilder(ResourceLocation machine, int time, List<ComponentRequirement<?, ?>> requirements, int prio, boolean voidF, int width, int height, PositionedRequirement progressPosition) {
      this.machine = machine;
      this.time = time;
      this.requirements = requirements;
      this.prio = prio;
      this.voidF = voidF;
      this.progressPosition = progressPosition;
      this.width = width;
      this.height = height;
    }

    public MachineRecipeBuilder(MachineRecipe recipe) {
      this(recipe.getOwningMachineIdentifier(), recipe.tickTime, recipe.recipeRequirements, recipe.configuredPriority, recipe.voidPerTickFailure, recipe.width, recipe.height, recipe.progressPosition);
    }

    public MachineRecipe build() {
      try {
        MMRLogger.INSTANCE.info("Building recipe...");
        MachineRecipe recipe = new MachineRecipe(machine, time, prio, voidF, width, height, progressPosition);
        requirements.forEach(recipe::addRequirement);
        MMRLogger.INSTANCE.info("Finished building recipe {}", recipe);
        return recipe;
      } catch (Exception ignored) {
      }
      return null;
    }
  }
}
