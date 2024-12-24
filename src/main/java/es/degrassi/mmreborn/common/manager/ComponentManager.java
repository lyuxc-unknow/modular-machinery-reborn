package es.degrassi.mmreborn.common.manager;

import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmreborn.common.crafting.modifier.RecipeModifier;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ComponentManager {
  private final MachineControllerEntity controller;

  private final Map<BlockPos, MachineComponent<?>> foundComponents = new LinkedHashMap<>();
  private final Map<BlockPos, ModifierReplacement> foundModifiers = new LinkedHashMap<>();

  public ComponentManager(MachineControllerEntity entity) {
    this.controller = entity;
  }

  public final void reset() {
    foundComponents.clear();
    foundModifiers.clear();
    controller.setChanged();
  }

  public final void updateComponents() {
    if (controller.getFoundMachine() == DynamicMachine.DUMMY) return;
    if (controller.getLevel().getGameTime() % 20 == 0) {
      foundComponents.clear();
      foundComponents.putAll(gatherComponents());
      foundModifiers.putAll(gatherModifiers());
    }
    controller.setChanged();
  }

  public final List<MachineComponent<?>> getFoundComponentsList() {
    return foundComponents.values().stream().toList();
  }

  public List<ModifierReplacement> getFoundModifiersList() {
    return foundModifiers.values().stream().toList();
  }

  public final Map<BlockPos, MachineComponent<?>> getFoundComponentsMap() {
    return foundComponents;
  }

  public final Map<BlockPos, ModifierReplacement> getFoundModifiersMap() {
    return foundModifiers;
  }

  private Map<BlockPos, MachineComponent<?>> gatherComponents() {
    Map<BlockPos, MachineComponent<?>> map = new LinkedHashMap<>();
    for (BlockPos potentialPosition :
        controller.getFoundMachine().getPattern().getBlocksFiltered(controller.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)).keySet()) {
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

  private Map<BlockPos, ModifierReplacement> gatherModifiers() {
    Map<BlockPos, ModifierReplacement> map = new LinkedHashMap<>();
    controller.getFoundMachine()
        .getPattern()
        .getPattern()
        .getModifiers(controller.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))
        .forEach((potentialPosition, modifier) -> {
          BlockPos realPos = controller.getBlockPos().offset(potentialPosition);
          BlockInWorld biw = new BlockInWorld(controller.getLevel(), realPos, false);
          if (modifier.getIngredient().getAll().stream().anyMatch(state -> state.test(biw)))
            map.put(realPos, modifier);
        });
    return map;
  }
}
