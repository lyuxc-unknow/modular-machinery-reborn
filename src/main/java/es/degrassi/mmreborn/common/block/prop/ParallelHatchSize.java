package es.degrassi.mmreborn.common.block.prop;

import es.degrassi.mmreborn.ModularMachineryReborn;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum ParallelHatchSize implements ConfigLoaded, StringRepresentable {
  BASIC(2),
  MEDIUM(25),
  ADVANCED(50),
  ULTIMATE(100),
  MAX(256);

  public static ParallelHatchSize value(String value) {
    return switch (ModularMachineryReborn.rootUC(value)) {
      case "MEDIUM" -> MEDIUM;
      case "ADVANCED" -> ADVANCED;
      case "ULTIMATE" -> ULTIMATE;
      case "MAX" -> MAX;
      default -> BASIC;
    };
  }

  public final int defaultMax;
  public int max;

  ParallelHatchSize(int max) {
    this.defaultMax = max;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }
}
