package es.degrassi.mmreborn.common.entity;

import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.common.block.prop.ParallelHatchSize;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.entity.base.MachineComponentEntity;
import es.degrassi.mmreborn.common.machine.component.ParallelComponent;
import es.degrassi.mmreborn.common.registration.EntityRegistration;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ParallelHatchEntity extends ColorableMachineComponentEntity implements MachineComponentEntity<ParallelComponent>, ControllerAccessible {
  @Nullable
  private BlockPos controllerPos;
  protected ParallelHatchSize size;
  private ParallelComponent component;

  public ParallelHatchEntity(BlockPos pos, BlockState state, ParallelHatchSize size) {
    super(EntityRegistration.PARALLEL_HATCH.get(), pos, state);
    this.size = size;
    this.component = new ParallelComponent(size);
  }

  public ParallelHatchEntity(BlockPos pos, BlockState state) {
    this(pos, state, ParallelHatchSize.BASIC);
  }

  @Override
  public void setControllerPos(BlockPos pos) {
    this.controllerPos = pos;
  }

  public void setCores(int cores) {
    this.component.setCores(cores, getLevel(), getBlockPos());
    if (getController() != null)
      getController().getProcessor().updateActiveCores(cores);
  }

  public int getCores() {
    return this.component.getContainerProvider();
  }

  public int getMaxCores() {
    return size.max;
  }

  @Override
  public ParallelComponent provideComponent() {
    return component;
  }

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.loadAdditional(compound, pRegistries);
    size = ParallelHatchSize.value(compound.getString("size"));
    this.component = new ParallelComponent(size);
    if (compound.contains("controllerPos")) {
      controllerPos = BlockPos.of(compound.getLong("controllerPos"));
    }
    if (compound.contains("cores")) {
      this.setCores(compound.getInt("cores"));
    }
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);
    compound.putString("size", size.getSerializedName());
    if (controllerPos != null)
      compound.putLong("controllerPos", controllerPos.asLong());
    compound.putInt("cores", component.getContainerProvider());
    compound.putInt("maxCores", getMaxCores());
  }
}
