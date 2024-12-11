package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.experiencelib.impl.capability.BasicExperienceTank;
import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.network.server.component.SUpdateExperienceComponentPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Locale;

public abstract class ExperienceHatchEntity extends ColorableMachineComponentEntity implements MachineComponentEntity {
  protected ExperienceHatchSize size;

  private final BasicExperienceTank experienceTank;

  public ExperienceHatchEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    this.experienceTank = new BasicExperienceTank(0, null);
  }

  public ExperienceHatchEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ExperienceHatchSize size, IOType ioType) {
    super(type, pos, state);
    this.size = size;
    this.experienceTank = new BasicExperienceTank(
        size.getCapacity(),
        () -> {
          if (getLevel() != null && !getLevel().isClientSide)
            PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) getLevel(),
                new ChunkPos(getBlockPos()),
                new SUpdateExperienceComponentPacket(getTank().getExperience(), getBlockPos())
            );
        }
    ) {

      @Override
      public boolean canExtract() {
        return !ioType.isInput();
      }

      @Override
      public boolean canReceive() {
        return ioType.isInput();
      }

      @Override
      public boolean canAcceptExperience(long l) {
        return canReceive() && receiveExperience(l, true) > 0;
      }

      @Override
      public boolean canProvideExperience(long l) {
        return canExtract() && extractExperience(l, true) > 0;
      }

      @Override
      public long getMaxExtract() {
        return canExtract() ? getExperienceCapacity() : 0;
      }

      @Override
      public long getMaxReceive() {
        return canReceive() ? getExperienceCapacity() : 0;
      }
    };
  }

  public BasicExperienceTank getTank() {
    return experienceTank;
  }

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.loadAdditional(compound, pRegistries);

    Tag experienceTank = compound.get("experience");
    if (experienceTank != null)
      this.experienceTank.deserializeNBT(pRegistries, experienceTank);
    this.size = ExperienceHatchSize.value(compound.getString("hatchSize").toUpperCase(Locale.ROOT));
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);

    compound.put("experience", experienceTank.serializeNBT(pRegistries));
    compound.putString("hatchSize", this.size.getSerializedName());
  }

  //MM stuff

  public ExperienceHatchSize getTier() {
    return size;
  }
}
