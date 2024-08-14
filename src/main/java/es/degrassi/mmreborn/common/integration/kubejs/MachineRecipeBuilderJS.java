package es.degrassi.mmreborn.common.integration.kubejs;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.util.TickDuration;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import es.degrassi.mmreborn.common.integration.kubejs.requirements.EnergyRequirementJS;
import es.degrassi.mmreborn.common.registration.RecipeRegistration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class MachineRecipeBuilderJS extends KubeRecipe implements RecipeJSBuilder,
  EnergyRequirementJS
{

  public static final Map<ResourceLocation, Map<ResourceLocation, Integer>> IDS = new HashMap<>();

  private final ResourceLocation typeID = RecipeRegistration.RECIPE_TYPE.getId();

  public MachineRecipeBuilderJS(ResourceLocation machine, int time) {
    setValue(ModularMachineryRebornRecipeSchemas.MACHINE_ID, machine);
    setValue(ModularMachineryRebornRecipeSchemas.TIME, new TickDuration(time));
  }

  public MachineRecipeBuilderJS() {}

  @Override
  public void afterLoaded() {
    super.afterLoaded();
    ResourceLocation machine = getValue(ModularMachineryRebornRecipeSchemas.MACHINE_ID);
    if(machine == null)
      throw new KubeRuntimeException("Invalid machine id: " + getValue(ModularMachineryRebornRecipeSchemas.MACHINE_ID));

    if(this.newRecipe) {
      int uniqueID = IDS.computeIfAbsent(this.typeID, id -> new HashMap<>()).computeIfAbsent(machine, m -> 0);
      IDS.get(this.typeID).put(machine, uniqueID + 1);
      this.id = ResourceLocation.fromNamespaceAndPath("kubejs", this.typeID.getPath() + "/" + machine.getNamespace() + "/" + machine.getPath() + "/" + uniqueID);
    }
  }

  @Override
  public @Nullable RecipeHolder<?> createRecipe() {
    if(this.removed)
      return null;

    if(!this.newRecipe)
      return super.createRecipe();

    MachineRecipe.MachineRecipeBuilder builder = new MachineRecipe.MachineRecipeBuilder(getValue(ModularMachineryRebornRecipeSchemas.MACHINE_ID), (int) getValue(ModularMachineryRebornRecipeSchemas.TIME).ticks());

    for (ComponentRequirement<?, ?> requirement : getValue(ModularMachineryRebornRecipeSchemas.REQUIREMENTS))
      builder.addRequirement(requirement);

    builder.withPriority(getValue(ModularMachineryRebornRecipeSchemas.PRIORITY));
    builder.shouldVoidOnFailure(getValue(ModularMachineryRebornRecipeSchemas.VOID));

    ResourceLocation id = getOrCreateId();
    if (getValue(ModularMachineryRebornRecipeSchemas.RECIPE_ID) != null)
      id = getValue(ModularMachineryRebornRecipeSchemas.RECIPE_ID);

    MachineRecipe recipe = builder.build(id);
    return new RecipeHolder<>(id, recipe);
  }

  public MachineRecipeBuilderJS voidOnFailure(boolean v) {
    setValue(ModularMachineryRebornRecipeSchemas.VOID, v);
    return this;
  }

  public MachineRecipeBuilderJS priority(int priority) {
    setValue(ModularMachineryRebornRecipeSchemas.PRIORITY, priority);
    return this;
  }

  @Override
  public String getFromToString() {
    return Objects.requireNonNull(createRecipe()).toString();
  }

  @Override
  public MachineRecipeBuilderJS addRequirement(ComponentRequirement<?, ?> requirement) {
    setValue(ModularMachineryRebornRecipeSchemas.REQUIREMENTS, addToList(ModularMachineryRebornRecipeSchemas.REQUIREMENTS, requirement));
    return this;
  }

  private <E> List<E> addToList(RecipeKey<List<E>> key, E element) {
    List<E> list = new ArrayList<>();
    List<E> values = getValue(key);
    if (values != null) list.addAll(values);
    list.add(element);
    return list;
  }
}
