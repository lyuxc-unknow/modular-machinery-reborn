package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.common.block.prop.ChemicalHatchSize;
import es.degrassi.mmreborn.common.machine.IOType;
import lombok.Getter;
import lombok.Setter;
import mekanism.api.chemical.BasicChemicalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Getter
@Setter
public abstract class ChemicalTankEntity extends ColorableMachineComponentEntity implements MachineComponentEntity {
  private BasicChemicalTank tank;
  private IOType ioType;
  private ChemicalHatchSize hatchSize;

  public ChemicalTankEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public ChemicalTankEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ChemicalHatchSize size, IOType ioType) {
    super(type, pos, state);
    this.tank = size.buildTank(this, ioType == IOType.INPUT, ioType == IOType.OUTPUT);
    this.hatchSize = size;
  }

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.loadAdditional(compound, provider);
    this.ioType = compound.getBoolean("input") ? IOType.INPUT : IOType.OUTPUT;
    this.hatchSize = ChemicalHatchSize.value(compound.getString("size"));
    BasicChemicalTank newTank = hatchSize.buildTank(this, ioType == IOType.INPUT, ioType == IOType.OUTPUT);
    CompoundTag tankTag = compound.getCompound("tank");
    newTank.deserializeNBT(provider, tankTag);
    this.tank = newTank;
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
    super.saveAdditional(compound, provider);
    compound.putBoolean("input", ioType == IOType.INPUT);
    compound.putString("size", this.hatchSize.getSerializedName());
    CompoundTag tankTag = this.tank.serializeNBT(provider);
    compound.put("tank", tankTag);
  }
}
