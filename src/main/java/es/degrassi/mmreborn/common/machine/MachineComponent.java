package es.degrassi.mmreborn.common.machine;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import org.jetbrains.annotations.Nullable;

public abstract class MachineComponent<T> {
  private final IOType ioType;

  public MachineComponent(IOType ioType) {
    this.ioType = ioType;
  }

  public final IOType getIOType() {
    return ioType;
  }

  public abstract ComponentType getComponentType();

  @Nullable
  public abstract T getContainerProvider();
}
