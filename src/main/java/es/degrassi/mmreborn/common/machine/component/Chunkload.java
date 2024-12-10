package es.degrassi.mmreborn.common.machine.component;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.util.Chunkloader;

public abstract class Chunkload extends MachineComponent<Chunkloader> {
  public Chunkload() {
    super(IOType.OUTPUT);
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_CHUNKLOAD.get();
  }
}
