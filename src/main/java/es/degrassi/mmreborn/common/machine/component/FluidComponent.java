package es.degrassi.mmreborn.common.machine.component;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.util.HybridTank;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

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

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<?>> C merge(C c) {
    FluidComponent comp = (FluidComponent) c;
    return (C) new FluidComponent(
        new HybridTank(handler.getCapacity() + comp.handler.getCapacity()) {
          @Override
          public int fill(FluidStack resource, FluidAction action) {
            int filled1 = handler.fill(resource, action);
            resource = resource.copyWithAmount(resource.getAmount() - filled1);
            int filled2 = comp.handler.fill(resource, action);
            return filled1 + filled2;
          }

          @Override
          public FluidStack drain(FluidStack resource, FluidAction action) {
            FluidStack drained1 = handler.drain(resource, action);
            resource = resource.copyWithAmount(resource.getAmount() - drained1.getAmount());
            FluidStack drained2 = comp.handler.drain(resource, action);
            return drained2.isEmpty() ? drained1 : resource.copyWithAmount(drained1.getAmount() + drained2.getAmount());
          }

          @Override
          public FluidStack drain(int maxDrain, FluidAction action) {
            FluidStack drained1 = handler.drain(maxDrain, action);
            maxDrain -= drained1.getAmount();
            FluidStack drained2 = handler.drain(maxDrain, action);
            return drained1.copyWithAmount(drained1.getAmount() + drained2.getAmount());
          }
        },
        getIOType()
    );
  }

  @Override
  public int compareTo(@NotNull MachineComponent<HybridTank> o) {
    HybridTank one = getContainerProvider();
    HybridTank two = o.getContainerProvider();
    if (one.isEmpty() && two.isEmpty()) return 0;
    if (one.isEmpty() && !two.isEmpty()) return -1;
    if (!one.isEmpty() && !two.isEmpty()) return 0;
    return 1;
  }
}
