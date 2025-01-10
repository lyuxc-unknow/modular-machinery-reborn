package es.degrassi.mmreborn.common.crafting.requirement;

import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.TimeComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.IntRange;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RequirementTime implements IRequirement<TimeComponent> {
  public static final NamedCodec<RequirementTime> CODEC = NamedCodec.record(instance -> instance.group(
      IntRange.CODEC.fieldOf("range").forGetter(RequirementTime::time),
      PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
  ).apply(instance, RequirementTime::new), "Time Requirement");

  private final IntRange time;
  @Getter
  private final PositionedRequirement position;

  public RequirementTime(IntRange time, PositionedRequirement position) {
    this.time = time;
    this.position = position;
  }

  public IntRange time() {
    return time;
  }

  @Override
  public RequirementType<RequirementTime> getType() {
    return RequirementTypeRegistration.TIME.get();
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_TIME.get();
  }

  @Override
  public IOType getMode() {
    return IOType.INPUT;
  }

  @Override
  public boolean test(TimeComponent component, ICraftingContext context) {
    long time = context.getMachineTile().getLevel().dimensionType().hasFixedTime()
        ? context.getMachineTile().getLevel().getDayTime()
        : context.getMachineTile().getLevel().getDayTime() % 24000L;
    return this.time.contains((int) time);
  }

  @Override
  public void gatherRequirements(IRequirementList<TimeComponent> list) {
    list.worldCondition(this::check);
  }

  private CraftingResult check(TimeComponent component, ICraftingContext context) {
    long time = context.getMachineTile().getLevel().dimensionType().hasFixedTime()
        ? context.getMachineTile().getLevel().getDayTime()
        : context.getMachineTile().getLevel().getDayTime() % 24000L;
    if(this.time.contains((int) time))
      return CraftingResult.success();
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.time",
        this.time.toFormattedString(),
        time
    ));
  }

  @Override
  public RequirementTime deepCopyModified(List<RecipeModifier> modifiers) {
    return this;
  }

  @Override
  public RequirementTime deepCopy() {
    return this;
  }

  @Override
  public @NotNull Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.time");
  }

  @Override
  public boolean isComponentValid(TimeComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }
}
