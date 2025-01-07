package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.DurationComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class RequirementDuration implements IRequirement<DurationComponent> {
  public static final NamedMapCodec<RequirementDuration> CODEC = NamedCodec.record(instance -> instance.group(
          NamedCodec.intRange(1, Integer.MAX_VALUE).fieldOf("time").forGetter(RequirementDuration::getTime),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
      ).apply(instance, RequirementDuration::new),
      "Duration requirement"
  );
  public final int time;
  private final PositionedRequirement position;

  public RequirementDuration(int time, PositionedRequirement position) {
    this.position = position;
    this.time = time;
  }

  @Override
  public RequirementType<RequirementDuration> getType() {
    return RequirementTypeRegistration.DURATION.get();
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_DURATION.get();
  }

  @Override
  public IOType getMode() {
    return IOType.INPUT;
  }

  @Override
  public boolean test(DurationComponent component, ICraftingContext context) {
    return true;
  }

  @Override
  public void gatherRequirements(IRequirementList<DurationComponent> list) {

  }

  @Override
  public RequirementDuration deepCopyModified(List<RecipeModifier> modifiers) {
    return this;
  }

  @Override
  public RequirementDuration deepCopy() {
    return this;
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.addProperty("time", time);
    return json;
  }

  @Override
  public @NotNull Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.duration");
  }

  @Override
  public boolean isComponentValid(DurationComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }
}
