package es.degrassi.mmreborn.common.util;

import net.neoforged.fml.ModList;

public interface Mods {
  static boolean isAULoaded() {
    return ModList.get().isLoaded("almostunified");
  }
}
