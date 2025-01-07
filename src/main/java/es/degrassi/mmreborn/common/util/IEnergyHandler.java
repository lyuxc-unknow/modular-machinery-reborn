package es.degrassi.mmreborn.common.util;

import net.neoforged.neoforge.energy.IEnergyStorage;

public interface IEnergyHandler extends IEnergyStorage {

  long getCurrentEnergy();

  void setCurrentEnergy(long energy);

  long getMaxEnergy();

  default long getRemainingCapacity() {
    return getMaxEnergy() - getCurrentEnergy();
  }

  void setCanExtract(boolean b);
  void setCanInsert(boolean b);
}
