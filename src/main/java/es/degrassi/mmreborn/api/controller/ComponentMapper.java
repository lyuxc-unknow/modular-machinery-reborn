package es.degrassi.mmreborn.api.controller;

import es.degrassi.mmreborn.common.machine.MachineComponent;
import net.minecraft.core.BlockPos;

import java.util.Map;

public interface ComponentMapper {
  Map<BlockPos, MachineComponent<?>> getFoundComponentsMap();
}
