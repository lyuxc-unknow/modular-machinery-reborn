package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.util.HybridTank;
import es.degrassi.mmreborn.common.util.Mods;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Getter
@Setter
public abstract class FluidTankEntity extends ColorableMachineComponentEntity implements MachineComponentEntity {

  private HybridTank tank;
  private IOType ioType;
  private FluidHatchSize hatchSize;

  public FluidTankEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public FluidTankEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, FluidHatchSize size, IOType ioType) {
    super(type, pos, state);
    this.tank = size.buildTank(this, ioType == IOType.INPUT, ioType == IOType.OUTPUT);
    this.hatchSize = size;
  }

  @Override
  public void readCustomNBT(CompoundTag compound, HolderLookup.Provider provider) {
    super.readCustomNBT(compound, provider);

    this.ioType = compound.getBoolean("input") ? IOType.INPUT : IOType.OUTPUT;
    this.hatchSize = FluidHatchSize.value(compound.getString("size"));
    HybridTank newTank = hatchSize.buildTank(this, ioType == IOType.INPUT, ioType == IOType.OUTPUT);
    CompoundTag tankTag = compound.getCompound("tank");
    newTank.readFromNBT(provider, tankTag);
    this.tank = newTank;
    if(Mods.MEKANISM.isPresent()) {
//      this.readMekGasData(tankTag);
    }
  }

  @Override
  public void writeCustomNBT(CompoundTag compound, HolderLookup.Provider provider) {
    super.writeCustomNBT(compound, provider);

    compound.putBoolean("input", ioType == IOType.INPUT);
    compound.putString("size", this.hatchSize.getSerializedName());
    CompoundTag tankTag = new CompoundTag();
    this.tank.writeToNBT(provider, tankTag);
    if(Mods.MEKANISM.isPresent()) {
//      this.writeMekGasData(tankTag);
    }
    compound.put("tank", tankTag);
  }

  @Nullable
  @Override
  public MachineComponent provideComponent() {
    return new MachineComponent.FluidHatch(ioType) {
      @Override
      public HybridTank getContainerProvider() {
        return FluidTankEntity.this.tank;
      }
    };
  }
}
