package es.degrassi.mmreborn.common.machine.component;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.util.HybridTank;
import es.degrassi.mmreborn.common.util.IOInventory;
import org.jetbrains.annotations.Nullable;

public class FluidComponent extends MachineComponent<HybridTank> {
  private final HybridTank handler;

  public FluidComponent(HybridTank handler, IOType ioType) {
    super(ioType);
    this.handler = handler;
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_FLUID.get();
  }

  @Override
  public HybridTank getContainerProvider() {
    return handler;
  }
}
