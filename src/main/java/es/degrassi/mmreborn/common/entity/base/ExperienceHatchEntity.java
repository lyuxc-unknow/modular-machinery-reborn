package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.experiencelib.impl.capability.BasicExperienceHandler;
import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.common.block.prop.ExperienceHatchSize;
import es.degrassi.mmreborn.common.entity.ExperienceInputHatchEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ExperienceComponent;
import es.degrassi.mmreborn.common.network.server.component.SUpdateExperienceComponentPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Locale;

public abstract class ExperienceHatchEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<ExperienceComponent>, ControllerAccessible {
  protected ExperienceHatchSize size;
  protected IOType ioType;
  @Getter
  private BlockPos controllerPos;

  private final BasicExperienceHandler experienceTank;

  public ExperienceHatchEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ExperienceHatchSize size, IOType ioType) {
    super(type, pos, state);
    this.size = size;
    this.ioType = ioType;
    this.experienceTank = buildTank();
  }

  public BasicExperienceHandler getTank() {
    return experienceTank;
  }

  @Nullable
  @Override
  public ExperienceComponent provideComponent() {
    return new ExperienceComponent(this.getTank(), ioType);
  }

  private BasicExperienceHandler buildTank() {
    return new BasicExperienceHandler(
        1,
        size == null ? 0 : size.getCapacity(),
        () -> {
          if (getLevel() != null && !getLevel().isClientSide)
            PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) getLevel(),
                new ChunkPos(getBlockPos()),
                new SUpdateExperienceComponentPacket(getTank().getExperience(), getBlockPos())
            );
          if (getController() != null)
            getController().getProcessor().setMachineInventoryChanged();
        }
    ) {
      @Override
      public boolean canExtract(int tank) {
        return ioType == null || !ioType.isInput();
      }

      @Override
      public boolean canReceive(int tank) {
        return ioType == null || ioType.isInput();
      }

      @Override
      public boolean canAcceptExperience(int tank, long l) {
        return canReceive(tank) && receiveExperience(tank, l, true) > 0;
      }

      @Override
      public boolean canProvideExperience(int tank, long l) {
        return canExtract(tank) && extractExperience(tank, l, true) > 0;
      }

      @Override
      public long getMaxExtract(int tank) {
        return canExtract(tank) ? getExperienceCapacity() : 0;
      }

      @Override
      public long getMaxReceive(int tank) {
        return canReceive(tank) ? getExperienceCapacity() : 0;
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
    for (int i = 0; i < experienceTank.getTanks(); i++) {
      experienceTank.setCapacity(i, size.getCapacity());
    }
    if (compound.contains("controllerPos")) {
      controllerPos = BlockPos.of(compound.getLong("controllerPos"));
    }
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
    if (controllerPos != null)
      compound.putLong("controllerPos", controllerPos.asLong());
  }

  @Override
  public void setControllerPos(BlockPos pos) {
    this.controllerPos = pos;
  }
}
