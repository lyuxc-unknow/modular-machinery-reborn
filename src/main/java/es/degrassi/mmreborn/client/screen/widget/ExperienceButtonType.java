package es.degrassi.mmreborn.client.screen.widget;

import es.degrassi.mmreborn.ModularMachineryReborn;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Locale;

public enum ExperienceButtonType implements StringRepresentable {
  EXTRACT_1,
  EXTRACT_10,
  EXTRACT_ALL,
  INSERT_1,
  INSERT_10,
  INSERT_ALL;

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }

  public boolean isAll() {
    return this == EXTRACT_ALL || this == INSERT_ALL;
  }

  public boolean extract() {
    return switch (this) {
      case EXTRACT_1, EXTRACT_10, EXTRACT_ALL -> true;
      default -> false;
    };
  }

  public static List<ExperienceButtonType> extractions() {
    return List.of(EXTRACT_1, EXTRACT_10, EXTRACT_ALL);
  }

  public static List<ExperienceButtonType> insertions() {
    return List.of(INSERT_1, INSERT_10, INSERT_ALL);
  }

  public ResourceLocation base() {
    return ModularMachineryReborn.rl("textures/gui/widget/experience_button_" + getSerializedName() + ".png");
  }

  public ResourceLocation disabled() {
    return ModularMachineryReborn.rl("textures/gui/widget/experience_button_disabled_" + getSerializedName() + ".png");
  }

  public ResourceLocation hovered() {
    return ModularMachineryReborn.rl("textures/gui/widget/experience_button_hovered_" + getSerializedName() + ".png");
  }

  public int getAmount(boolean extraction) {
    return switch (this) {
      case INSERT_1, EXTRACT_1 -> extraction ? 1 : -1;
      case INSERT_10, EXTRACT_10 -> extraction ? 10 : -10;
      default -> 0;
    };
  }

  public Component component() {
    return Component.translatable("mmr.gui.tooltip.experience.button." + getSerializedName());
  }
}
