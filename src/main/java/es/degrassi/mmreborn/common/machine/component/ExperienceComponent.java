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

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<?>> C merge(C c) {
    ExperienceComponent comp = (ExperienceComponent) c;
    return (C) new ExperienceComponent(
        new IExperienceHandler() {
          @Override
          public boolean canAcceptExperience(long l) {
            return true;
          }

          @Override
          public boolean canProvideExperience(long l) {
            return true;
          }

          @Override
          public long getMaxExtract() {
            return Math.max(handler.getMaxExtract(), comp.handler.getMaxExtract());
          }

          @Override
          public long getMaxReceive() {
            return Math.max(handler.getMaxReceive(), comp.handler.getMaxReceive());
          }

          @Override
          public long getExperience() {
            return handler.getExperience() + comp.handler.getExperience();
          }

          @Override
          public long getExperienceCapacity() {
            return handler.getExperienceCapacity() + comp.handler.getExperienceCapacity();
          }

          @Override
          public void setExperience(long l) {

          }

          @Override
          public void setCapacity(long l) {

          }

          @Override
          public long receiveExperience(long l, boolean b) {
            long received1 = handler.receiveExperience(l, b);
            l -= received1;
            long received2 = comp.handler.receiveExperience(l, b);
            return received1 + received2;
          }

          @Override
          public long extractExperience(long l, boolean b) {
            long extracted1 = handler.extractExperience(l, b);
            l -= extracted1;
            long extracted2 = comp.handler.extractExperience(l, b);
            return extracted1 + extracted2;
          }

          @Override
          public long extractExperienceRecipe(long l, boolean b) {
            long extracted1 = handler.extractExperienceRecipe(l, b);
            l -= extracted1;
            long extracted2 = comp.handler.extractExperienceRecipe(l, b);
            return extracted1 + extracted2;
          }

          @Override
          public long receiveExperienceRecipe(long l, boolean b) {
            long received1 = handler.receiveExperienceRecipe(l, b);
            l -= received1;
            long received2 = comp.handler.receiveExperienceRecipe(l, b);
            return received1 + received2;
          }
        },
        getIOType()
    );
  }
}
