package es.degrassi.mmreborn.common.machine;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.util.Locale;

public enum IOType implements StringRepresentable {
  INPUT,
  OUTPUT;

  @Nullable
  public static IOType getByString(String name) {
    for (IOType val : values()) {
      if (val.name().equalsIgnoreCase(name)) {
        return val;
      }
    }
    return null;
  }

  public boolean isInput() {
    return this == INPUT;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }
}
