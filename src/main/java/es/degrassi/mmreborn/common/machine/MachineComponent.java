package es.degrassi.mmreborn.common.machine;

import es.degrassi.mmreborn.common.crafting.ComponentType;

public abstract class MachineComponent<T> {
  private final IOType ioType;

  public MachineComponent(IOType ioType) {
    this.ioType = ioType;
  }

  public final IOType getIOType() {
    return ioType;
  }

  public abstract ComponentType getComponentType();

  public abstract T getContainerProvider();
}
