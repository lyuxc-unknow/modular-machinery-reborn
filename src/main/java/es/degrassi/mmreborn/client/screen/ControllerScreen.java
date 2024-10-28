package es.degrassi.mmreborn.client.screen;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.container.ControllerContainer;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.util.RedstoneHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ControllerScreen extends AbstractContainerScreen<ControllerContainer> {
  private final MachineControllerEntity entity;

  public static final ResourceLocation TEXTURES_CONTROLLER = ResourceLocation.fromNamespaceAndPath(ModularMachineryReborn.MODID, "textures/gui/guicontroller.png");

  public ControllerScreen(ControllerContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
    super(pMenu, pPlayerInventory, pTitle);
    this.entity = pMenu.getEntity();
  }

  @Override
  public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
    // Neo: replicate the super method's implementation to insert the event between background and widgets
    super.render(guiGraphics, mouseX, mouseY, pPartialTick);
    renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    // render image background:
    guiGraphics.pose().pushPose();
    guiGraphics.setColor(1f, 1f, 1f, 1f);
    int i = (this.width - this.imageWidth) / 2;
    int j = (this.height - this.imageHeight) / 2;
    guiGraphics.blit(TEXTURES_CONTROLLER, i, j, 0, 0, imageWidth, imageHeight);
    guiGraphics.pose().popPose();
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(i, j, 0);
    float scale = 0.72f;
    guiGraphics.pose().scale(scale, scale, scale);
    int offsetX = 12;
    int offsetY = 12;

    int redstone = RedstoneHelper.getRedstoneLevel(entity);
    if(redstone > 0) {
      // render if redstone paused the machine
      Component drawnStop = Component.translatable("gui.controller.status.redstone_stopped");
      List<FormattedCharSequence> out = font.split(drawnStop, Mth.floor(135 * (1 / scale)));
      for (FormattedCharSequence draw : out) {
        offsetY += 10;
        guiGraphics.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
        offsetY += 10;
      }
      guiGraphics.pose().popPose();
      return;
    }

    DynamicMachine machine = entity.getFoundMachine();
    if(machine != DynamicMachine.DUMMY) {
      // render if the structure of machine is not null
      Component drawnHead = Component.translatable("gui.controller.structure", "");
      List<FormattedCharSequence>out = font.split(Component.literal(machine.getLocalizedName()), Mth.floor(135 * (1 / scale)));
      guiGraphics.drawString(font, drawnHead, offsetX, offsetY, 0xFFFFFF);
      for (FormattedCharSequence draw : out) {
        offsetY += 10;
        guiGraphics.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
      }
    } else {
      // render if the structure of machine is null
      Component drawnHead = Component.translatable("gui.controller.structure", Component.translatable("gui.controller.structure.none"));
      guiGraphics.drawString(font, drawnHead, offsetX, offsetY, 0xFFFFFF);
    }
    offsetY += 15;

    // render the current status
    Component status = Component.translatable("gui.controller.status");
    guiGraphics.drawString(font, status, offsetX, offsetY, 0xFFFFFF);
    String statusKey = entity.getCraftingStatus().getUnlocMessage();

    List<FormattedCharSequence> out = font.split(Component.translatable(statusKey), Mth.floor(135 * (1 / scale)));
    for (FormattedCharSequence draw : out) {
      offsetY += 10;
      guiGraphics.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
    }
    offsetY += 15;
    if (entity.hasActiveRecipe()) {
      // render if the recipe of machine is not null
      int percProgress = Mth.clamp(Mth.floor(entity.getCurrentActiveRecipeProgress() * 100F), 0, 100);
      Component progressStr = Component.translatable("gui.controller.status.crafting.progress", percProgress + "%");
      guiGraphics.drawString(font, progressStr, offsetX, offsetY, 0xFFFFFF);
    }

    guiGraphics.pose().popPose();
  }

  @Override
  protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {}
}
