package es.degrassi.mmreborn.common.util;

import es.degrassi.mmreborn.common.entity.ChunkloaderEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

@Getter
public class Chunkloader implements INBTSerializable<CompoundTag> {
  private final ChunkloaderEntity entity;
  private int radius;
  private int tempo = -1;
  private boolean active;

  public Chunkloader(ChunkloaderEntity entity, int radius, boolean active) {
    this.active = active;
    this.radius = radius;
    this.entity = entity;
  }

  public Chunkloader(ChunkloaderEntity entity) {
    this(entity, 1, false);
  }

  @Override
  public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
    CompoundTag nbt = new CompoundTag();
    nbt.putBoolean("active", this.active);
    nbt.putInt("radius", this.radius);
    return nbt;
  }

  @Override
  public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
    if(nbt.contains("active", CompoundTag.TAG_BYTE))
      this.active = nbt.getBoolean("active");
    if(nbt.contains("radius", CompoundTag.TAG_INT))
      this.radius = nbt.getInt("radius");
  }
  private static final TicketType<BlockPos> MACHINE_CHUNKLOADER = TicketType.create("chunkloader", Vec3i::compareTo, 0);

  public void setActive(ServerLevel level, int radius) {
    if(this.active)
      this.setInactive(level);

    this.active = true;
    this.radius = radius;

    BlockPos machinePos = getEntity().getBlockPos();
    ChunkPos chunk = new ChunkPos(machinePos);
    level.setChunkForced(chunk.x, chunk.z, true);
    level.getChunkSource().addRegionTicket(MACHINE_CHUNKLOADER, chunk, radius + 1, machinePos);
  }

  public void setActiveWithTempo(ServerLevel level, int radius, int tempo) {
    this.tempo = Math.max(this.tempo, tempo);
    if(!this.active || this.radius < radius)
      this.setActive(level, radius);
  }

  public void setInactive(ServerLevel level) {
    this.active = false;

    BlockPos machinePos = getEntity().getBlockPos();
    ChunkPos chunk = new ChunkPos(machinePos);
    if(ChunkloaderList.findInSameChunk(getEntity()).isEmpty())
      level.setChunkForced(chunk.x, chunk.z, false);
    level.getChunkSource().removeRegionTicket(MACHINE_CHUNKLOADER, chunk, this.radius + 1, machinePos);
  }

  public void serverTick() {
    if(this.tempo >= 0 && this.tempo-- == 0)
      this.setInactive((ServerLevel) getEntity().getLevel());
  }

  public void onRemoved() {
    if(getEntity().getLevel() instanceof ServerLevel level && !getEntity().isUnloaded())
      this.setInactive(level);
  }

  public void init() {
    if(this.active && getEntity().getLevel() instanceof ServerLevel level) {
      ChunkPos pos = new ChunkPos(getEntity().getBlockPos());
      if(level.getChunk(pos.x, pos.z, ChunkStatus.EMPTY, false) instanceof LevelChunk)
        this.setActive(level, this.radius);
      else
        TaskDelayer.enqueue(1, () -> this.setActive(level, this.radius));
    }
  }
}
