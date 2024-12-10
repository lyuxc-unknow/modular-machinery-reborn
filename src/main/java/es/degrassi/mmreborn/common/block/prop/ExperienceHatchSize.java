package es.degrassi.mmreborn.common.block.prop;

import es.degrassi.mmreborn.common.data.MMRConfig;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.StringRepresentable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum ExperienceHatchSize implements StringRepresentable {
  TINY(1_000),
  SMALL(4_000),
  NORMAL(10_000),
  REINFORCED(20_000),
  BIG(45_000),
  HUGE(80_000),
  LUDICROUS(160_000),
  VACUUM(320_000);

  @Getter
  private int capacity;

  public final int defaultCapacity;

  ExperienceHatchSize(int defaultCapacity) {
    this.defaultCapacity = defaultCapacity;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static ExperienceHatchSize value(String value) {
    return switch(value.toUpperCase(Locale.ROOT)) {
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "REINFORCED" -> REINFORCED;
      case "BIG" -> BIG;
      case "HUGE" -> HUGE;
      case "LUDICROUS" -> LUDICROUS;
      case "VACUUM" -> VACUUM;
      default -> TINY;
    };
  }
  public static void loadFromConfig() {
    for (ExperienceHatchSize size : values()) {
      size.capacity = MMRConfig.get().experienceSize(size);
    }
  }
}
