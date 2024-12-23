package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.impl.capability.BasicExperienceTank;
import es.degrassi.mmreborn.common.block.prop.EnergyHatchSize;
import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.entity.ExperienceInputHatchEntity;
import es.degrassi.mmreborn.common.entity.FluidInputHatchEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ExperienceHatch;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Locale;

public abstract class ExperienceHatchEntity extends ColorableMachineComponentEntity implements MachineComponentEntity {
  protected ExperienceHatchSize size;
  protected IOType ioType;

  private final BasicExperienceTank experienceTank;

  public ExperienceHatchEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ExperienceHatchSize size, IOType ioType) {
    super(type, pos, state);
    this.size = size;
    this.ioType = ioType;
    this.experienceTank = buildTank();
  }

  public BasicExperienceTank getTank() {
    return experienceTank;
  }

  @Nullable
  @Override
  public ExperienceHatch provideComponent() {
    return new ExperienceHatch(ioType) {
      @Override
      public @NotNull IExperienceHandler getContainerProvider() {
        return experienceTank;
      }
    };
  }

  private BasicExperienceTank buildTank() {
    return new BasicExperienceTank(
        size == null ? 0 : size.getCapacity(),
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
        return ioType == null || !ioType.isInput();
      }

      @Override
      public boolean canReceive() {
        return ioType == null || ioType.isInput();
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

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.loadAdditional(compound, pRegistries);
    this.size = ExperienceHatchSize.value(compound.getString("hatchSize").toUpperCase(Locale.ROOT));
    this.ioType = IOType.getByString(compound.getString("ioType"));

    if (compound.contains("experience", Tag.TAG_COMPOUND))
      this.experienceTank.deserializeNBT(pRegistries, compound.getCompound("experience"));
    experienceTank.setCapacity(size.getCapacity());
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);
    compound.putString("hatchSize", this.size.getSerializedName());
    if (ioType == null) {
      ioType = this instanceof ExperienceInputHatchEntity ? IOType.INPUT : IOType.OUTPUT;
    }
    compound.putString("ioType", ioType.getSerializedName());

    compound.put("experience", experienceTank.serializeNBT(pRegistries));
  }
}
