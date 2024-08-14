package es.degrassi.mmreborn.common.util;

import net.neoforged.fml.ModList;

public enum Mods {
  JEI("jei"),
  MEKANISM("mekanism"),
  IC2("ic2");

  public final String modid;
  private final boolean loaded;

  Mods(String modName) {
    this.modid = modName;
    this.loaded = ModList.get().isLoaded(this.modid);
  }

  public boolean isPresent() {
    return loaded;
  }

}
