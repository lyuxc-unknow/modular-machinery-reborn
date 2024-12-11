package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentOutputRestrictor;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.CraftCheck;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.IntRange;
import es.degrassi.mmreborn.common.util.ResultChance;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RequirementTime extends ComponentRequirement<IntRange, RequirementTime> {
  public static final NamedCodec<RequirementTime> CODEC = NamedCodec.record(instance -> instance.group(
      IntRange.CODEC.fieldOf("range").forGetter(RequirementTime::time),
      PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(ComponentRequirement::getPosition)
  ).apply(instance, RequirementTime::new), "Time Requirement");

  private final IntRange time;

  public RequirementTime(IntRange time, PositionedRequirement position) {
    super(RequirementTypeRegistration.TIME.get(), IOType.INPUT, position);
    this.time = time;
  }

  public IntRange time() {
    return time;
  }

  @Override
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    return component.component().getComponentType().equals(ComponentRegistration.COMPONENT_TIME.get());
  }

  @Override
  public boolean startCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    return canStartCrafting(component, context, Lists.newArrayList()).isSuccess();
  }

  @Override
  public @NotNull CraftCheck finishCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    return CraftCheck.skipComponent();
  }

  @Override
  public @NotNull CraftCheck canStartCrafting(ProcessingComponent<?> component, RecipeCraftingContext context,
                                              List<ComponentOutputRestrictor<?>> restrictions) {
    long time = context.getMachineController().getLevel().dimensionType().hasFixedTime()
        ? context.getMachineController().getLevel().getDayTime()
        : context.getMachineController().getLevel().getDayTime() % 24000L;
    if (this.time.contains((int) time))
      return CraftCheck.success();
    return CraftCheck.failure(
        Component.translatable(
            "craftcheck.failure.time",
            this.time.toFormattedString(),
            time
        ).getString()
    );
  }

  @Override
  public ComponentRequirement<IntRange, RequirementTime> deepCopy() {
    return new RequirementTime(time, getPosition());
  }

  @Override
  public ComponentRequirement<IntRange, RequirementTime> deepCopyModified(List<RecipeModifier> modifiers) {
    return new RequirementTime(time, getPosition());
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {

  }

  @Override
  public void endRequirementCheck() {

  }

  @Override
  public @NotNull String getMissingComponentErrorMessage(IOType ioType) {
    return "component.missing.time";
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("time", time.toFormattedString());
    return json;
  }
}
