package es.degrassi.mmreborn.common.machine.component;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.util.IntRange;
import org.jetbrains.annotations.Nullable;

public class TimeComponent extends MachineComponent<IntRange> {

  public TimeComponent() {
    super(IOType.INPUT);
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_TIME.get();
  }

  @Override
  public @Nullable IntRange getContainerProvider() {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<?>> C merge(C c) {
    return (C) this;
  }
}
