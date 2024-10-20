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
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.MMRLogger;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class MachineRecipe implements Comparable<MachineRecipe>, Recipe<RecipeInput> {

  public static final NamedMapCodec<MachineRecipeBuilder> CODEC = NamedCodec.record(instance -> instance.group(
    DefaultCodecs.RESOURCE_LOCATION.fieldOf("id").forGetter(MachineRecipeBuilder::getId),
    DefaultCodecs.RESOURCE_LOCATION.fieldOf("machine").forGetter(MachineRecipeBuilder::getMachine),
    NamedCodec.intRange(1, Integer.MAX_VALUE).fieldOf("time").forGetter(MachineRecipeBuilder::getTime),
    ComponentRequirement.CODEC.listOf().fieldOf("requirements").forGetter(MachineRecipeBuilder::getRequirements),
    NamedCodec.INT.optionalFieldOf("priority", 0).forGetter(MachineRecipeBuilder::getPrio),
    NamedCodec.BOOL.optionalFieldOf("voidFailure", true).forGetter(MachineRecipeBuilder::isVoidF)
  ).apply(instance, MachineRecipeBuilder::new), "Machine recipe");

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

  private static int counter = 0;

  private final int sortId;
  private final ResourceLocation owningMachine, id;
  @Getter(AccessLevel.NONE)
  private final int tickTime;
  private final List<ComponentRequirement<?, ?>> recipeRequirements = Lists.newArrayList();
  private final int configuredPriority;
  private final boolean voidPerTickFailure;

  public MachineRecipe(ResourceLocation id, ResourceLocation owningMachine, int tickTime, int configuredPriority, boolean voidPerTickFailure) {
    this(id, owningMachine, tickTime, configuredPriority, voidPerTickFailure, false);
    counter++;
  }

  public MachineRecipe(ResourceLocation id, ResourceLocation owningMachine, int tickTime, int configuredPriority, boolean voidPerTickFailure, boolean copy) {
    this.id = id;
    this.sortId = counter;
    this.owningMachine = owningMachine;
    this.tickTime = tickTime;
    this.configuredPriority = configuredPriority;
    this.voidPerTickFailure = voidPerTickFailure;
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
    return new MachineRecipe(id, owningMachine, tickTime, configuredPriority, voidPerTickFailure, true);
  }

  public MachineRecipe copy(Function<ResourceLocation, ResourceLocation> registryNameChange, ResourceLocation newOwningMachineIdentifier, List<RecipeModifier> modifiers) {
    MachineRecipe copy = new MachineRecipe(
      registryNameChange.apply(getId()),
      newOwningMachineIdentifier,
      Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypeRegistration.DURATION.get(), null, this.getRecipeTotalTickTime(), false)),
      this.getConfiguredPriority(),
      this.doesCancelRecipeOnPerTickFailure(),
      true
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
    int weightOut = sortId;
    for (ComponentRequirement<?, ?> req : this.recipeRequirements) {
      if (req.getActionType() == IOType.OUTPUT) {
        continue;
      }
      weightOut -= req.getSortingWeight();
    }
    return weightOut;
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("sortId", sortId);
    json.addProperty("owningMachine", owningMachine.toString());
    json.addProperty("id", id.toString());
    json.addProperty("tickTime", tickTime);
    JsonArray recipeRequirements = new JsonArray();
    this.recipeRequirements.forEach(req -> recipeRequirements.add(req.asJson()));
    json.add("recipeRequirements", recipeRequirements);
    json.addProperty("configuredPriority", configuredPriority);
    json.addProperty("voidPerTickFailure", voidPerTickFailure);
    return json;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }

  @Getter
  public static class MachineRecipeBuilder {
    private ResourceLocation id;
    private final ResourceLocation machine;
    private final int time;
    private int prio;
    private final List<ComponentRequirement<?, ?>> requirements;
    private boolean voidF;

    public MachineRecipeBuilder(ResourceLocation machine, int time) {
      this.requirements = new LinkedList<>();
      this.machine = machine;
      this.time = time;
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

    public MachineRecipeBuilder(ResourceLocation id, ResourceLocation machine, int time, List<ComponentRequirement<?, ?>> requirements, int prio, boolean voidF) {
      this.id = id;
      this.machine = machine;
      this.time = time;
      this.requirements = requirements;
      this.prio = prio;
      this.voidF = voidF;
    }

    public MachineRecipeBuilder(MachineRecipe recipe) {
      this(recipe.getId(), recipe.getOwningMachineIdentifier(), recipe.tickTime, recipe.recipeRequirements, recipe.configuredPriority, recipe.voidPerTickFailure);
    }

    public MachineRecipe build() {
      try {
        MachineRecipe recipe = new MachineRecipe(id, machine, time, prio, voidF);
        requirements.forEach(recipe::addRequirement);
        logBuild(recipe);
        return recipe;
      } catch(Exception ignored){}
      return  null;
    }

    public MachineRecipe build(ResourceLocation id) {
      try {
        MachineRecipe recipe = new MachineRecipe(id, machine, time, prio, voidF);
        requirements.forEach(recipe::addRequirement);
        logBuild(recipe);
        return recipe;
      } catch(Exception ignored){}
      return  null;
    }

    private static void logBuild(MachineRecipe recipe) {
      MMRLogger.INSTANCE.info("Building recipe...\n{}\nFinished building recipe with id: {}", recipe, recipe.getId());
    }
  }
}
