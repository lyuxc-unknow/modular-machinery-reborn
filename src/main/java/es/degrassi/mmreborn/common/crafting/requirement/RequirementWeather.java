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
import es.degrassi.mmreborn.common.crafting.requirement.jei.JeiWeatherRequirement;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.ResultChance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class RequirementWeather extends ComponentRequirement<RequirementWeather.WeatherType, RequirementWeather> {
  public static final NamedCodec<RequirementWeather> CODEC = NamedCodec.record(instance -> instance.group(
      WeatherType.CODEC.fieldOf("weather").forGetter(RequirementWeather::weather),
      IJeiRequirement.POSITION_CODEC.fieldOf("position").forGetter(ComponentRequirement::getPosition)
  ).apply(instance, RequirementWeather::new), "Weather Requirement");

  private final WeatherType weather;

  public RequirementWeather(WeatherType filter, IJeiRequirement.JeiPositionedRequirement position) {
    super(RequirementTypeRegistration.WEATHER.get(), IOType.INPUT, position);
    this.weather = filter;
  }

  public WeatherType weather() {
    return weather;
  }

  @Override
  public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
    return component.component().getComponentType().equals(ComponentRegistration.COMPONENT_WEATHER.get());
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
    Level world = context.getMachineController().getLevel();
    BlockPos pos = context.getMachineController().getBlockPos();
    if (switch(weather) {
      case RAIN -> world.isRaining();
      case SNOW -> world.isRaining() && world.canSeeSky(pos.above()) && world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos.above()).getY() > pos.above().getY() && world.getBiome(pos).value().coldEnoughToSnow(pos.above());
      case THUNDER -> world.isThundering();
      case CLEAR -> !world.isRaining();
    }) return CraftCheck.success();
    return CraftCheck.failure(
        Component.translatable(
        "craftcheck.failure.weather",
            weather.name().toLowerCase(Locale.ROOT)
        ).getString()
    );
  }

  @Override
  public ComponentRequirement<WeatherType, RequirementWeather> deepCopy() {
    return new RequirementWeather(weather, getPosition());
  }

  @Override
  public ComponentRequirement<WeatherType, RequirementWeather> deepCopyModified(List<RecipeModifier> modifiers) {
    return new RequirementWeather(weather, getPosition());
  }

  @Override
  public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {

  }

  @Override
  public void endRequirementCheck() {

  }

  @Override
  public @NotNull String getMissingComponentErrorMessage(IOType ioType) {
    return "component.missing.weather";
  }

  @Override
  public JeiWeatherRequirement jeiComponent() {
    return new JeiWeatherRequirement(this);
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = super.asJson();
    json.addProperty("type", ModularMachineryReborn.rl("weather").toString());
    json.addProperty("weather", weather.name().toLowerCase(Locale.ROOT));
    return json;
  }

  public enum WeatherType {
    CLEAR,
    RAIN,
    SNOW,
    THUNDER;

    public static final NamedCodec<WeatherType> CODEC = NamedCodec.enumCodec(WeatherType.class);

    public static WeatherType value(String value) {
      return valueOf(value.toUpperCase(Locale.ROOT));
    }
  }
}
