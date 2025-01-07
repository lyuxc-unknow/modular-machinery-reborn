package es.degrassi.mmreborn.common.machine.component;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.entity.ChunkloaderEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.util.Chunkloader;

public class ChunkloadComponent extends MachineComponent<Chunkloader> {
  private final ChunkloaderEntity entity;

  public ChunkloadComponent(ChunkloaderEntity entity) {
    super(IOType.OUTPUT);
    this.entity = entity;
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentRegistration.COMPONENT_CHUNKLOAD.get();
  }

  @Override
  public Chunkloader getContainerProvider() {
    return entity.getChunkloader();
  }
}
