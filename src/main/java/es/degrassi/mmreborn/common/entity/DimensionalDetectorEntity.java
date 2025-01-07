package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.component.DimensionComponent;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

@MethodsReturnNonnullByDefault
public class DimensionalDetectorEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<DimensionComponent> {
  public DimensionalDetectorEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.DIMENSIONAL_DETECTOR.get(), pos, blockState);
  }

  @Override
  public DimensionComponent provideComponent() {
    return new DimensionComponent(this);
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadAdditional(nbt, pRegistries);
  }
}
