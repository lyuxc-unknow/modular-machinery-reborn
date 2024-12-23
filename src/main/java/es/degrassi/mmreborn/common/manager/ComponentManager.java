package es.degrassi.mmreborn.common.manager;

import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ComponentManager {
  private final MachineControllerEntity controller;

  private final Map<BlockPos, MachineComponent<?>> foundComponents = new LinkedHashMap<>();

  public ComponentManager(MachineControllerEntity entity) {
    this.controller = entity;
  }

  public final void reset() {
    foundComponents.clear();
    controller.setChanged();
  }

  public final void updateComponents() {
    if (controller.getFoundMachine() == DynamicMachine.DUMMY) return;
    if (controller.getLevel().getGameTime() % 20 == 0) {
      foundComponents.clear();
      foundComponents.putAll(gatherComponents());
    }
    controller.setChanged();
  }

  public final List<MachineComponent<?>> getFoundComponentsList() {
    return foundComponents.values().stream().toList();
  }

  public final Map<BlockPos, MachineComponent<?>> getFoundComponentsMap() {
    return foundComponents;
  }

  private Map<BlockPos, MachineComponent<?>> gatherComponents() {
    Map<BlockPos, MachineComponent<?>> map = new LinkedHashMap<>();
    for(BlockPos potentialPosition :
        controller.getFoundMachine().getPattern().getBlocks(controller.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)).keySet()) {
      BlockPos realPos = controller.getBlockPos().offset(potentialPosition);
      BlockEntity te = controller.getLevel().getBlockEntity(realPos);
      if (te instanceof MachineComponentEntity entity) {
        MachineComponent<?> component = entity.provideComponent();
        if (component != null) {
          if (entity instanceof ControllerAccessible accessible) {
            if (accessible.getControllerPos() != null) continue;
            accessible.setControllerPos(controller.getBlockPos());
          }
          map.put(realPos, component);
        }
      }
    }
    return map;
  }
}
