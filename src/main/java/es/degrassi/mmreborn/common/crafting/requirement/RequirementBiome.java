package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
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
import es.degrassi.mmreborn.common.util.ResultChance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RequirementBiome extends ComponentRequirement<ResourceLocation, RequirementBiome> {
  public static final NamedCodec<RequirementBiome> CODEC = NamedCodec.record(instance -> instance.group(
      DefaultCodecs.RESOURCE_LOCATION.listOf().fieldOf("filter").forGetter(RequirementBiome::filter),
      NamedCodec.BOOL.optionalFieldOf("blacklist", false).forGetter(RequirementBiome::blacklist),
      PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(ComponentRequirement::getPosition)
  ).apply(instance, RequirementBiome::new), "Biome Requirement");

  private final List<ResourceLocation> filter;
  private final boolean blacklist;

  public RequirementBiome(List<ResourceLocation> filter, boolean blacklist, PositionedRequirement position) {
    super(RequirementTypeRegistration.BIOME.get(), IOType.INPUT, position);
    this.filter = filter;
    this.blacklist = blacklist;
  }

  public List<ResourceLocation> filter() {
    return filter;
  }

  public boolean blacklist() {
    return blacklist;
  }

  @Override
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    return component.component().getComponentType().equals(ComponentRegistration.COMPONENT_BIOME.get());
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
    Registry<Biome> biomeRegistry = context.getMachineController().getLevel().registryAccess().registryOrThrow(Registries.BIOME);
    if (this.filter.stream().anyMatch(biome -> biomeRegistry.get(biome) == context.getMachineController().getLevel().getBiome(context.getMachineController().getBlockPos()).value()) != this.blacklist)
      return CraftCheck.success();
    return CraftCheck.failure(Component.translatable(
            "craftcheck.failure.biome." + blacklist,
            filter.stream().map(ResourceLocation::toString).toList().toString(),
            context.getMachineController().getLevel().getBiome(context.getMachineController().getBlockPos()).unwrapKey().get().location().toString()
        ).getString()
    );
  }

  @Override
  public ComponentRequirement<ResourceLocation, RequirementBiome> deepCopy() {
    return new RequirementBiome(Lists.newArrayList(filter), blacklist, getPosition());
  }

  @Override
  public ComponentRequirement<ResourceLocation, RequirementBiome> deepCopyModified(List<RecipeModifier> modifiers) {
    return new RequirementBiome(Lists.newArrayList(filter), blacklist, getPosition());
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {

  }

  @Override
  public void endRequirementCheck() {

  }

  @Override
  public @NotNull String getMissingComponentErrorMessage(IOType ioType) {
    return "component.missing.biome";
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("blacklist", blacklist);
    JsonArray array = new JsonArray();
    filter.stream().map(ResourceLocation::toString).forEach(array::add);
    json.add("filter", array);
    return json;
  }
}
