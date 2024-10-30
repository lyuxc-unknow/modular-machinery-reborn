package es.degrassi.mmreborn.client.container;

import es.degrassi.mmreborn.client.ModularMachineryRebornClient;
import es.degrassi.mmreborn.common.entity.base.ChemicalTankEntity;
import es.degrassi.mmreborn.common.integration.mekanism.ContainerRegistration;
import es.degrassi.mmreborn.common.integration.mekanism.MekanismClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public class ChemicalHatchContainer extends ContainerBase<ChemicalTankEntity> {

  public static void open(ServerPlayer player, ChemicalTankEntity machine) {
    player.openMenu(new MenuProvider() {
      @Override
      public @NotNull Component getDisplayName() {
        return Component.translatable("modular_machinery_reborn.gui.title.chemical_hatch");
      }

      @Override
      public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ChemicalHatchContainer(id, inv, machine);
      }
    }, buf -> buf.writeBlockPos(machine.getBlockPos()));
  }

  public ChemicalHatchContainer(int id, Inventory playerInv, ChemicalTankEntity entity) {
    super(entity, playerInv.player, ContainerRegistration.CHEMICAL_HATCH.get(), id);
  }

  public ChemicalHatchContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
    this(id, inv, MekanismClient.getClientSideChemicalHatchEntity(buffer.readBlockPos()));
  }
}
