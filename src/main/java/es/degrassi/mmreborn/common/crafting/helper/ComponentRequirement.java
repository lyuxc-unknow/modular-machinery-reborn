package es.degrassi.mmreborn.common.crafting.helper;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.codec.NamedMapCodec;
import es.degrassi.mmreborn.api.codec.RegistrarCodec;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementType;
import es.degrassi.mmreborn.common.crafting.requirement.PositionedRequirement;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.util.ResultChance;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class ComponentRequirement<T, V extends ComponentRequirement<T, V>> implements Comparable<ComponentRequirement<?, ?>> {
  public static final NamedMapCodec<ComponentRequirement<?, ?>> CODEC = RegistrarCodec.REQUIREMENT.dispatch(type -> type.requirementType, RequirementType::getCodec, "Requirement");

  public static final int PRIORITY_WEIGHT_ENERGY = 500_000_000;
  public static final int PRIORITY_WEIGHT_FLUID = 100_000_000;
  public static final int PRIORITY_WEIGHT_ITEM = 500_000;

  private final IOType actionType;
  private final RequirementType<V> requirementType;

  private ComponentSelectorTag tag = null;
  @Getter
  private final PositionedRequirement position;

  public ComponentRequirement(RequirementType<V> requirementType, IOType actionType, PositionedRequirement position) {
    this.requirementType = requirementType;
    this.actionType = actionType;
    this.position = position;
  }

  public final NamedCodec<ComponentRequirement<?, ?>> getCodec() {
    return CODEC;
  }

  public final RequirementType<V> getRequirementType() {
    return requirementType;
  }

  public final IOType getActionType() {
    return actionType;
  }

  public final void setTag(ComponentSelectorTag tag) {
    this.tag = tag;
  }

  public final ComponentSelectorTag getTag() {
    return tag;
  }

  public int getSortingWeight() {
    return 0;
  }

  /**
   * Return true here to indicate the passed {@link ProcessingComponent} is valid for the methods:
   * - {@link #startCrafting(ProcessingComponent, RecipeCraftingContext, ResultChance)}
   * - {@link #finishCrafting(ProcessingComponent, RecipeCraftingContext, ResultChance)}
   * - {@link #canStartCrafting(ProcessingComponent, RecipeCraftingContext, List)}
   * <p>
   * and for {@link PerTick} instances:
   * - {@link PerTick#doIOTick(ProcessingComponent, RecipeCraftingContext)}
   *
   * @param component The component to test
   * @param ctx       The context to test in
   * @return true, if the component is valid for further processing by the specified methods, false otherwise
   */
  public abstract boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx);

  //True, if the requirement could be fulfilled by the given component
  public abstract boolean startCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance);

  @Nonnull
  public abstract CraftCheck finishCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance);

  @Nonnull
  public abstract CraftCheck canStartCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, List<ComponentOutputRestrictor<?>> restrictions);

  //Creates an exact copy of the current requirement
  public abstract ComponentRequirement<T, V> deepCopy();

  //Creates a copy of the current requirement and applies all modifiers to the requirement.
  //Supplying an empty list should behave identical to deepCopy
  public abstract ComponentRequirement<T, V> deepCopyModified(List<RecipeModifier> modifiers);

  public abstract void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context);

  public abstract void endRequirementCheck();

  //Previously in ComponentType.getMissingComponentErrorMessage
  //Should return an unlocalized error message to display if no component for the given io-type was found
  //i.e. a recipe has an item output, but there's no item output bus on the machine at all.
  //Overwrite this if necessary at all
  @Nonnull
  public abstract String getMissingComponentErrorMessage(IOType ioType);

  @Override
  public String toString() {
    return asJson().toString();
  }

  //Be sure, that if you specify a new object here as type that you register that along with a helper and renderer
  //in the JEI Integration! Otherwise JEI will complain about not having proper handling for this
  //Also, be sure that this generic T is the *only one* with that type otherwise internally stuff might break...

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("actionType", actionType.name());
    json.add("position", position.asJson());
    return json;
  }

//  public abstract <J extends JeiComponent<T, V>> J jeiComponent();

  @Override
  public int compareTo(@NotNull ComponentRequirement<?, ?> o) {
    return Integer.compare(o.getSortingWeight(), this.getSortingWeight());
  }

  public interface PerTick {
    //Multiplier is passed into this to adjust 'production' or 'consumption' accordingly if the recipe has a longer or shorter duration
    void startIOTick(RecipeCraftingContext context, float durationMultiplier);

    // Returns the actual result of the IOTick-check after a sufficient amount of components have been checked for the requirement
    // Supply a failure message if invalid!
    CraftCheck resetIOTick(RecipeCraftingContext context);

    // Returns either success, partial success or skip component
    // Return value indicates whether the IO tick requirement was already successful
    // or if more components need to be checked.
    CraftCheck doIOTick(ProcessingComponent<?> component, RecipeCraftingContext context);
  }

  public interface ChancedRequirement {
    void setChance(float chance);
  }

}
