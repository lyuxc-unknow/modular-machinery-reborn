package es.degrassi.mmreborn.common.manager.crafting;

import es.degrassi.mmreborn.api.crafting.CraftingContext;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.network.ISyncable;
import es.degrassi.mmreborn.api.network.ISyncableStuff;
import es.degrassi.mmreborn.api.network.syncable.FloatSyncable;
import es.degrassi.mmreborn.api.network.syncable.IntegerSyncable;
import es.degrassi.mmreborn.api.network.syncable.StringSyncable;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.manager.crafting.RequirementList.RequirementWithFunction;
import es.degrassi.mmreborn.common.util.Utils;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class MachineProcessorCore implements ISyncableStuff {
  private final MachineProcessor processor;
  private final MachineControllerEntity tile;
  private final Random rand = Utils.RAND;
  private final MachineRecipeFinder recipeFinder;

  @Nullable
  @Getter
  private RecipeHolder<MachineRecipe> currentRecipe;
  private ResourceLocation futureRecipeID;
  private CraftingContext context;
  @Getter
  private float recipeProgressTime;
  private int recipeTotalTime = 0;
  private boolean searchImmediately = false;
  private Phase phase = Phase.CONDITIONS;
  private boolean componentChanged = false;
  @Nullable
  @Getter
  private Component error = null;
  private boolean isLastRecipeTick = false;

  private RequirementList<MachineComponent<?>> requirementList;
  private final List<RequirementWithFunction> currentProcessRequirements = new ArrayList<>();

  public MachineProcessorCore(MachineProcessor processor, MachineControllerEntity tile) {
    this.processor = processor;
    this.tile = tile;
    this.recipeFinder = new MachineRecipeFinder(tile, new CraftingContext.Mutable(tile));
  }

  public float getRecipeTotalTime() {
    return this.recipeTotalTime;
  }

  @SuppressWarnings("unchecked")
  public void init() {
    //Search for previous recipe
    if (this.futureRecipeID != null && this.tile.getLevel() != null) {
      this.tile.getLevel().getRecipeManager()
          .byKey(this.futureRecipeID)
          .filter(holder -> holder.value() instanceof MachineRecipe)
          .map(holder -> (RecipeHolder<MachineRecipe>) holder)
          .ifPresent(this::setRecipe);
      this.futureRecipeID = null;
    }
    this.recipeFinder.init();
  }

  public void tick() {
    if (this.currentRecipe == null) {
      this.recipeFinder.findRecipe(this.searchImmediately).ifPresent(this::setRecipe);
      this.searchImmediately = false;
      this.componentChanged = false;
    }

    if (this.currentRecipe != null) {
      if (this.phase == Phase.CONDITIONS)
        this.checkConditions();

      if (this.phase == Phase.PROCESS)
        this.processRequirements();

      if (this.phase == Phase.PROCESS_TICK)
        this.processTickRequirements();

      if (this.recipeProgressTime >= this.recipeTotalTime) {
        if (this.isLastRecipeTick) {
          this.isLastRecipeTick = false;
          this.currentRecipe = null;
          this.recipeProgressTime = 0.0f;
          this.context = null;
          this.recipeFinder.findRecipe(true).ifPresent(this::setRecipe);
        } else
          this.isLastRecipeTick = true;
      }
    }
  }

  private void checkConditions() {
    for (RequirementWithFunction requirement : this.requirementList.getWorldConditions()) {
      CraftingResult result = requirement.process(this.tile.getComponentManager(), this.context);
      if (!result.isSuccess()) {
        this.setError(result.getMessage());
        return;
      }
    }

    if (this.componentChanged) {
      this.componentChanged = false;
      for (RequirementWithFunction requirement : this.requirementList.getInventoryConditions()) {
        CraftingResult result = requirement.process(this.tile.getComponentManager(), this.context);
        if (!result.isSuccess()) {
          this.setError(result.getMessage());
          return;
        }
      }
    }

    this.setRunning();
    this.phase = Phase.PROCESS;
  }

  private void processRequirements() {
    if (this.currentProcessRequirements.isEmpty()) {
      this.requirementList.getProcessRequirements().entrySet().removeIf(entry -> {
        //if the recipe is at last tick process all remaining requirements
        //Else process only requirements that have a delay lower than the current progress
        if (entry.getKey() <= this.recipeProgressTime / this.recipeTotalTime || this.isLastRecipeTick) {
          this.currentProcessRequirements.addAll(entry.getValue());
          return true;
        }
        return false;
      });
    }

    for (Iterator<RequirementWithFunction> iterator = this.currentProcessRequirements.iterator(); iterator.hasNext(); ) {
      RequirementWithFunction requirement = iterator.next();
      if (!requirement.requirement().shouldSkip(this.rand, this.context)) {
        CraftingResult result = requirement.process(this.tile.getComponentManager(), this.context);
        if (!result.isSuccess()) {
//          if (this.currentRecipe.value().isVoidPerTickFailure())
//            this.reset();
//          else
            this.setError(result.getMessage());
          return;
        }
      }
      iterator.remove();
    }

    this.setRunning();
    this.phase = Phase.PROCESS_TICK;
  }

  private void processTickRequirements() {
    if (this.currentProcessRequirements.isEmpty())
      this.currentProcessRequirements.addAll(this.requirementList.getTickableRequirements());

    for (Iterator<RequirementWithFunction> iterator = this.currentProcessRequirements.iterator(); iterator.hasNext(); ) {
      RequirementWithFunction requirement = iterator.next();
      if (!requirement.requirement().shouldSkip(this.rand, this.context)) {
        CraftingResult result = requirement.process(this.tile.getComponentManager(), this.context);
        if (!result.isSuccess()) {
//          if (this.currentRecipe.value().isVoidPerTickFailure())
//            this.reset();
//          else
            this.setError(result.getMessage());
          return;
        }
      }
      iterator.remove();
    }

    this.setRunning();
    this.phase = Phase.CONDITIONS;
    this.recipeProgressTime += this.context.getModifiedSpeed();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void setRecipe(@NotNull RecipeHolder<MachineRecipe> recipe) {
    this.currentRecipe = recipe;
    this.context = new CraftingContext(this.tile, recipe, () -> this.recipeProgressTime);
    this.recipeTotalTime = this.currentRecipe.value().getRecipeTotalTickTime();
    this.requirementList = new RequirementList<>();
    this.currentRecipe.value().getRequirements().forEach(requirement -> {
      this.requirementList.setCurrentRequirement(requirement);
      requirement.requirement().gatherRequirements((RequirementList) this.requirementList);
    });
    this.phase = Phase.CONDITIONS;
  }

  private void setRunning() {
    this.error = null;
    this.processor.setRunning();
  }

  private void setError(Component error) {
    this.error = error;
    this.processor.setError(error);
  }

  public void reset() {
    this.currentRecipe = null;
    this.futureRecipeID = null;
    this.recipeProgressTime = 0;
    this.recipeTotalTime = 0;
    this.requirementList = null;
    this.context = null;
    this.phase = Phase.CONDITIONS;
  }

  public void setSearchImmediately() {
    if (this.currentRecipe == null)
      this.searchImmediately = true;
  }

  public void setComponentChanged() {
    this.recipeFinder.setComponentChanged(true);
    this.componentChanged = true;
  }

  public CompoundTag serialize() {
    CompoundTag nbt = new CompoundTag();
    if (this.currentRecipe != null)
      nbt.putString("recipe", this.currentRecipe.id().toString());
    nbt.putString("phase", this.phase.toString());
    nbt.putFloat("recipeProgressTime", this.recipeProgressTime);
    return nbt;
  }

  public void deserialize(CompoundTag nbt) {
    if (nbt.contains("recipe", Tag.TAG_STRING))
      this.futureRecipeID = ResourceLocation.parse(nbt.getString("recipe"));
    if (nbt.contains("phase", Tag.TAG_STRING))
      this.phase = Phase.valueOf(nbt.getString("phase"));
    if (nbt.contains("recipeProgressTime", Tag.TAG_DOUBLE))
      this.recipeProgressTime = nbt.getFloat("recipeProgressTime");
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    container.accept(FloatSyncable.create(() -> this.recipeProgressTime, recipeProgressTime -> this.recipeProgressTime = recipeProgressTime));
    container.accept(IntegerSyncable.create(() -> this.recipeTotalTime, recipeTotalTime -> this.recipeTotalTime = recipeTotalTime));
    container.accept(StringSyncable.create(() -> phase.name(), phase -> this.phase = Phase.valueOf(phase)));
  }

  public float getCurrentActiveRecipeProgress() {
    return getRecipeProgressTime() / getRecipeTotalTime();
  }
}
