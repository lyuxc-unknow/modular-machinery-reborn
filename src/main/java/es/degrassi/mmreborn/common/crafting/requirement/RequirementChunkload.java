package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.common.crafting.helper.ComponentOutputRestrictor;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.crafting.helper.CraftCheck;
import es.degrassi.mmreborn.common.crafting.helper.ProcessingComponent;
import es.degrassi.mmreborn.common.crafting.helper.RecipeCraftingContext;
import es.degrassi.mmreborn.common.crafting.requirement.jei.IJeiRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiChunkloadRequirement;
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiTimeRequirement;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.Chunkloader;
import es.degrassi.mmreborn.common.util.IntRange;
import es.degrassi.mmreborn.common.util.ResultChance;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RequirementChunkload extends ComponentRequirement<Integer, RequirementChunkload> implements ComponentRequirement.PerTick{
  public static final NamedCodec<RequirementChunkload> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.intRange(1, 32).optionalFieldOf("radius", 1).forGetter(RequirementChunkload::radius),
      IJeiRequirement.POSITION_CODEC.fieldOf("position").forGetter(ComponentRequirement::getPosition)
  ).apply(instance, RequirementChunkload::new), "Chunkload Requirement");

  private final Integer radius;

  public RequirementChunkload(Integer radius, IJeiRequirement.JeiPositionedRequirement position) {
    super(RequirementTypeRegistration.CHUNKLOAD.get(), IOType.OUTPUT, position);
    this.radius = radius;
  }

  public Integer radius() {
    return radius;
  }

  @Override
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    return component.component().getComponentType().equals(ComponentRegistration.COMPONENT_CHUNKLOAD.get()) &&
        component.component() instanceof MachineComponent.Chunkload;
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
  public @NotNull CraftCheck canStartCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, List<ComponentOutputRestrictor> restrictions) {
    return switch (getActionType()) {
      case INPUT -> CraftCheck.skipComponent();
      case OUTPUT -> CraftCheck.success();
    };
  }

  @Override
  public ComponentRequirement<Integer, RequirementChunkload> deepCopy() {
    return new RequirementChunkload(radius, getPosition());
  }

  @Override
  public ComponentRequirement<Integer, RequirementChunkload> deepCopyModified(List<RecipeModifier> modifiers) {
    return new RequirementChunkload(radius, getPosition());
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {

  }

  @Override
  public void endRequirementCheck() {

  }

  @Override
  public @NotNull String getMissingComponentErrorMessage(IOType ioType) {
    return "component.missing.chunkload";
  }

  @Override
  public JeiChunkloadRequirement jeiComponent() {
    return new JeiChunkloadRequirement(this);
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("type", ModularMachineryReborn.rl("chunkload").toString());
    json.addProperty("radius", radius);
    return json;
  }

  @Override
  public void startIOTick(RecipeCraftingContext context, float durationMultiplier) {

  }

  @Override
  public CraftCheck resetIOTick(RecipeCraftingContext context) {
    return null;
  }

  @Override
  public CraftCheck doIOTick(ProcessingComponent<?> component, RecipeCraftingContext context) {
    Chunkloader chunkloader = (Chunkloader) component.providedComponent();
    chunkloader.setActiveWithTempo((ServerLevel) context.getMachineController().getLevel(), this.radius, 2);
    return CraftCheck.success();
  }
}
