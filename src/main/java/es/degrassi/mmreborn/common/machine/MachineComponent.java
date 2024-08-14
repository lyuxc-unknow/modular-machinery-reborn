package es.degrassi.mmreborn.common.machine;


import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.util.HybridTank;
import es.degrassi.mmreborn.common.util.IEnergyHandler;
import es.degrassi.mmreborn.common.util.IOInventory;

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

  public static abstract class ItemBus extends MachineComponent<IOInventory> {
    public ItemBus(IOType ioType) {
      super(ioType);
    }

    @Override
    public ComponentType getComponentType() {
      return ComponentRegistration.COMPONENT_ITEM.get();
    }

  }

  public static abstract class FluidHatch extends MachineComponent<HybridTank> {

    public FluidHatch(IOType ioType) {
      super(ioType);
    }

    @Override
    public ComponentType getComponentType() {
      return ComponentRegistration.COMPONENT_FLUID.get();
    }

  }

  public static abstract class EnergyHatch extends MachineComponent<IEnergyHandler> {
    public EnergyHatch(IOType ioType) {
      super(ioType);
    }

    @Override
    public ComponentType getComponentType() {
      return ComponentRegistration.COMPONENT_ENERGY.get();
    }

  }
}
