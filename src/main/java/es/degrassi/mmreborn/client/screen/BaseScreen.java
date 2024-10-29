package es.degrassi.mmreborn.client.screen;

import es.degrassi.mmreborn.client.container.ContainerBase;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class BaseScreen<T extends ContainerBase<E>, E extends ColorableMachineComponentEntity> extends AbstractContainerScreen<T> {
  protected final E entity;
  public BaseScreen(T menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.entity = menu.getEntity();
  }

  public abstract ResourceLocation getTexture();

  @Override
  public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
    super.render(guiGraphics, mouseX, mouseY, pPartialTick);
    renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    guiGraphics.pose().pushPose();
    guiGraphics.setColor(1f, 1f, 1f, 1f);
    this.leftPos = (this.width - this.imageWidth) / 2;
    this.topPos = (this.height - this.imageHeight) / 2;
    guiGraphics.blit(getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);
    guiGraphics.pose().popPose();
  }
}
