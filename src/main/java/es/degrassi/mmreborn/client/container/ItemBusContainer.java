package es.degrassi.mmreborn.client.container;

import es.degrassi.mmreborn.client.ModularMachineryRebornClient;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.entity.base.TileItemBus;
import es.degrassi.mmreborn.common.registration.ContainerRegistration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ItemBusContainer extends ContainerBase<TileItemBus> {

  public static void open(ServerPlayer player, TileItemBus machine) {
    player.openMenu(new MenuProvider() {
      @Override
      public @NotNull Component getDisplayName() {
        return Component.translatable("modular_machinery_reborn.gui.title.item_bus");
      }

      @Override
      public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ItemBusContainer(id, inv, machine);
      }
    }, buf -> buf.writeBlockPos(machine.getBlockPos()));
  }

  public ItemBusContainer(int id, Inventory playerInv, TileItemBus entity) {
    super(entity, playerInv.player, ContainerRegistration.ITEM_BUS.get(), id);

    addInventorySlots(entity.getInventory().asGUIAccess(), entity.getSize());
  }

  public ItemBusContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
    this(id, inv, ModularMachineryRebornClient.getClientSideItemBusEntity(buffer.readBlockPos()));
  }

  private void addInventorySlots(IItemHandlerModifiable itemHandler, ItemBusSize size) {
    switch (size) {
      case TINY:
        addSlot(new SlotItemHandler(itemHandler, 0, 81, 30));
        break;
      case SMALL:
        addSlot(new SlotItemHandler(itemHandler, 0, 70, 18));
        addSlot(new SlotItemHandler(itemHandler, 1, 88, 18));
        addSlot(new SlotItemHandler(itemHandler, 2, 70, 36));
        addSlot(new SlotItemHandler(itemHandler, 3, 88, 36));
        break;
      case NORMAL:
        for (int zz = 0; zz < 2; zz++) {
          for (int xx = 0; xx < 3; xx++) {
            int index = zz * 3 + xx;
            addSlot(new SlotItemHandler(itemHandler, index, 61 + xx * 18, 18 + zz * 18));
          }
        }
        break;
      case REINFORCED:
        for (int zz = 0; zz < 3; zz++) {
          for (int xx = 0; xx < 3; xx++) {
            int index = zz * 3 + xx;
            addSlot(new SlotItemHandler(itemHandler, index, 61 + xx * 18, 13 + zz * 18));
          }
        }
        break;
      case BIG:
        for (int zz = 0; zz < 3; zz++) {
          for (int xx = 0; xx < 4; xx++) {
            int index = zz * 4 + xx;
            addSlot(new SlotItemHandler(itemHandler, index, 52 + xx * 18, 18 + zz * 18));
          }
        }
        break;
      case HUGE:
        for (int zz = 0; zz < 4; zz++) {
          for (int xx = 0; xx < 4; xx++) {
            int index = zz * 4 + xx;
            addSlot(new SlotItemHandler(itemHandler, index, 53 + xx * 18, 8 + zz * 18));
          }
        }
        break;
      case LUDICROUS:
        for (int zz = 0; zz < 4; zz++) {
          for (int xx = 0; xx < 8; xx++) {
            int index = zz * 8 + xx;
            addSlot(new SlotItemHandler(itemHandler, index, 17 + xx * 18, 8 + zz * 18));
          }
        }
        break;
    }
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);

    if (slot.hasItem()) {
      ItemStack itemstack1 = slot.getItem();
      itemstack = itemstack1.copy();

      boolean changed = false;
      if (index >= 0 && index < 36) {
        if(this.moveItemStackTo(itemstack1, 36, slots.size(), false)) {
          changed = true;
        }
      }

      if(!changed) {
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
}
