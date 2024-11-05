package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentOutputRestrictor;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.CraftCheck;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.crafting.requirement.jei.IJeiRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiEnergyComponent;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.IEnergyHandler;
import es.degrassi.mmreborn.common.util.ResultChance;
import java.util.List;
import javax.annotation.Nonnull;

@SuppressWarnings("unchecked")
public class RequirementEnergy extends ComponentRequirement<Long, RequirementEnergy> implements ComponentRequirement.PerTick {
  public static final NamedMapCodec<RequirementEnergy> CODEC = NamedCodec.record(instance -> instance.group(
    NamedCodec.longRange(0, Long.MAX_VALUE).fieldOf("amount").forGetter(req -> req.requirementPerTick),
    NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(ComponentRequirement::getActionType),
      IJeiRequirement.POSITION_CODEC.fieldOf("position").forGetter(ComponentRequirement::getPosition)
  ).apply(instance, (amount, type, position) -> new RequirementEnergy(type, amount, position)), "EnergyRequirement");

  public final long requirementPerTick;
  private long activeIO;

  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("type", ModularMachineryReborn.rl("energy").toString());
    json.addProperty("amount", requirementPerTick);
    json.addProperty("activeIO", activeIO);
    return json;
  }

  @Override
  public JeiEnergyComponent jeiComponent() {
    return new JeiEnergyComponent(this);
  }

  public RequirementEnergy(IOType ioType, long requirementPerTick, IJeiRequirement.JeiPositionedRequirement position) {
    super(RequirementTypeRegistration.ENERGY.get(), ioType, position);
    this.requirementPerTick = requirementPerTick;
    this.activeIO = this.requirementPerTick;
  }

  @Override
  public int getSortingWeight() {
    return PRIORITY_WEIGHT_ENERGY;
  }

  @Override
  public ComponentRequirement<Long, RequirementEnergy> deepCopy() {
    RequirementEnergy energy = new RequirementEnergy(this.getActionType(), this.requirementPerTick, getPosition());
    energy.activeIO = this.activeIO;
    return energy;
  }

  @Override
  public ComponentRequirement<Long, RequirementEnergy> deepCopyModified(List<RecipeModifier> modifiers) {
    int requirement = Math.round(RecipeModifier.applyModifiers(modifiers, this, this.requirementPerTick, false));
    RequirementEnergy energy = new RequirementEnergy(this.getActionType(), requirement, getPosition());
    energy.activeIO = this.activeIO;
    return energy;
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {
  }

  @Override
  public void endRequirementCheck() {
  }

  @Nonnull
  @Override
  public String getMissingComponentErrorMessage(IOType ioType) {
    return String.format("component.missing.energy.%s", ioType.name().toLowerCase());
  }

  public long getRequiredEnergyPerTick() {
    return requirementPerTick;
  }

  @Override
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    MachineComponent<?> cmp = component.component();
    return cmp.getComponentType().equals(ComponentRegistration.COMPONENT_ENERGY.get()) &&
      cmp instanceof MachineComponent.EnergyHatch &&
      cmp.getIOType() == this.getActionType();
  }

  @Nonnull
  @Override
  public CraftCheck canStartCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, List<ComponentOutputRestrictor> restrictions) {
    IEnergyHandler handler = (IEnergyHandler) component.providedComponent();
    return switch (getActionType()) {
      case INPUT -> {
        if (handler.getCurrentEnergy() >= RecipeModifier.applyModifiers(context, this, this.requirementPerTick, false)) {
          yield CraftCheck.success();
        }
        yield CraftCheck.failure("craftcheck.failure.energy.input");
      }
      case OUTPUT -> {
        if (handler.getCurrentEnergy() + RecipeModifier.applyModifiers(context, this, this.requirementPerTick, false) <= handler.getMaxEnergy())
          yield CraftCheck.success();
        yield CraftCheck.failure("craftcheck.failure.energy.output");
      }
    };
  }

  @Override
  public boolean startCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    return canStartCrafting(component, context, Lists.newArrayList()).isSuccess();
  }

  @Override
  @Nonnull
  public CraftCheck finishCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    return CraftCheck.success();
  }

  @Override
  public void startIOTick(RecipeCraftingContext context, float durationMultiplier) {
    this.activeIO = Math.round(((double) RecipeModifier.applyModifiers(context, this, this.activeIO, false)) * durationMultiplier);
  }

  @Nonnull
  @Override
  public CraftCheck resetIOTick(RecipeCraftingContext context) {
    boolean enough = this.activeIO <= 0;
    this.activeIO = this.requirementPerTick;
    return enough ? CraftCheck.success() : CraftCheck.failure("craftcheck.failure.energy.input");
  }

  @Nonnull
  @Override
  public CraftCheck doIOTick(ProcessingComponent<?> component, RecipeCraftingContext context) {
    IEnergyHandler handler = (IEnergyHandler) component.providedComponent();
    switch (getActionType()) {
      case INPUT:
        if (handler.getCurrentEnergy() >= this.activeIO) {
          handler.setCurrentEnergy(handler.getCurrentEnergy() - this.activeIO);
          this.activeIO = 0;
          return CraftCheck.success();
        } else {
          this.activeIO -= handler.getCurrentEnergy();
          handler.setCurrentEnergy(0);
          return CraftCheck.partialSuccess();
        }
      case OUTPUT:
        long remaining = handler.getRemainingCapacity();
        if (remaining - this.activeIO < 0) {
          handler.setCurrentEnergy(handler.getMaxEnergy());
          this.activeIO -= remaining;
          return CraftCheck.partialSuccess();
        }
        handler.setCurrentEnergy(Math.min(handler.getCurrentEnergy() + this.activeIO, handler.getMaxEnergy()));
        this.activeIO = 0;
        return CraftCheck.success();
    }
    //This is neither input nor output? when do we actually end up in this case down here?
    return CraftCheck.skipComponent();
  }

  @Override
  public String toString() {
    return asJson().toString();
  }
}
