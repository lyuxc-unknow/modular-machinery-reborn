package es.degrassi.mmreborn.common.data;

import es.degrassi.mmreborn.common.util.MMRLogger;

public class Config {
  public static int machineColor;

  static {
    load();
  }

  public static void load() {
    machineColor = MMRConfig.get().general.general_casing_color;
  }

  public static int toInt(String color) {
    try {
      color = color.length() == 6 ? "FF" + color : color;
      if (!color.startsWith("#")) color = "#" + color;
      return Long.decode(color).intValue();
    } catch (Exception exc) {
      MMRLogger.INSTANCE.error("Machine-Casing color defined in the config is not a hex color: {}", color);
      MMRLogger.INSTANCE.error("Using default color instead...");
    }
    return 0xFF4900;
  }
}
