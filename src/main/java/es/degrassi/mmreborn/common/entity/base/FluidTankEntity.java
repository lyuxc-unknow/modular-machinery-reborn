package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.FluidHatch;
import es.degrassi.mmreborn.common.util.HybridTank;
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
    this.ioType = ioType;
  }

  @Override
  public FluidHatch provideComponent() {
    return new FluidHatch(ioType) {
      @Override
      public HybridTank getContainerProvider() {
        return getTank();
      }
    };
  }

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.loadAdditional(compound, provider);
    this.ioType = compound.getBoolean("input") ? IOType.INPUT : IOType.OUTPUT;
    this.hatchSize = FluidHatchSize.value(compound.getString("size"));
    HybridTank newTank = hatchSize.buildTank(this, ioType == IOType.INPUT, ioType == IOType.OUTPUT);
    CompoundTag tankTag = compound.getCompound("tank");
    newTank.readFromNBT(provider, tankTag);
    this.tank = newTank;
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.saveAdditional(compound, provider);
    compound.putBoolean("input", ioType == IOType.INPUT);
    compound.putString("size", this.hatchSize.getSerializedName());
    CompoundTag tankTag = new CompoundTag();
    this.tank.writeToNBT(provider, tankTag);
    compound.put("tank", tankTag);
  }
}
