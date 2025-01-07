package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.common.block.prop.FluidHatchSize;
import es.degrassi.mmreborn.common.entity.FluidInputHatchEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.FluidComponent;
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
public abstract class FluidTankEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<FluidComponent>, ControllerAccessible {
  private HybridTank tank;
  private IOType ioType;
  private FluidHatchSize hatchSize;
  private BlockPos controllerPos;

  public FluidTankEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, FluidHatchSize size, IOType ioType) {
    super(type, pos, state);
    this.tank = size.buildTank(this, ioType == IOType.INPUT, ioType == IOType.OUTPUT);
    this.hatchSize = size;
    this.ioType = ioType;

    this.tank.setListener(() -> {
      if (getController() != null)
        getController().getProcessor().setMachineInventoryChanged();
    });
  }

  @Override
  public FluidComponent provideComponent() {
    return new FluidComponent(this.getTank(), ioType);
  }

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.loadAdditional(compound, provider);
    this.ioType = IOType.getByString(compound.getString("ioType"));
    this.hatchSize = FluidHatchSize.value(compound.getString("size"));
    HybridTank newTank = hatchSize.buildTank(this, ioType == IOType.INPUT, ioType == IOType.OUTPUT);
    CompoundTag tankTag = compound.getCompound("tank");
    newTank.readFromNBT(provider, tankTag);
    this.tank = newTank;
    if (compound.contains("controllerPos")) {
      controllerPos = BlockPos.of(compound.getLong("controllerPos"));
    }

    this.tank.setListener(() -> {
      if (getController() != null)
        getController().getProcessor().setMachineInventoryChanged();
    });
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.saveAdditional(compound, provider);
    if (ioType == null) {
      ioType = this instanceof FluidInputHatchEntity ? IOType.INPUT : IOType.OUTPUT;
    }
    compound.putString("ioType", ioType.getSerializedName());
    compound.putString("size", this.hatchSize.getSerializedName());
    CompoundTag tankTag = new CompoundTag();
    this.tank.writeToNBT(provider, tankTag);
    compound.put("tank", tankTag);
    if (controllerPos != null)
      compound.putLong("controllerPos", controllerPos.asLong());
  }

  @Override
  public void setControllerPos(BlockPos pos) {
    this.controllerPos = pos;
  }
}
