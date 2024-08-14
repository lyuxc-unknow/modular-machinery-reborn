package es.degrassi.mmreborn.client.util;

import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.data.config.MMREnergyHatchConfig;
import lombok.Getter;
import net.minecraft.util.Mth;

public class EnergyDisplayUtil {

  public static boolean displayFETooltip = true;
  public static boolean displayIC2EUTooltip = true;
//  public static boolean displayGTEUTooltip = true;

  //Available: FE, IC2_EU
  public static EnergyType type = EnergyType.FE;

  public static void loadFromConfig() {
    MMREnergyHatchConfig config = MMRConfig.get().energyHatch;
    displayFETooltip = config.displayFETooltip;
    displayIC2EUTooltip = config.displayIC2EUTooltip;
//    displayGTEUTooltip = config.displayGTEUTooltip;
    type = config.type;
  }

  public enum EnergyType {
    FE(1),
    IC2_EU(0.25F);
//    GT_EU(0.25F);

    private final double multiplier;
    @Getter
    private final String unlocalizedFormat;

    EnergyType(double multiplier) {
      this.multiplier = multiplier;
      this.unlocalizedFormat = "tooltip.energy.type." + name().toLowerCase();
    }

    public long formatEnergyForDisplay(long energy) {
      return Mth.lfloor(energy * multiplier);
    }

    public static EnergyType getType(String configValue) {
      EnergyType type = FE;
      try {
        type = EnergyType.valueOf(configValue);
      } catch (Exception ignored) {}

      return type;
    }

  }

}
