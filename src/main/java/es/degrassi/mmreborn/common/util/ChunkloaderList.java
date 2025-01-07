package es.degrassi.mmreborn.common.util;

import es.degrassi.mmreborn.common.entity.ChunkloaderEntity;
import net.minecraft.world.level.ChunkPos;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ChunkloaderList {

  private static final List<WeakReference<ChunkloaderEntity>> LOADED_MACHINES = Collections.synchronizedList(new ArrayList<>());

  public static void add(ChunkloaderEntity tile) {
    if (tile.getLevel() != null && !tile.getLevel().isClientSide())
      LOADED_MACHINES.add(new WeakReference<>(tile));
  }

  public static Optional<ChunkloaderEntity> findInSameChunk(ChunkloaderEntity machine) {
    return getLoadedMachines().stream()
        .filter(tile -> tile != machine && tile.getLevel() == machine.getLevel() && new ChunkPos(tile.getBlockPos()).equals(new ChunkPos(machine.getBlockPos())))
        .findFirst();
  }

  public static List<ChunkloaderEntity> getLoadedMachines() {
    Iterator<WeakReference<ChunkloaderEntity>> iterator = LOADED_MACHINES.iterator();
    List<ChunkloaderEntity> loadedMachines = new ArrayList<>();
    while (iterator.hasNext()) {
      ChunkloaderEntity tile = iterator.next().get();
      if (tile == null || tile.isRemoved())
        iterator.remove();
      else
        loadedMachines.add(tile);
    }
    return loadedMachines;
  }
}
