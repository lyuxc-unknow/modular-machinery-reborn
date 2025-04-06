package es.degrassi.mmreborn.client.container;

import es.degrassi.mmreborn.client.ModularMachineryRebornClient;
import es.degrassi.mmreborn.common.block.prop.ItemBusSize;
import es.degrassi.mmreborn.common.entity.base.TileItemBus;
import es.degrassi.mmreborn.common.registration.ContainerRegistration;
import es.degrassi.mmreborn.common.util.IOInventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

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
  }

  public ItemBusContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
    this(id, inv, ModularMachineryRebornClient.getClientSideItemBusEntity(buffer.readBlockPos()));
  }

  @Override
  public void init() {
    super.init();
    addInventorySlots(getEntity().getInventory(), getEntity().getSize(), new AtomicInteger(this.getFirstComponentSlotIndex()));
  }

  protected void addInventorySlots(IOInventory itemHandler, ItemBusSize size, AtomicInteger atomicInteger) {
    switch (size) {
      case TINY:
        addSlot(new SlotItemComponent(itemHandler.getInventory().get(0), atomicInteger.getAndIncrement(), 81, 30));
        break;
      case SMALL:
        addSlot(new SlotItemComponent(itemHandler.getInventory().get(0), atomicInteger.getAndIncrement(), 70, 18));
        addSlot(new SlotItemComponent(itemHandler.getInventory().get(1), atomicInteger.getAndIncrement(), 88, 18));
        addSlot(new SlotItemComponent(itemHandler.getInventory().get(2), atomicInteger.getAndIncrement(), 70, 36));
        addSlot(new SlotItemComponent(itemHandler.getInventory().get(3), atomicInteger.getAndIncrement(), 88, 36));
        break;
      case NORMAL:
        for (int zz = 0; zz < 2; zz++) {
          for (int xx = 0; xx < 3; xx++) {
            int index = zz * 3 + xx;
            addSlot(new SlotItemComponent(itemHandler.getInventory().get(index), atomicInteger.getAndIncrement(), 61 + xx * 18, 18 + zz * 18));
          }
        }
        break;
      case REINFORCED:
        for (int zz = 0; zz < 3; zz++) {
          for (int xx = 0; xx < 3; xx++) {
            int index = zz * 3 + xx;
            addSlot(new SlotItemComponent(itemHandler.getInventory().get(index), atomicInteger.getAndIncrement(), 61 + xx * 18, 13 + zz * 18));
          }
        }
        break;
      case BIG:
        for (int zz = 0; zz < 3; zz++) {
          for (int xx = 0; xx < 4; xx++) {
            int index = zz * 4 + xx;
            addSlot(new SlotItemComponent(itemHandler.getInventory().get(index), atomicInteger.getAndIncrement(), 52 + xx * 18, 18 + zz * 18));
          }
        }
        break;
      case HUGE:
        for (int zz = 0; zz < 4; zz++) {
          for (int xx = 0; xx < 4; xx++) {
            int index = zz * 4 + xx;
            addSlot(new SlotItemComponent(itemHandler.getInventory().get(index), atomicInteger.getAndIncrement(), 53 + xx * 18, 8 + zz * 18));
          }
        }
        break;
      case LUDICROUS:
        for (int zz = 0; zz < 4; zz++) {
          for (int xx = 0; xx < 8; xx++) {
            int index = zz * 8 + xx;
            addSlot(new SlotItemComponent(itemHandler.getInventory().get(index), atomicInteger.getAndIncrement(), 17 + xx * 18, 8 + zz * 18));
          }
        }
        break;
    }
  }
}
