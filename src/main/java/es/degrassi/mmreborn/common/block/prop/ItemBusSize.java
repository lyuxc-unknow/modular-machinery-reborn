package es.degrassi.mmreborn.common.block.prop;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum ItemBusSize implements StringRepresentable, ConfigLoaded {
  TINY(1),
  SMALL(4),
  NORMAL(6),
  REINFORCED(9),
  BIG(12),
  HUGE(16),
  LUDICROUS(32);

  private int slots;

  public final int defaultConfigSize;

  ItemBusSize(int defaultConfigSize) {
    this.defaultConfigSize = defaultConfigSize;
    this.slots = this.defaultConfigSize; //Temp. TODO configurable and GUI building
  }

  public static ItemBusSize value(String value) {
    return switch (value.toUpperCase(Locale.ROOT)) {
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "REINFORCED" -> REINFORCED;
      case "BIG" -> BIG;
      case "HUGE" -> HUGE;
      case "LUDICROUS" -> LUDICROUS;
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
