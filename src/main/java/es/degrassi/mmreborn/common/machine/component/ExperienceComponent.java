package es.degrassi.mmreborn.common.machine.component;

import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import org.jetbrains.annotations.NotNull;

public class ExperienceComponent extends MachineComponent<IExperienceHandler> {
  private final IExperienceHandler handler;

  public ExperienceComponent(IExperienceHandler handler, IOType ioType) {
    super(ioType);
    this.handler = handler;
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_EXPERIENCE.get();
  }

  @Override
  public @NotNull IExperienceHandler getContainerProvider() {
    return handler;
  }
}
