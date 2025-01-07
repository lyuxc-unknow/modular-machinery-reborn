package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.common.machine.MachineComponent;
import javax.annotation.Nullable;

public interface MachineComponentEntity<T extends MachineComponent<?>> {
  @Nullable
  T provideComponent();
}
