package es.degrassi.mmreborn.common.manager.crafting;

import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.manager.ComponentManager;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequirementList<C extends MachineComponent<?>> implements IRequirementList<C> {

  @Getter
  private final Map<Double, List<RequirementWithFunction>> processRequirements = new HashMap<>();
  @Getter
  private final List<RequirementWithFunction> tickableRequirements = new ArrayList<>();
  @Getter
  private final List<RequirementWithFunction> worldConditions = new ArrayList<>();
  @Getter
  private final List<RequirementWithFunction> inventoryConditions = new ArrayList<>();

  @Setter
  private RecipeRequirement<? extends MachineComponent<?>, ?> currentRequirement;

  @Override
  public void processOnStart(RequirementFunction<C> function) {
    this.processRequirements.computeIfAbsent(0.0D, delay -> new ArrayList<>()).add(new RequirementWithFunction(this.currentRequirement, function));
  }

  @Override
  public void processOnEnd(RequirementFunction<C> function) {
    this.processRequirements.computeIfAbsent(1.0D, delay -> new ArrayList<>()).add(new RequirementWithFunction(this.currentRequirement, function));
  }

  @Override
  public void processEachTick(RequirementFunction<C> function) {
    this.tickableRequirements.add(new RequirementWithFunction(this.currentRequirement, function));
  }

  @Override
  public void worldCondition(RequirementFunction<C> function) {
    this.worldConditions.add(new RequirementWithFunction(this.currentRequirement, function));
  }

  @Override
  public void inventoryCondition(RequirementFunction<C> function) {
    this.inventoryConditions.add(new RequirementWithFunction(this.currentRequirement, function));
  }

  @Override
  public void processDelayed(double baseDelay, RequirementFunction<C> function) {
    this.processRequirements.computeIfAbsent(baseDelay, delay -> new ArrayList<>()).add(new RequirementWithFunction(this.currentRequirement, function));
  }

  @Override
  public void process(IOType mode, RequirementFunction<C> function) {
    this.processDelayed(mode.isInput() ? 0.0D : 1.0D, function);
  }

  public record RequirementWithFunction(RecipeRequirement<?, ?> requirement, RequirementFunction<?> function) {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public CraftingResult process(ComponentManager manager, ICraftingContext context) {
      return ((RequirementFunction)this.function).process(requirement.findComponent(manager, context), context);
    }
  }
}
