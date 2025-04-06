package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.component.HeightComponent;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

@Getter
@Setter
@MethodsReturnNonnullByDefault
public class HeightMeterEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<HeightComponent>, ControllerAccessible {
  @Nullable
  private BlockPos controllerPos;
  public HeightMeterEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.HEIGHT_METER.get(), pos, blockState);
  }

  @Override
  public HeightComponent provideComponent() {
    return new HeightComponent();
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.saveAdditional(nbt, pRegistries);
    if (controllerPos != null)
      nbt.putLong("controllerPos", controllerPos.asLong());
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
    super.loadAdditional(nbt, pRegistries);
    if (nbt.contains("controllerPos")) {
      controllerPos = BlockPos.of(nbt.getLong("controllerPos"));
    }
  }
}
