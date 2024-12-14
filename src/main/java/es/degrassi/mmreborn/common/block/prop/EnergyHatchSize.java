package es.degrassi.mmreborn.common.block.prop;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum EnergyHatchSize implements StringRepresentable, ConfigLoaded {
  TINY      (2048,    128),
  SMALL     (4096,    512),
  NORMAL    (8192,    512),
  REINFORCED(16384,   2048),
  BIG       (32768,   8192),
  HUGE      (131072,  32768),
  LUDICROUS (524288,  131072),
  ULTIMATE  (2097152, 131072);

  public static EnergyHatchSize value(String value) {
    return switch (value.toUpperCase(Locale.ROOT)) {
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "REINFORCED" -> REINFORCED;
      case "BIG" -> BIG;
      case "HUGE" -> HUGE;
      case "LUDICROUS" -> LUDICROUS;
      case "ULTIMATE" -> ULTIMATE;
      default -> TINY;
    };
  }

  public long maxEnergy;
  public long transferLimit;

  public final int defaultConfigurationEnergy;
  public final int defaultConfigurationTransferLimit;

  EnergyHatchSize(int maxEnergy, int transferLimit) {
    this.defaultConfigurationEnergy = maxEnergy;
    this.defaultConfigurationTransferLimit = transferLimit;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase();
  }

//  public static void loadFromConfig() {
//    for (EnergyHatchSize size : values()) {
//      size.maxEnergy = MMRConfig.get().energySize(size);
//      size.maxEnergy = MiscUtils.clamp(size.maxEnergy, 1, Long.MAX_VALUE);
//      size.transferLimit = MMRConfig.get().energyLimit(size);
//      size.transferLimit = MiscUtils.clamp(size.transferLimit, 1, Long.MAX_VALUE);
//    }
//  }
}
