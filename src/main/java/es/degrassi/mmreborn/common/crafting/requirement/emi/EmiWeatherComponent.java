package es.degrassi.mmreborn.common.crafting.requirement.emi;

import dev.emi.emi.api.stack.EmiStack;
import es.degrassi.mmreborn.client.requirement.ItemRendering;
import es.degrassi.mmreborn.common.crafting.requirement.RequirementWeather;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class EmiWeatherComponent extends EmiComponent<RequirementWeather.WeatherType, RequirementWeather> implements ItemRendering {
  public EmiWeatherComponent(RequirementWeather requirement) {
    super(requirement, 0, 0, false);
  }

  @Override
  @Nullable
  public ResourceLocation texture() {
    return null;
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
  public List<RequirementWeather.WeatherType> ingredients() {
    return List.of(requirement.weather());
  }

  public EmiStack getStack() {
    return EmiStack.of(ItemRegistration.WEATHER_SENSOR.toStack());
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    super.render(guiGraphics, mouseX, mouseY);
    drawStack(guiGraphics, 0, 0, -1);
  }

  @Override
  public List<Component> getTooltip() {
    return List.of(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.weather",
        requirement.weather().name().toLowerCase(Locale.ROOT)
    ));
  }
}
