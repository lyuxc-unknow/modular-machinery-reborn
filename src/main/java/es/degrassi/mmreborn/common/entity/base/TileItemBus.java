package es.degrassi.mmreborn.common.entity.base;

import es.degrassi.mmreborn.api.controller.ControllerAccessible;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.entity.ItemInputBusEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ItemComponent;
import es.degrassi.mmreborn.common.network.server.component.SUpdateItemComponentPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

@Getter
public abstract class TileItemBus extends TileInventory implements MachineComponentEntity<ItemComponent>, ControllerAccessible {

  private BlockPos controllerPos;
  private ItemBusSize size;
  private IOType ioType;

  public TileItemBus(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState, ItemBusSize size, IOType ioType) {
    super(entityType, pos, blockState, size.getSlotCount());
    this.size = size;
    this.ioType = ioType;

    this.inventory.setListener((slot, stack) -> {
      if (getController() != null)
        getController().getProcessor().setMachineInventoryChanged();
      if (getLevel() instanceof ServerLevel l)
        PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()),
            new SUpdateItemComponentPacket(slot, stack, getBlockPos()));
    });
  }

  @Nullable
  @Override
  public ItemComponent provideComponent() {
    return new ItemComponent(this.getInventory(), ioType);
  }

  @Override
  protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.loadAdditional(compound, pRegistries);

    this.size = ItemBusSize.value(compound.getString("busSize"));
    this.ioType = IOType.getByString(compound.getString("ioType"));
    if (compound.contains("controllerPos")) {
      controllerPos = BlockPos.of(compound.getLong("controllerPos"));
    }

    this.inventory.setListener((slot, stack) -> {
      if (getController() != null)
        getController().getProcessor().setMachineInventoryChanged();
      if (getLevel() instanceof ServerLevel l)
        PacketDistributor.sendToPlayersTrackingChunk(l, new ChunkPos(getBlockPos()),
            new SUpdateItemComponentPacket(slot, stack, getBlockPos()));
    });
  }

  @Override
  protected void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
    super.saveAdditional(compound, pRegistries);

    compound.putString("busSize", this.size.getSerializedName());
    if (ioType == null) {
      ioType = this instanceof ItemInputBusEntity ? IOType.INPUT : IOType.OUTPUT;
    }
    compound.putString("ioType", this.ioType.getSerializedName());
    if (controllerPos != null)
      compound.putLong("controllerPos", controllerPos.asLong());
  }

  @Override
  public void setControllerPos(BlockPos pos) {
    this.controllerPos = pos;
  }
}
