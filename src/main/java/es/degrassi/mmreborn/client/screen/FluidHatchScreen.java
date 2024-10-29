package es.degrassi.mmreborn.client.screen;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.container.FluidHatchContainer;
import es.degrassi.mmreborn.client.util.FluidRenderer;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class FluidHatchScreen extends BaseScreen<FluidHatchContainer, FluidTankEntity> {

  public FluidHatchScreen(FluidHatchContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
    super(pMenu, pPlayerInventory, pTitle);
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
  }

  @Override
  public ResourceLocation getTexture() {
    return ResourceLocation.fromNamespaceAndPath(ModularMachineryReborn.MODID, "textures/gui/guibar.png");
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    // render image background:
    super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
    FluidStack content = entity.getTank().getFluid();
    guiGraphics.pose().pushPose();
    FluidRenderer.renderFluid(guiGraphics.pose(), leftPos + 15, topPos + 10, 20, 61, content, entity.getTank().getCapacity());
    guiGraphics.pose().popPose();
//    if (content.getAmount() > 0) {
//      drawTexturedModalRect(15, 10 + 61 - pxFilled, tas, 20, pxFilled);
//    } else if (Mods.MEKANISM.isPresent()){
//      drawMekGasContent();
//    }
//    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//    this.mc.getTextureManager().bindTexture(TEXTURES_FLUID_HATCH);
  }

  @Override
  protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    super.renderTooltip(guiGraphics, x, y);

    int offsetX = (this.width - this.getXSize()) / 2;
    int offsetZ = (this.height - this.getYSize()) / 2;

    if(x >= 15 + offsetX && x <= 35 + offsetX && y >= 10 + offsetZ && y <= 71 + offsetZ) {
//      if(Mods.MEKANISM.isPresent()) {
//        drawMekTooltip(x, y);
//      } else {
        List<Component> text = Lists.newArrayList();

        FluidStack content = entity.getTank().getFluid();
        int amt;
        if(content.getAmount() <= 0) {
          text.add(Component.translatable("tooltip.fluidhatch.empty"));
          amt = 0;
        } else {
          text.add(content.getHoverName());
          amt = content.getAmount();
        }
        text.add(Component.translatable("tooltip.fluidhatch.tank", String.valueOf(amt), String.valueOf(entity.getTank().getCapacity())));

        Font font = Minecraft.getInstance().font;
        guiGraphics.renderTooltip(font, text.stream().map(Component::getVisualOrderText).toList(), x, y);
//      }
    }
  }

  private void drawMekTooltip(int x, int y) {
    List<String> text = Lists.newArrayList();

    FluidStack content = entity.getTank().getFluid();
    int amt;
    if(content.getAmount() <= 0) {
//      if(entity.getTank() instanceof HybridGasTank) {
//        GasStack gasContent = ((HybridGasTank) tank.getTank()).getGas();
//        if(gasContent == null || gasContent.amount <= 0) {
//          text.add(I18n.format("tooltip.fluidhatch.empty"));
//          amt = 0;
//          text.add(I18n.format("tooltip.fluidhatch.tank", String.valueOf(amt), String.valueOf(tank.getTank().getCapacity())));
//        } else {
//          if(Mods.MEKANISM.isPresent()) {
//            text.add(I18n.format("tooltip.fluidhatch.gas"));
//          }
//          text.add(gasContent.getGas().getLocalizedName());
//          amt = gasContent.amount;
//          text.add(I18n.format("tooltip.fluidhatch.tank.gas", String.valueOf(amt), String.valueOf(tank.getTank().getCapacity())));
//        }
//      } else {
//        text.add(I18n.format("tooltip.fluidhatch.empty"));
//        amt = 0;
//        text.add(I18n.format("tooltip.fluidhatch.tank", String.valueOf(amt), String.valueOf(tank.getTank().getCapacity())));
//      }
    } else {
//      if(Mods.MEKANISM.isPresent()) {
//        text.add(I18n.format("tooltip.fluidhatch.fluid"));
//      }
//      text.add(content.getLocalizedName());
//      amt = content.amount;
//      text.add(I18n.format("tooltip.fluidhatch.tank", String.valueOf(amt), String.valueOf(tank.getTank().getCapacity())));
    }

//    FontRenderer font = Minecraft.getMinecraft().fontRenderer;
//    drawHoveringText(text, x, z, (font == null ? fontRenderer : font));
  }
}
