package es.degrassi.mmreborn.common.manager;

import com.google.common.collect.Iterables;
import es.degrassi.mmreborn.common.crafting.ActiveMachineRecipe;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.CraftingCheckResult;
import es.degrassi.mmreborn.common.crafting.helper.CraftingStatus;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.network.server.SUpdateRecipePacket;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Locale;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CraftingManager implements INBTSerializable<CompoundTag> {
  private final MachineControllerEntity controller;

  private ActiveMachineRecipe activeRecipe = null;
  private CraftingCheckResult highestValidityResult = null;
  private RecipeHolder<MachineRecipe> highestValidity = null;
  private Float validity = null;
  private boolean paused = false;
  private Phase phase;
  private CraftingStatus current;
  private Integer currentTick = null;

  public CraftingManager(MachineControllerEntity entity) {
    controller = entity;
    phase = Phase.WAITING;
    current = entity.getCraftingStatus();
  }

  public void reset() {
    reset(controller.getCraftingStatus());
  }

  public void reset(CraftingStatus craftingStatus) {
    phase = Phase.WAITING;
    currentTick = null;
    activeRecipe = null;
    current = craftingStatus;
  }

  public int getTicks() {
    return currentTick;
  }

  public int getRecipeTicks() {
    if (activeRecipe != null) {
      return activeRecipe.getRecipe().getRecipeTotalTickTime();
    }
    return 0;
  }

  public float getCurrentActiveRecipeProgress() {
    if (this.activeRecipe == null || this.currentTick == null || activeRecipe.getRecipe() == null) return 0F;
    float maxTick = this.activeRecipe.getRecipe().getRecipeTotalTickTime();
    return Mth.clamp(this.currentTick / maxTick, 0F, 1F);
  }

  public boolean hasActiveRecipe() {
    return this.activeRecipe != null && this.activeRecipe.getHolder() != null && this.activeRecipe.getRecipe() != null && this.currentTick != null;
  }

  public void pause() {
    this.paused = true;
  }

  public void resume() {
    this.paused = false;
  }

  public void serverTick() {
    controller.setChanged();
    current = controller.getCraftingStatus();
    Level level = controller.getLevel();
    if (level == null) return;
    if (paused) return;
    if (current.isMissingStructure()) {
      reset();
      return;
    }
    if (activeRecipe == null && phase.isWaiting()) {
      if (level.getGameTime() % MMRConfig.get().checkRecipeTicks.get() == 0)
        this.phase = Phase.SEARCHING;
      return;
    }
    if (!current.isMissingStructure() && !current.isFailure())
      switch (phase) {
        case SEARCHING -> searchRecipe(level);
        case STARTING -> start(createContext());
        case PROCESSING -> recipeTick(createContext());
        case ENDING -> endCraft(createContext());
        case WAITING -> showErrorsIfNeeded();
      }
    controller.setCraftingStatus(current);
    if (activeRecipe != null && activeRecipe.getHolder() != null && currentTick != null)
      changed();
  }

  public void setActiveRecipe(ActiveMachineRecipe recipe) {
    activeRecipe = recipe;
    controller.setChanged();
    changed();
  }

  public void setRecipeTicks(Integer ticks) {
    currentTick = ticks;
    controller.setChanged();
    changed();
  }

  private void changed() {
    if (controller.getLevel() instanceof ServerLevel l)
      PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(controller.getBlockPos()),
          new SUpdateRecipePacket(activeRecipe.getHolder().id(), currentTick, controller.getBlockPos()));
  }

  @Nullable
  private RecipeCraftingContext createContext() {
    return controller.getFoundMachine().createContext(activeRecipe, controller,
        controller.getFoundComponents());
  }

  public void showErrorsIfNeeded() {
    if (this.activeRecipe == null) {
      phase = Phase.WAITING;
      if (highestValidity != null && validity >= .5) {
        current = CraftingStatus.failure(
            Iterables.getFirst(highestValidityResult.getUnlocalizedErrorMessages(), ""));
      }
    }
  }

  public void searchRecipe(Level level) {
    List<RecipeHolder<MachineRecipe>> availableRecipes =
        level
            .getRecipeManager()
            .getAllRecipesFor(RecipeRegistration.RECIPE_TYPE.get())
            .stream()
            .filter(recipe -> recipe.value().getOwningMachineIdentifier() != null)
            .filter(recipe -> recipe.value().getOwningMachineIdentifier().equals(controller.getId()))
            .toList();

    validity = 0F;
    for (RecipeHolder<MachineRecipe> recipe : availableRecipes) {
      ActiveMachineRecipe aRecipe = new ActiveMachineRecipe(recipe, controller);
      RecipeCraftingContext context = controller.getFoundMachine().createContext(aRecipe, controller,
          controller.getFoundComponents());
      if (context == null) continue;
      CraftingCheckResult result = context.canStartCrafting();
      if (!result.isFailure()) {
        this.activeRecipe = aRecipe;
        phase = Phase.STARTING;
        break;
      } else if (highestValidity == null || (result.getValidity() >= 0.5F && result.getValidity() > validity)) {
        highestValidity = recipe;
        highestValidityResult = result;
        validity = result.getValidity();
      }
    }

    if (activeRecipe == null) {
      if (highestValidity != null && validity >= .5) {
        current = CraftingStatus.failure(
            Iterables.getFirst(highestValidityResult.getUnlocalizedErrorMessages(), ""));
      }
    } else {
      current = CraftingStatus.working();
    }
  }

  public void start(@Nullable RecipeCraftingContext context) {
    if (context == null) {
      phase = Phase.WAITING;
      return;
    }
    phase = Phase.PROCESSING;
    highestValidity = null;
    highestValidityResult = null;
    validity = null;
    if (!this.activeRecipe.isInitialized()) this.activeRecipe.init();
    if (activeRecipe == null) {
      reset(CraftingStatus.NO_RECIPE);
      return;
    }
    current = CraftingStatus.working();
    currentTick = 0;
    context.startCrafting();
  }

  public void recipeTick(@Nullable RecipeCraftingContext context) {
    if (activeRecipe == null) return;
    if (context == null) {
      phase = Phase.WAITING;
      return;
    }
    //Skip per-tick logic until controller can finish the recipe
    if (isCompleted(context)) {
      current = CraftingStatus.done();
    } else {
      currentTick++;
      CraftingCheckResult check;
      if (!(check = context.ioTick(currentTick)).isFailure()) {
        current = CraftingStatus.working();
        return;
      } else {
        current = CraftingStatus.failure(
            Iterables.getFirst(check.getUnlocalizedErrorMessages(), ""));
      }
      if (current.isFailure()) {
        if (activeRecipe.getRecipe().doesCancelRecipeOnPerTickFailure()) {
          reset(CraftingStatus.NO_RECIPE);
          return;
        } else {
          currentTick = 0;
          context.startCrafting();
          current = CraftingStatus.working();
        }
        return;
      }
    }

    if (current.isDone() && !context.canStartCrafting(req -> !req.getActionType().isInput()).isFailure()) {
      phase = Phase.ENDING;
      current = CraftingStatus.working();
    }
  }

  private boolean isCompleted(RecipeCraftingContext context) {
    int time = activeRecipe.getRecipe().getRecipeTotalTickTime();
    //Not sure which a user will use... let's try both.
    time = Math.round(RecipeModifier.applyModifiers(context.getModifiers(RequirementTypeRegistration.DURATION.get()), RequirementTypeRegistration.DURATION.get(), null, time, false));
    return currentTick >= time;
  }

  public void endCraft(@Nullable RecipeCraftingContext context) {
    if (context == null) {
      phase = Phase.WAITING;
      return;
    }
    context.finishCrafting();
    currentTick = null;
    current = CraftingStatus.NO_RECIPE;
    context = controller.getFoundMachine().createContext(activeRecipe, controller, controller.getFoundComponents());
    if (context == null) {
      reset(CraftingStatus.NO_RECIPE);
      return;
    }
    CraftingCheckResult result = context.canStartCrafting();
    if (result.isFailure()) {
      reset(CraftingStatus.NO_RECIPE);
      return;
    }
    phase = Phase.STARTING;
  }

  @Override
  public CompoundTag serializeNBT(HolderLookup.Provider provider) {
    CompoundTag nbt = new CompoundTag();
    nbt.putString("phase", phase.getSerializedName());
    if (activeRecipe != null)
      nbt.put("activeRecipe", activeRecipe.serialize());
    if (currentTick != null)
      nbt.putInt("currentTick", currentTick);
    if (current != null)
      nbt.put("current", current.serializeNBT());
    return nbt;
  }

  @Override
  public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
    phase = Phase.value(nbt.getString("phase"));
    currentTick = nbt.contains("currentTick", Tag.TAG_INT) ? nbt.getInt("currentTick") : null;
    current = nbt.contains("current", Tag.TAG_COMPOUND) ? CraftingStatus.deserialize(nbt.getCompound("current")) : null;
    try {
      if (!nbt.contains("recipe", Tag.TAG_COMPOUND)) throw new IllegalArgumentException("");
      activeRecipe = new ActiveMachineRecipe(nbt.getCompound("recipe"), controller);
      currentTick = currentTick != null ? currentTick : 0;
    } catch (Exception e) {
      activeRecipe = null;
      phase = Phase.WAITING;
      currentTick = null;
      current = CraftingStatus.NO_RECIPE;
    }
  }

  @MethodsReturnNonnullByDefault
  public enum Phase implements StringRepresentable {
    WAITING,
    SEARCHING,
    STARTING,
    PROCESSING,
    ENDING;

    @Override
    public String getSerializedName() {
      return name().toLowerCase(Locale.ROOT);
    }

    public boolean isWaiting() {
      return this == WAITING;
    }

    public static Phase value(String v) {
      return switch (v.toLowerCase(Locale.ROOT)) {
        case "searching" -> SEARCHING;
        case "starting" -> STARTING;
        case "processing" -> PROCESSING;
        case "ending" -> ENDING;
        default -> WAITING;
      };
    }
  }
}
