package es.degrassi.mmreborn.common.util;

public interface IEnergyHandler {

  long getCurrentEnergy();

  void setCurrentEnergy(long energy);

  long getMaxEnergy();

  default long getRemainingCapacity() {
    return getMaxEnergy() - getCurrentEnergy();
  }
}
