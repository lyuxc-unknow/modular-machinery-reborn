package es.degrassi.mmreborn.common.machine.component;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import org.jetbrains.annotations.Nullable;

public class ItemComponent extends MachineComponent<IOInventory> {
  private final IOInventory handler;

  public ItemComponent(IOInventory handler, IOType ioType) {
    super(ioType);
    this.handler = handler;
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_ITEM.get();
  }

  @Override
  public IOInventory getContainerProvider() {
    return handler;
  }
}
