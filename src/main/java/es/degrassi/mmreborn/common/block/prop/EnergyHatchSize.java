package es.degrassi.mmreborn.common.block.prop;

import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.util.MiscUtils;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;
import java.util.Locale;

public enum EnergyHatchSize implements StringRepresentable {
  TINY      (2048,    1, 128/*, 1, 2*/),
  SMALL     (4096,    2, 512/*, 2, 2*/),
  NORMAL    (8192,    2, 512/*, 2, 2*/),
  REINFORCED(16384,   3, 2048/*, 3, 2*/),
  BIG       (32768,   4, 8192/*, 4, 2*/),
  HUGE      (131072,  5, 32768/*, 5, 2*/),
  LUDICROUS (524288,  6, 131072/*, 6, 2*/),
  ULTIMATE  (2097152, 6, 131072/*, 6, 2*/);

  public static EnergyHatchSize value(String value) {
    return switch (value.toUpperCase(Locale.ROOT)) {
      case "TINY" -> TINY;
      case "SMALL" -> SMALL;
      case "NORMAL" -> NORMAL;
      case "REINFORCED" -> REINFORCED;
      case "BIG" -> BIG;
      case "HUGE" -> HUGE;
      case "LUDICROUS" -> LUDICROUS;
      case "ULTIMATE" -> ULTIMATE;
      default -> null;
    };
  }

  public long maxEnergy;
  public long transferLimit;

  public int ic2EnergyTier;
//  public int gtEnergyTier;
//  public int gtAmperage;

  public final int defaultConfigurationEnergy;
  public final int defaultConfigurationTransferLimit;

  public final int defaultIC2EnergyTier;
//  private final int defaultGTEnergyTier;
//  private final int defaultGTAmperage;

  EnergyHatchSize(int maxEnergy, int ic2EnergyTier, int transferLimit/*, int gtEnergyTier, int gtAmperage*/) {
    this.defaultConfigurationEnergy = maxEnergy;
    this.defaultIC2EnergyTier = ic2EnergyTier;
    this.defaultConfigurationTransferLimit = transferLimit;
//    this.defaultGTEnergyTier = gtEnergyTier;
//    this.defaultGTAmperage = gtAmperage;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase();
  }

  @Nonnull
  public String getUnlocalizedEnergyDescriptor() {
    return "tooltip.ic2.powertier." + ic2EnergyTier + ".name";
  }

  // MM only supports GTCE tiers from ULV to UV
//  public int getGTEnergyTier() {
//    return Mth.clamp(this.gtEnergyTier, 0, 8);
//  }

  //  @Optional.Method(modid = "gregtech")
//  public String getUnlocalizedGTEnergyTier() {
//    return GTValues.VN[getGTEnergyTier()];
//  }

//  public long getGTEnergyTransferVoltage() {
//    if(getGTEnergyTier() < 0) {
//      return -1;
//    }
//    return (int) Math.pow(2, ((getGTEnergyTier() + 1) * 2) + 1);
//  }

  public int getIC2EnergyTransmission() {
    if(ic2EnergyTier < 0) {
      return -1;
    }
    return (int) Math.pow(2, ((ic2EnergyTier + 1) * 2) + 1);
  }

  public static void loadFromConfig() {
    for (EnergyHatchSize size : values()) {
      size.maxEnergy = MMRConfig.get().energySize(size);
      size.maxEnergy = MiscUtils.clamp(size.maxEnergy, 1, Long.MAX_VALUE);
      size.transferLimit = MMRConfig.get().energyLimit(size);
      size.transferLimit = MiscUtils.clamp(size.transferLimit, 1, Long.MAX_VALUE - 1);
      size.ic2EnergyTier = MMRConfig.get().energyTier(size);

//      size.gtEnergyTier = cfg.get("energyhatch.gtvoltage", size.name().toUpperCase(), size.defaultGTEnergyTier, "Defines the GT voltage tier. Affects both input and output hatches of this tier. [range: 0 ~ 8, default: " + size.defaultGTEnergyTier + "]").getInt();
//      size.gtEnergyTier = Mth.clamp(size.gtEnergyTier, 0, 8);
//      size.gtAmperage = cfg.get("energyhatch.gtamperage", size.name().toUpperCase(), size.defaultGTAmperage, "Defines the GT amperage. Affects both output amperage as well as maximum input amperage. [range: 1 ~ 16, default: " + size.defaultGTAmperage + "]").getInt();
//      size.gtAmperage = Mth.clamp(size.gtAmperage, 1, 16);
    }
  }
}
