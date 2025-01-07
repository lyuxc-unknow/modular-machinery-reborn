package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.entity.base.BlockEntityRestrictedTick;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.component.ChunkloadComponent;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import es.degrassi.mmreborn.common.util.Chunkloader;
import es.degrassi.mmreborn.common.util.ChunkloaderList;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@MethodsReturnNonnullByDefault
@Getter
public class ChunkloaderEntity extends BlockEntityRestrictedTick implements MachineComponentEntity<ChunkloadComponent> {
  private final Chunkloader chunkloader;
  public ChunkloaderEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.CHUNKLOADER.get(), pos, blockState);
    chunkloader = new Chunkloader(this);
  }

  @Override
  public ChunkloadComponent provideComponent() {
    return new ChunkloadComponent(this);
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
    nbt.putString("chunkloader", chunkloader.toString());
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadAdditional(nbt, pRegistries);
    chunkloader.deserializeNBT(pRegistries, nbt.getCompound("chunkloader"));
  }

  @Override
  public void setLevel(Level level) {
    super.setLevel(level);
    ChunkloaderList.add(this);
  }

  @Override
  public void doRestrictedTick() {
    chunkloader.serverTick();
  }

  @Override
  public void onLoad() {
    super.onLoad();
    chunkloader.init();
  }

  @Getter
  private boolean unloaded = false;

  @Override
  public void onChunkUnloaded() {
    super.onChunkUnloaded();
    this.unloaded = true;
  }

  @Override
  public void setRemoved() {
    chunkloader.onRemoved();
    super.setRemoved();
  }
}
