package es.degrassi.mmreborn.api.crafting;

import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.manager.crafting.ProcessorType;
import net.minecraft.nbt.CompoundTag;

public interface IProcessor {

  ProcessorType<? extends IProcessor> getType();

  MachineControllerEntity tile();

  void tick();

  void reset();

  void setMachineInventoryChanged();

  default void setSearchImmediately() {

  }

  CompoundTag serialize();

  void deserialize(CompoundTag nbt);
}
