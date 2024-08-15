package es.degrassi.mmreborn.api.utils;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.Structure;
import es.degrassi.mmreborn.common.data.Config;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.util.MMRLogger;
import net.minecraft.core.Direction;

public class StructureCheck {
  private final MachineControllerEntity entity;

  private Direction facing;
  private Structure toCheck;
  private DynamicMachine machine;
  private MachineControllerEntity.CraftingStatus status;
  private boolean recipe;

  public StructureCheck(MachineControllerEntity entity) {
    this.entity = entity;
    this.facing = null;
    this.machine = null;
    this.toCheck = null;
    this.status = MachineControllerEntity.CraftingStatus.MISSING_STRUCTURE;
    this.recipe = false;
  }

  public void checkIn(int ticks, Direction facing) {
    if (facing == null) return;
    if (this.facing != facing)
      this.facing = facing;
    if (entity.ticksExisted % ticks == 0) {
      checkNotNull();
      checkNull();
      entity.set(machine, status, recipe);
      colorize(machine);
    }
  }

  private void checkNotNull() {
    if (machine != null && toCheck != null) {
      if (machine.requiresBlueprint() && !machine.equals(entity.getBlueprintMachine())) {
        status = MachineControllerEntity.CraftingStatus.MISSING_STRUCTURE;
        machine = null;
        toCheck = null;
        recipe = true;
      } else {
        if (!toCheck.match(entity.getLevel(), entity.getBlockPos(), facing)) {
          status = MachineControllerEntity.CraftingStatus.MISSING_STRUCTURE;
          machine = null;
          toCheck = null;
          recipe = true;
        }
      }
    }
  }

  private void checkNull() {
    if (toCheck == null || machine == null) {
      machine = null;
      toCheck = null;
      status = MachineControllerEntity.CraftingStatus.MISSING_STRUCTURE;
      recipe = false;
      DynamicMachine machine = entity.getBlueprintMachine();
      if (machine != null) checkBlueprintNotNull(machine);
      else checkBlueprintNull();
    }
  }

  private void checkBlueprintNotNull(DynamicMachine machine) {
    this.machine = machine;
    this.toCheck = machine.getPattern();
    if (machine.getPattern().match(entity.getLevel(), entity.getBlockPos(), facing)) {
      MMRLogger.INSTANCE.info("Structure matching: {} in machine {}", toCheck.asJson(), machine.getRegistryName());
      recipe = false;
      status = MachineControllerEntity.CraftingStatus.NO_RECIPE;
    } else {
      this.machine = null;
      toCheck = null;
      recipe = true;
      this.status = MachineControllerEntity.CraftingStatus.MISSING_STRUCTURE;
    }
  }

  private void checkBlueprintNull() {
    for (DynamicMachine machine : ModularMachineryReborn.MACHINES.values()) {
      if (machine.requiresBlueprint()) continue;
      this.machine = machine;
      this.toCheck = machine.getPattern();
      if (machine.getPattern().match(entity.getLevel(), entity.getBlockPos(), facing)) {
        recipe = false;
        status = MachineControllerEntity.CraftingStatus.NO_RECIPE;
        return;
      }
    }
    machine = null;
    toCheck = null;
    recipe = true;
    status = MachineControllerEntity.CraftingStatus.MISSING_STRUCTURE;
  }

  private void colorize(DynamicMachine machine) {
    if (machine.getMachineColor() != Config.machineColor) {
      entity.distributeCasingColor(false);
    }
  }
}
