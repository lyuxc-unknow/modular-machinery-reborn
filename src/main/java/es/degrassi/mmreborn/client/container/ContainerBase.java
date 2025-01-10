package es.degrassi.mmreborn.client.container;

import es.degrassi.mmreborn.api.network.DataType;
import es.degrassi.mmreborn.api.network.IData;
import es.degrassi.mmreborn.api.network.ISyncable;
import es.degrassi.mmreborn.api.network.ISyncableStuff;
import es.degrassi.mmreborn.api.network.syncable.IntegerSyncable;
import es.degrassi.mmreborn.api.network.syncable.ItemStackSyncable;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.network.server.SUpdateContainerPacket;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class ContainerBase<T extends ColorableMachineComponentEntity> extends AbstractContainerMenu {
  private final Player player;
  private final T entity;
  private final List<ISyncable<?, ?>> stuffToSync = new ArrayList<>();

  protected ContainerBase(T entity, Player player, @Nullable MenuType<?> menuType, int containerId) {
    super(menuType, containerId);
    this.player = player;
    this.entity = entity;
    addPlayerSlots();
    init();
  }

  public void init() {
    this.stuffToSync.clear();
    this.stuffToSync.add(DataType.createSyncable(ItemStack.class, this::getCarried, this::setCarried));
    if (entity instanceof ISyncableStuff syncableStuff) {
      syncableStuff.getStuffToSync(this.stuffToSync::add);
    }
  }

  public boolean needFullSync() {
    return this.entity.getLevel() != null && this.entity.getLevel().getGameTime() % 100 == 0;
  }

  @Override
  public void broadcastChanges() {
    if(this.player != null && player instanceof ServerPlayer sp) {
      if(this.needFullSync()) {
        List<IData<?>> toSync = new ArrayList<>();
        for(short id = 0; id < this.stuffToSync.size(); id++)
          toSync.add(this.stuffToSync.get(id).getData(id));
        PacketDistributor.sendToPlayer(sp, new SUpdateContainerPacket(this.containerId, toSync));
        return;
      }
      List<IData<?>> toSync = new ArrayList<>();
      for(short id = 0; id < this.stuffToSync.size(); id++) {
        if(this.stuffToSync.get(id).needSync())
          toSync.add(this.stuffToSync.get(id).getData(id));
      }
      if(!toSync.isEmpty())
        PacketDistributor.sendToPlayer(sp, new SUpdateContainerPacket(this.containerId, toSync));
    }
  }

  @Override
  protected DataSlot addDataSlot(DataSlot intReferenceHolder) {
    this.stuffToSync.add(IntegerSyncable.create(intReferenceHolder::get, intReferenceHolder::set));
    return intReferenceHolder;
  }

  @Override
  protected void addDataSlots(ContainerData array) {
    for(int i = 0; i < array.getCount(); i++) {
      int index = i;
      this.stuffToSync.add(IntegerSyncable.create(() -> array.get(index), integer -> array.set(index, integer)));
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void handleData(IData<?> data) {
    short id = data.getID();
    ISyncable syncable = this.stuffToSync.get(id);
    if(syncable != null)
      syncable.set(data.getValue());
  }

  protected Slot addSyncedSlot(Slot slot) {
    this.stuffToSync.add(ItemStackSyncable.create(slot::getItem, slot::set));
    return this.addSlot(slot);
  }

  protected void addPlayerSlots() {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 9; j++) {
        addSyncedSlot(new Slot(player.getInventory(), j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }
    for (int i = 0; i < 9; i++) {
      addSyncedSlot(new Slot(player.getInventory(), i, 8 + i * 18, 142));
    }
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);

    if (slot.hasItem()) {
      ItemStack itemstack1 = slot.getItem();
      itemstack = itemstack1.copy();
      if (index >= 0 && index < 27) {
        if (!this.moveItemStackTo(itemstack1, 27, 36, false)) {
          return ItemStack.EMPTY;
        }
      } else if (index >= 27 && index < 36) {
        if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.moveItemStackTo(itemstack1, 0, 36, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.getCount() == 0) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }

      if (itemstack1.getCount() == itemstack.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTake(player, itemstack1);
    }

    return itemstack;
  }

  @Override
  public boolean stillValid(Player player) {
    return player.level().getBlockState(this.entity.getBlockPos()) == this.entity.getBlockState() &&
        player.level().getBlockEntity(this.entity.getBlockPos()) == this.entity &&
        player.position().distanceToSqr(Vec3.atCenterOf(this.entity.getBlockPos())) <= 64;
  }
}
