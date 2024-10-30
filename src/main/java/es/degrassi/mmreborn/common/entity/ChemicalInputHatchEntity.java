package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.block.prop.ChemicalHatchSize;
import es.degrassi.mmreborn.common.entity.base.ChemicalTankEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.integration.mekanism.EntityRegistration;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import mekanism.api.chemical.BasicChemicalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ChemicalInputHatchEntity extends ChemicalTankEntity implements MachineComponentEntity {

  public ChemicalInputHatchEntity(BlockPos pos, BlockState state) {
    super(EntityRegistration.CHEMICAL_INPUT_HATCH.get(), pos, state);
  }

  public ChemicalInputHatchEntity(BlockPos pos, BlockState state, ChemicalHatchSize size) {
    super(EntityRegistration.CHEMICAL_INPUT_HATCH.get(), pos, state, size, IOType.INPUT);
  }

  @Nullable
  @Override
  public MachineComponent provideComponent() {
    return new MachineComponent.ChemicalHatch(IOType.INPUT) {
      @Override
      public BasicChemicalTank getContainerProvider() {
        return getTank();
      }
    };
  }
}
