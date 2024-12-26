package es.degrassi.mmreborn.common.crafting.helper;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import es.degrassi.mmreborn.common.crafting.ActiveMachineRecipe;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.ResultChance;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

@Getter
@Setter
public class RecipeCraftingContext {

  private static final Random RAND = new Random();

  private final ActiveMachineRecipe activeRecipe;
  private final MachineControllerEntity machineController;

  private List<ProcessingComponent<?>> typeComponents = new LinkedList<>();
  private Map<RequirementType<?>, List<RecipeModifier>> modifiers = new HashMap<>();

  private List<ComponentOutputRestrictor<?>> currentRestrictions = Lists.newArrayList();

  public RecipeCraftingContext(ActiveMachineRecipe activeRecipe, MachineControllerEntity controller) {
    this.activeRecipe = activeRecipe;
    this.machineController = controller;
  }

  public MachineRecipe getParentRecipe() {
    return activeRecipe.getRecipe();
  }

  @Nonnull
  public List<RecipeModifier> getModifiers(RequirementType<?> target) {
    return modifiers.computeIfAbsent(target, t -> new LinkedList<>());
  }

  public float getDurationMultiplier() {
    float dur = this.getParentRecipe().getRecipeTotalTickTime();
    float result = RecipeModifier.applyModifiers(getModifiers(RequirementTypeRegistration.DURATION.get()),
        RequirementTypeRegistration.DURATION.get(), IOType.INPUT, dur, false);
    return dur / result;
  }

  public void addRestriction(ComponentOutputRestrictor<?> restrictor) {
    this.currentRestrictions.add(restrictor);
  }

  public List<ProcessingComponent<?>> getComponentsFor(ComponentRequirement<?, ?> requirement) {
    return this.typeComponents.stream()
        .filter(processingComponent -> requirement.isValidComponent(processingComponent, this))
        .toList();
  }

  public CraftingCheckResult ioTick() {
    float durMultiplier = this.getDurationMultiplier();

    for (ComponentRequirement<?, ?> requirement : this.getParentRecipe().getCraftingRequirements()) {
      if (!(requirement instanceof ComponentRequirement.PerTick perTickRequirement) ||
          requirement.getActionType() == IOType.OUTPUT) continue;

      perTickRequirement.resetIOTick(this);
      perTickRequirement.startIOTick(this, durMultiplier);

      for (ProcessingComponent<?> component : getComponentsFor(requirement)) {
        CraftCheck result = perTickRequirement.doIOTick(component, this);
        if (result.isSuccess()) {
          break;
        }
      }

      CraftCheck result = perTickRequirement.resetIOTick(this);
      if (!result.isSuccess()) {
        CraftingCheckResult res = CraftingCheckResult.empty();
        res.addError(result.getUnlocalizedMessage());
        return res;
      }
    }

    for (ComponentRequirement<?, ?> requirement : this.getParentRecipe().getCraftingRequirements()) {
      if (!(requirement instanceof ComponentRequirement.PerTick perTickRequirement) ||
          requirement.getActionType() == IOType.INPUT) continue;

      perTickRequirement.resetIOTick(this);
      perTickRequirement.startIOTick(this, durMultiplier);

      for (ProcessingComponent<?> component : getComponentsFor(requirement)) {
        CraftCheck result = perTickRequirement.doIOTick(component, this);
        if (result.isSuccess()) {
          break;
        }
      }
      perTickRequirement.resetIOTick(this);
    }

    return CraftingCheckResult.SUCCESS;
  }

  public void startCrafting() {
    startCrafting(RAND.nextLong());
  }

  public void startCrafting(long seed) {
    ResultChance chance = new ResultChance(seed);
    for (ComponentRequirement<?, ?> requirement : this.getParentRecipe().getCraftingRequirements()) {
      requirement.startRequirementCheck(chance, this);

      for (ProcessingComponent<?> component : getComponentsFor(requirement)) {
        if (requirement.startCrafting(component, this, chance)) {
          requirement.endRequirementCheck();
          break;
        }
      }
      requirement.endRequirementCheck();
    }
  }

  public void finishCrafting() {
    finishCrafting(RAND.nextLong());
  }

  public void finishCrafting(long seed) {
    ResultChance chance = new ResultChance(seed);
    for (ComponentRequirement<?, ?> requirement : this.getParentRecipe().getCraftingRequirements()) {
      requirement.startRequirementCheck(chance, this);

      for (ProcessingComponent<?> component : getComponentsFor(requirement)) {
        CraftCheck check = requirement.finishCrafting(component, this, chance);
        if (check.isSuccess()) {
          requirement.endRequirementCheck();
          break;
        }
      }
      requirement.endRequirementCheck();
    }
  }

  public CraftingCheckResult canStartCrafting() {
    return this.canStartCrafting(req -> true);
  }

  public CraftingCheckResult canStartCrafting(Predicate<ComponentRequirement<?, ?>> requirementFilter) {
    currentRestrictions.clear();
    CraftingCheckResult result = CraftingCheckResult.empty();
    int successfulRequirements = 0;
    List<ComponentRequirement<?, ?>> requirements = getParentRecipe()
        .getCraftingRequirements()
        .stream()
        .filter(requirementFilter)
        .toList();

    lblRequirements:
    for (ComponentRequirement<?, ?> requirement : requirements) {
      requirement.startRequirementCheck(ResultChance.GUARANTEED, this);

      Iterable<ProcessingComponent<?>> components = getComponentsFor(requirement);
      if (!Iterables.isEmpty(components)) {
        for (ProcessingComponent<?> component : components) {
          CraftCheck check = requirement.canStartCrafting(component, this, this.currentRestrictions);

          if (check.isSuccess()) {
            requirement.endRequirementCheck();
            successfulRequirements++;
            continue lblRequirements;
          }

          if (!check.isInvalid() && !check.getUnlocalizedMessage().isEmpty()) {
            result.addError(check.getUnlocalizedMessage());
          }
        }
      } else {
        // No component found that would apply for the given requirement
        result.addError(requirement.getMissingComponentErrorMessage(requirement.getActionType()));
      }

      requirement.endRequirementCheck();
    }
    result.setValidity(successfulRequirements / (requirements.size() * 1F));

    currentRestrictions.clear();
    return result;
  }

  public <T> void addComponent(MachineComponent<T> component) {
    this.typeComponents.add(new ProcessingComponent<>(component, component.getContainerProvider()));
  }

  public void addModifier(ModifierReplacement modifier) {
    List<RecipeModifier> modifiers = modifier.getModifiers();
    for (RecipeModifier mod : modifiers) {
      this.modifiers.computeIfAbsent(mod.getTarget(), t -> new LinkedList<>()).add(mod);
    }
  }
}
