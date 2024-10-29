package es.degrassi.mmreborn.common.crafting.helper;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import es.degrassi.mmreborn.common.crafting.ActiveMachineRecipe;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.modifier.ModifierReplacement;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
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
import java.util.stream.Collectors;

@Getter
@Setter
public class RecipeCraftingContext {

  private static final Random RAND = new Random();

  private final ActiveMachineRecipe activeRecipe;
  private final MachineControllerEntity machineController;

  private int currentCraftingTick = 0;
  private List<ProcessingComponent<?>> typeComponents = new LinkedList<>();
  private Map<RequirementType<?>, List<RecipeModifier>> modifiers = new HashMap<>();

  private List<ComponentOutputRestrictor> currentRestrictions = Lists.newArrayList();

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
    float result = RecipeModifier.applyModifiers(getModifiers(RequirementTypeRegistration.DURATION.get()), RequirementTypeRegistration.DURATION.get(), null, dur, false);
    return dur / result;
  }

  public void addRestriction(ComponentOutputRestrictor restrictor) {
    this.currentRestrictions.add(restrictor);
  }

  public List<ProcessingComponent<?>> getComponentsFor(ComponentRequirement<?, ?> requirement) {
    return this.typeComponents.stream()
      .filter(processingComponent -> requirement.isValidComponent(processingComponent, this))
      .toList();
  }

  public CraftingCheckResult ioTick(int currentTick) {
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
        CraftingCheckResult res = new CraftingCheckResult();
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
    return this.canStartCrafting(req -> {
      List<ProcessingComponent<?>> reqs = getComponentsFor(req);
      if (reqs.isEmpty()) return false;
      return req.isValidComponent(reqs.get(0), this);
    });
  }

  public CraftingCheckResult canStartCrafting(Predicate<ComponentRequirement<?, ?>> requirementFilter) {
    currentRestrictions.clear();
    CraftingCheckResult result = new CraftingCheckResult();
    float successfulRequirements = 0;
    List<ComponentRequirement<?, ?>> requirements = this.getParentRecipe().getCraftingRequirements().stream()
      .filter(requirementFilter)
      .toList();

    lblRequirements:
    for (ComponentRequirement<?, ?> requirement : requirements) {
      requirement.startRequirementCheck(ResultChance.GUARANTEED, this);

      Iterable<ProcessingComponent<?>> components = getComponentsFor(requirement);
      if (!Iterables.isEmpty(components)) {

        List<String> errorMessages = Lists.newArrayList();
        for (ProcessingComponent<?> component : components) {
          CraftCheck check = requirement.canStartCrafting(component, this, this.currentRestrictions);

          if (check.isSuccess()) {
            requirement.endRequirementCheck();
            successfulRequirements += 1;
            continue lblRequirements;
          }

          if (!check.isInvalid() && !check.getUnlocalizedMessage().isEmpty()) {
            errorMessages.add(check.getUnlocalizedMessage());
          }
        }
        errorMessages.forEach(result::addError);
      } else {
        // No component found that would apply for the given requirement
        result.addError(requirement.getMissingComponentErrorMessage(requirement.getActionType()));
      }

      requirement.endRequirementCheck();
    }
    result.setValidity(successfulRequirements / requirements.size());

    currentRestrictions.clear();
    return result;
  }

  public <T> void addComponent(MachineComponent<T> component) {
    this.typeComponents.add(new ProcessingComponent<>(component, component.getContainerProvider()));
  }

  public void addModifier(ModifierReplacement modifier) {
    List<RecipeModifier> modifiers = modifier.getModifiers();
    for (RecipeModifier mod : modifiers) {
      RequirementType<?> target = mod.getTarget();
      if (target == null) {
        target = RequirementTypeRegistration.DURATION.get();
      }
      this.modifiers.computeIfAbsent(target, t -> new LinkedList<>()).add(mod);
    }
  }

  public static class CraftingCheckResult {

    private static final CraftingCheckResult SUCCESS = new CraftingCheckResult();

    private final Map<String, Integer> unlocErrorMessages = new HashMap<>();
    @Getter
    private float validity = 0F;

    private CraftingCheckResult() {}

    private void setValidity(float validity) {
      this.validity = validity;
    }

    private void addError(String unlocError) {
      if (!unlocError.isEmpty()) {
        int count = this.unlocErrorMessages.getOrDefault(unlocError, 0);
        count++;
        this.unlocErrorMessages.put(unlocError, count);
      }
    }

    public List<String> getUnlocalizedErrorMessages() {
      return this.unlocErrorMessages.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
    }

    public boolean isFailure() {
      return !this.unlocErrorMessages.isEmpty();
    }
  }
}
