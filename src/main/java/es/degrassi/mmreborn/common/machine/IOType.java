package es.degrassi.mmreborn.common.machine;

import javax.annotation.Nullable;

public enum IOType {
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
}
