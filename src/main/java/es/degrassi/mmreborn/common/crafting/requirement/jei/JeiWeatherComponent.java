package es.degrassi.mmreborn.common.crafting.requirement.jei;

import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.WeatherType;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementWeather;
import es.degrassi.mmreborn.common.integration.jei.category.MMRRecipeCategory;
import es.degrassi.mmreborn.common.machine.component.WeatherComponent;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

public class JeiWeatherComponent extends JeiComponent<WeatherType, RecipeRequirement<WeatherComponent, RequirementWeather>> {
  public JeiWeatherComponent(RecipeRequirement<WeatherComponent, RequirementWeather> requirement) {
    super(requirement, 0, 0);
  }

  @Override
  public int getWidth() {
    return 18;
  }

  @Override
  public int getHeight() {
    return 18;
  }

  @Override
  public List<WeatherType> ingredients() {
    return List.of(requirement.requirement().weather());
  }

  @Override
  public void setRecipe(MMRRecipeCategory category, IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, getPosition().x(), getPosition().y())
        .addItemStack(ItemRegistration.WEATHER_SENSOR.toStack())
        .addRichTooltipCallback((view, tooltip) -> {
          tooltip.clear();
          tooltip.add(Component.translatable(
              "modular_machinery_reborn.jei.ingredient.weather",
              requirement.requirement().weather().name().toLowerCase(Locale.ROOT)
          ));
        });
  }
}
