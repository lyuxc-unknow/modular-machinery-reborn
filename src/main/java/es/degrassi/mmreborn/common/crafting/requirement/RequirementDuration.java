package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentOutputRestrictor;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.CraftCheck;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.ResultChance;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class RequirementDuration extends ComponentRequirement<Integer, RequirementDuration> implements ComponentRequirement.PerTick {
  public static final NamedMapCodec<RequirementDuration> CODEC = NamedCodec.record(instance -> instance.group(
          NamedCodec.intRange(1, Integer.MAX_VALUE).fieldOf("time").forGetter(RequirementDuration::getTime),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(ComponentRequirement::getPosition)
      ).apply(instance, RequirementDuration::new),
      "Duration requirement"
  );

  public final int time;

  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("time", time);
    return json;
  }

  public RequirementDuration(int time, PositionedRequirement position) {
    super(RequirementTypeRegistration.DURATION.get(), IOType.INPUT, position);
    this.time = time;
  }

  @Override
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    return true;
  }

  @Override
  public boolean startCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    return true;
  }

  @NotNull
  @Override
  public CraftCheck finishCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
    return CraftCheck.skipComponent();
  }

  @NotNull
  @Override
  public CraftCheck canStartCrafting(ProcessingComponent<?> component, RecipeCraftingContext context,
                                     List<ComponentOutputRestrictor<?>> restrictions) {
    return CraftCheck.skipComponent();
  }

  @Override
  public ComponentRequirement<Integer, RequirementDuration> deepCopy() {
    return new RequirementDuration(time, getPosition());
  }

  @Override
  public ComponentRequirement<Integer, RequirementDuration> deepCopyModified(List<RecipeModifier> modifiers) {
    return new RequirementDuration(time, getPosition());
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {

  }

  @Override
  public void endRequirementCheck() {

  }

  @NotNull
  @Override
  public String getMissingComponentErrorMessage(IOType ioType) {
    return "";
  }

  @Override
  public void startIOTick(RecipeCraftingContext context, float durationMultiplier) {

  }

  @NotNull
  @Override
  public CraftCheck resetIOTick(RecipeCraftingContext context) {
    return CraftCheck.skipComponent();
  }

  @NotNull
  @Override
  public CraftCheck doIOTick(ProcessingComponent<?> component, RecipeCraftingContext context) {
    return CraftCheck.skipComponent();
  }
}
