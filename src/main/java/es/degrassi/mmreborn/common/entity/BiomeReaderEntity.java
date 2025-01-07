package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.component.BiomeComponent;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNonnullByDefault
@Getter
@Setter
public class BiomeReaderEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<BiomeComponent> {
  public BiomeReaderEntity(BlockPos pos, BlockState blockState) {
    super(EntityRegistration.BIOME_READER.get(), pos, blockState);
  }

  @Override
  public @Nullable BiomeComponent provideComponent() {
    return new BiomeComponent(this);
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
