package es.degrassi.mmreborn.common.block.prop;

import es.degrassi.mmreborn.common.data.MMRConfig;
import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public enum ItemBusSize implements StringRepresentable {
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
      case "TINY" -> TINY;
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "REINFORCED" -> REINFORCED;
      case "BIG" -> BIG;
      case "HUGE" -> HUGE;
      case "LUDICROUS" -> LUDICROUS;
      default -> null;
    };
  }

  public static void loadFromConfig() {
    for (ItemBusSize size : ItemBusSize.values()) {
      size.slots = MMRConfig.get().itemSize(size);
    }
  }

  public int getSlotCount() {
    return slots;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase();
  }
}
