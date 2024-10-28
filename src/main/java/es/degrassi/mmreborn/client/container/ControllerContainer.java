package es.degrassi.mmreborn.client.container;

import es.degrassi.mmreborn.client.ModularMachineryRebornClient;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.registration.ContainerRegistration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public class ControllerContainer extends ContainerBase<MachineControllerEntity> {

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
    super(entity, playerInv.player, ContainerRegistration.CONTROLLER.get(), id);
  }

  public ControllerContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
    this(id, inv, ModularMachineryRebornClient.getClientSideMachineControllerEntity(buffer.readBlockPos()));
  }
}
