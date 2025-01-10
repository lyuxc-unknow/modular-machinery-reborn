package es.degrassi.mmreborn.common.crafting.requirement;

import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.api.crafting.requirement.WeatherType;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.WeatherComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class RequirementWeather implements IRequirement<WeatherComponent> {
  public static final NamedCodec<RequirementWeather> CODEC = NamedCodec.record(instance -> instance.group(
      WeatherType.CODEC.fieldOf("weather").forGetter(RequirementWeather::weather),
      PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
  ).apply(instance, RequirementWeather::new), "Weather Requirement");

  private final WeatherType weather;
  @Getter
  private final PositionedRequirement position;

  public RequirementWeather(WeatherType filter, PositionedRequirement position) {
    this.weather = filter;
    this.position = position;
  }

  public WeatherType weather() {
    return weather;
  }

  @Override
  public RequirementType<RequirementWeather> getType() {
    return RequirementTypeRegistration.WEATHER.get();
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_WEATHER.get();
  }

  @Override
  public IOType getMode() {
    return IOType.INPUT;
  }

  @Override
  public boolean test(WeatherComponent component, ICraftingContext context) {
    Level world = context.getMachineTile().getLevel();
    BlockPos pos = context.getMachineTile().getBlockPos();
    if (world == null) return false;
    return switch (weather) {
      case RAIN -> world.isRaining();
      case SNOW -> world.isRaining()
          && world.canSeeSky(pos.above())
          && world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos.above()).getY() > pos.above().getY()
          && world.getBiome(pos).value().coldEnoughToSnow(pos.above());
      case THUNDER -> world.isThundering();
      case CLEAR -> !world.isRaining();
    };
  }

  @Override
  public void gatherRequirements(IRequirementList<WeatherComponent> list) {
    list.worldCondition(this::check);
  }

  public CraftingResult check(WeatherComponent component, ICraftingContext context) {
    if(test(component, context))
      return CraftingResult.success();
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.weather",
        weather.name().toLowerCase(Locale.ROOT)
    ));
  }

  @Override
  public RequirementWeather deepCopyModified(List<RecipeModifier> modifiers) {
    return this;
  }

  @Override
  public RequirementWeather deepCopy() {
    return this;
  }

  @Override
  public @NotNull Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.weather");
  }

  @Override
  public boolean isComponentValid(WeatherComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }
}
