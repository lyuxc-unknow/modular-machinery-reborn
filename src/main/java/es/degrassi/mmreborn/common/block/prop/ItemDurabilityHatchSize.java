package es.degrassi.mmreborn.common.block.prop;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum ItemDurabilityHatchSize implements StringRepresentable, ConfigLoaded {
  TINY(1, 9),
  SMALL(4, 9),
  NORMAL(6, 9),
  BIG(9, 9);

  public int slots;
  public int cols;

  public final int defaultSlots;
  public final int defaultCols;

  ItemDurabilityHatchSize(int defaultSlots, int defaultCols) {
    this.defaultSlots = defaultSlots;
    this.defaultCols = defaultCols;
  }

  public static ItemDurabilityHatchSize value(String value) {
    return switch (value.toUpperCase(Locale.ROOT)) {
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "BIG" -> BIG;
      default -> TINY;
    };
  }

  public int getSlotCount() {
    return slots;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase();
  }
}
