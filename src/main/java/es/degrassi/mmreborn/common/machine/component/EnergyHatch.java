package es.degrassi.mmreborn.common.machine.component;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.util.IEnergyHandler;

public abstract class EnergyHatch extends MachineComponent<IEnergyHandler> {
  public EnergyHatch(IOType ioType) {
    super(ioType);
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_ENERGY.get();
  }

}
