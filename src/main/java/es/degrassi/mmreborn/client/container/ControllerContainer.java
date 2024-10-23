package es.degrassi.mmreborn.client.container;

import es.degrassi.mmreborn.client.ModularMachineryRebornClient;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.registration.ContainerRegistration;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Getter
public class ControllerContainer extends AbstractContainerMenu {
  private final MachineControllerEntity entity;
  private final Player player;

  public static void open(ServerPlayer player, MachineControllerEntity machine) {
    player.openMenu(new MenuProvider() {
      @Override
      public @NotNull Component getDisplayName() {
        return Component.translatable("modular_machinery_reborn.gui.title.controller");
      }

      @Override
      public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ControllerContainer(id, inv, machine);
      }
    }, buf -> buf.writeBlockPos(machine.getBlockPos()));
  }

  public ControllerContainer(int id, Inventory playerInv, MachineControllerEntity entity) {
    super(ContainerRegistration.CONTROLLER.get(), id);
    this.entity = entity;
    this.player = playerInv.player;
    addPlayerSlots();
  }

  public ControllerContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
    this(id, inv, ModularMachineryRebornClient.getClientSideMachineControllerEntity(buffer.readBlockPos()));
  }

  private void addPlayerSlots() {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 9; j++) {
        addSlot(new Slot(player.getInventory(), j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }
    for (int i = 0; i < 9; i++) {
      addSlot(new Slot(player.getInventory(), i, 8 + i * 18, 142));
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
