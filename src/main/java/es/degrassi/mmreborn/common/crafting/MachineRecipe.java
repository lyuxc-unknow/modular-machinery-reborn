package es.degrassi.mmreborn.common.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedSizedRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementEnergy;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.IOType;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class MachineRecipe implements Comparable<MachineRecipe>, Recipe<RecipeInput> {
  public static final NamedMapCodec<MachineRecipeBuilder> CODEC = NamedCodec.record(instance -> instance.group(
      DefaultCodecs.RESOURCE_LOCATION.fieldOf("machine").forGetter(MachineRecipeBuilder::getMachine),
      NamedCodec.intRange(1, Integer.MAX_VALUE).fieldOf("time").forGetter(MachineRecipeBuilder::getTime),
      RecipeRequirement.CODEC.listOf().fieldOf("requirements").forGetter(MachineRecipeBuilder::getRequirements),
      NamedCodec.INT.optionalFieldOf("priority", 0).forGetter(MachineRecipeBuilder::getPrio),
      NamedCodec.BOOL.optionalFieldOf("voidFailure", true).forGetter(MachineRecipeBuilder::isVoidF),
      NamedCodec.INT.optionalFieldOf("width", 256).forGetter(MachineRecipeBuilder::getWidth),
      NamedCodec.INT.optionalFieldOf("height", 256).forGetter(MachineRecipeBuilder::getHeight),
      NamedCodec.BOOL.optionalFieldOf("renderProgress", true).forGetter(MachineRecipeBuilder::isShouldRenderProgress),
      PositionedRequirement.POSITION_CODEC.optionalFieldOf("progressPosition", new PositionedRequirement(74, 8)).forGetter(MachineRecipeBuilder::getProgressPosition)
  ).apply(instance, MachineRecipeBuilder::new), "Machine recipe");

  private final ResourceLocation owningMachine;
  @Getter(AccessLevel.NONE)
  private final int tickTime;
  private final List<RecipeRequirement<?, ?>> recipeRequirements = Lists.newArrayList();
  private final int configuredPriority;
  private final boolean voidPerTickFailure;
  private final PositionedRequirement progressPosition;
  private final int width, height;
  public final List<Component> textsToRender = new LinkedList<>();
  public final List<Pair<PositionedSizedRequirement, Object>> chanceTexts = new LinkedList<>();
  private final boolean shouldRenderProgress;

  private boolean modified = false;

  public MachineRecipe(ResourceLocation owningMachine, int tickTime, int configuredPriority,
                       boolean voidPerTickFailure, int width, int height,
                       boolean shouldRenderProgress, PositionedRequirement progressPosition) {
    this.owningMachine = owningMachine;
    this.tickTime = tickTime;
    this.configuredPriority = configuredPriority;
    this.voidPerTickFailure = voidPerTickFailure;
    this.progressPosition = progressPosition;
    this.shouldRenderProgress = shouldRenderProgress;
    this.width = width;
    this.height = height;
  }

  public ResourceLocation getOwningMachineIdentifier() {
    return owningMachine;
  }

  public List<RecipeRequirement<?, ?>> getRequirements() {
    return recipeRequirements;
  }

  public void addRequirement(RecipeRequirement<?, ?> requirement) {
    if (requirement.requirement() instanceof RequirementEnergy) {
      for (RecipeRequirement<?, ?> req : this.getRequirements()) {
        if (req.requirement() instanceof RequirementEnergy && req.requirement().getMode() == requirement.requirement().getMode()) {
          throw new IllegalStateException("Tried to add multiple energy requirements for the same ioType! Please only add one for each ioType!");
        }
      }
    }
    if (requirement.isModified()) setModified(true);
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
        Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypeRegistration.DURATION.get(), IOType.INPUT,
            this.getRecipeTotalTickTime(), false)),
        this.getConfiguredPriority(),
        this.doesCancelRecipeOnPerTickFailure(),
        this.width,
        this.height,
        this.shouldRenderProgress,
        this.progressPosition
    );

    for (RecipeRequirement<?, ?> requirement : this.getRequirements()) {
      copy.addRequirement(new RecipeRequirement<>(requirement.deepCopyModified(modifiers)));
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
    json.addProperty("shouldRenderProgress", shouldRenderProgress);
    json.add("progressPosition", progressPosition.asJson());
    json.addProperty("modifiedByAU", modified);
    return json;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }

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
  public @NotNull MachineRecipeSerializer getSerializer() {
    return RecipeRegistration.RECIPE_SERIALIZER.get();
  }

  @Override
  public @NotNull RecipeType<?> getType() {
    return RecipeRegistration.RECIPE_TYPE.get();
  }

  @Getter
  public static class MachineRecipeBuilder {
    private final ResourceLocation machine;
    private final PositionedRequirement progressPosition;
    private final int time;
    private final int width, height;
    private int prio;
    private boolean shouldRenderProgress;
    private final List<RecipeRequirement<?, ?>> requirements;
    private boolean voidF;
    private boolean modified;

    public MachineRecipeBuilder(ResourceLocation machine, int time, int width, int height, PositionedRequirement progressPosition) {
      this.requirements = new LinkedList<>();
      this.machine = machine;
      this.time = time;
      this.progressPosition = progressPosition;
      this.width = width;
      this.height = height;
    }

    public void modified(boolean modified) {
      this.modified = modified;
    }

    public void withPriority(int prio) {
      this.prio = prio;
    }

    public void shouldVoidOnFailure(boolean v) {
      this.voidF = v;
    }

    public void shouldRenderProgress(boolean v) {
      this.shouldRenderProgress = v;
    }

    public void addRequirement(RecipeRequirement<?, ?> requirement) {
      requirements.add(requirement);
      if (requirement.isModified())
        modified(true);
    }

    public MachineRecipeBuilder(ResourceLocation machine, int time, List<RecipeRequirement<?, ?>> requirements,
                                int prio, boolean voidF, int width, int height,
                                boolean shouldRenderProgress, PositionedRequirement progressPosition) {
      this.machine = machine;
      this.time = time;
      this.requirements = requirements;
      this.prio = prio;
      this.voidF = voidF;
      this.progressPosition = progressPosition;
      this.shouldRenderProgress = shouldRenderProgress;
      this.width = width;
      this.height = height;
    }

    public MachineRecipeBuilder(MachineRecipe recipe) {
      this(recipe.getOwningMachineIdentifier(), recipe.tickTime, recipe.recipeRequirements, recipe.configuredPriority,
          recipe.voidPerTickFailure, recipe.width, recipe.height, recipe.shouldRenderProgress,
          recipe.progressPosition);
      modified(recipe.modified);
    }

    public MachineRecipe build() {
      try {
        MMRLogger.INSTANCE.info("Building recipe...");
        MachineRecipe recipe = new MachineRecipe(machine, time, prio, voidF, width, height, shouldRenderProgress, progressPosition);
        requirements.forEach(recipe::addRequirement);
        if (!recipe.modified)
          recipe.setModified(modified);
        MMRLogger.INSTANCE.info("Finished building recipe {}", recipe);
        return recipe;
      } catch (Exception ignored) {
      }
      return null;
    }
  }
}
