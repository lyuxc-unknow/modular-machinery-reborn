package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentOutputRestrictor;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.CraftCheck;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.machine.component.ExperienceHatch;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.ResultChance;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class RequirementExperience extends ComponentRequirement<Long, RequirementExperience> {
  public static final NamedMapCodec<RequirementExperience> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.longRange(0, Long.MAX_VALUE).fieldOf("amount").forGetter(req -> req.required),
      NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(ComponentRequirement::getActionType),
      PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(ComponentRequirement::getPosition)
  ).apply(instance, (amount, type, position) -> new RequirementExperience(type, amount, position)), "EnergyRequirement");

  private final long required;

  public RequirementExperience(IOType actionType, long amount, PositionedRequirement position) {
    super(RequirementTypeRegistration.EXPERIENCE.get(), actionType, position);
    this.required = amount;
  }
  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("amount", required);
    return json;
  }

  @Override
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    MachineComponent<?> cmp = component.component();
    if (cmp.getContainerProvider() == null) return false;
    return cmp.getComponentType().equals(ComponentRegistration.COMPONENT_EXPERIENCE.get()) &&
        cmp instanceof ExperienceHatch e &&
        cmp.getIOType() == this.getActionType()
        && validRecipe(e.getContainerProvider());
  }

  private boolean validRecipe(IExperienceHandler handler) {
    return
        (getActionType().isInput() && handler.getExperience() >= required) ||
        (!getActionType().isInput() && handler.getExperience() + required <= handler.getExperienceCapacity());
  }

  @Override
  public boolean startCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    IExperienceHandler handler = (IExperienceHandler) component.providedComponent();
    if (Objects.requireNonNull(getActionType()) == IOType.INPUT) {
      if (handler.getExperience() >= this.required) {
        handler.extractExperienceRecipe(required, false);
        return true;
      }
      return false;
    }
    return true;
  }

  @Override
  public @NotNull CraftCheck finishCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    IExperienceHandler handler = (IExperienceHandler) component.providedComponent();
    if (Objects.requireNonNull(getActionType()) == IOType.OUTPUT) {
      long remaining = handler.getExperienceCapacity() - handler.getExperience();
      if (remaining - this.required < 0) {
        return CraftCheck.failure("craftcheck.failure.experience.output");
      }
      handler.receiveExperienceRecipe(required, false);
      return CraftCheck.success();
    }
    return CraftCheck.skipComponent();
  }

  @Override
  public @NotNull CraftCheck canStartCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, List<ComponentOutputRestrictor<?>> restrictions) {
    IExperienceHandler handler = (IExperienceHandler) component.providedComponent();
    return switch (getActionType()) {
      case INPUT -> {
        if (handler.getExperience() >= RecipeModifier.applyModifiers(context, this, this.required, false)) {
          yield CraftCheck.success();
        }
        yield CraftCheck.failure("craftcheck.failure.experience.input");
      }
      case OUTPUT -> {
        if (handler.getExperience() + RecipeModifier.applyModifiers(context, this, this.required, false) <= handler.getExperienceCapacity())
          yield CraftCheck.success();
        yield CraftCheck.failure("craftcheck.failure.experience.output");
      }
    };
  }

  @Override
  public int getSortingWeight() {
    return 100_000;
  }

  @Override
  public ComponentRequirement<Long, RequirementExperience> deepCopy() {
    return new RequirementExperience(getActionType(), required, new PositionedRequirement(getPosition().x(), getPosition().y()));
  }

  @Override
  public ComponentRequirement<Long, RequirementExperience> deepCopyModified(List<RecipeModifier> modifiers) {
    long requirement = (long) RecipeModifier.applyModifiers(modifiers, this, this.required, false);
    return new RequirementExperience(this.getActionType(), requirement, new PositionedRequirement(getPosition().x(), getPosition().y()));
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {
  }

  @Override
  public void endRequirementCheck() {
  }

  @Override
  public @NotNull String getMissingComponentErrorMessage(IOType ioType) {
    return String.format("component.missing.experience.%s", ioType.name().toLowerCase());
  }
}
