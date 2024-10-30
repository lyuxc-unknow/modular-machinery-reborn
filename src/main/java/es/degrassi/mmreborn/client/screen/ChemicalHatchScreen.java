package es.degrassi.mmreborn.client.screen;

import com.google.common.collect.Lists;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.container.ChemicalHatchContainer;
import es.degrassi.mmreborn.client.container.FluidHatchContainer;
import es.degrassi.mmreborn.client.util.ChemicalRenderer;
import es.degrassi.mmreborn.client.util.FluidRenderer;
import es.degrassi.mmreborn.common.entity.base.ChemicalTankEntity;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class ChemicalHatchScreen extends BaseScreen<ChemicalHatchContainer, ChemicalTankEntity> {

  public ChemicalHatchScreen(ChemicalHatchContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
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
    ChemicalStack content = entity.getTank().getStack();
    guiGraphics.pose().pushPose();
    ChemicalRenderer.renderChemical(guiGraphics, leftPos + 15, topPos + 10, 20, 61, content, entity.getTank().getCapacity());
    guiGraphics.pose().popPose();
  }

  @Override
  protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    super.renderTooltip(guiGraphics, x, y);

    int offsetX = (this.width - this.getXSize()) / 2;
    int offsetZ = (this.height - this.getYSize()) / 2;

    if(x >= 15 + offsetX && x <= 35 + offsetX && y >= 10 + offsetZ && y <= 71 + offsetZ) {
        List<Component> text = Lists.newArrayList();

        ChemicalStack content = entity.getTank().getStack();
        long amt;
        if(content.getAmount() <= 0) {
          text.add(Component.translatable("tooltip.chemicalhatch.empty"));
          amt = 0;
        } else {
          text.add(content.getTextComponent());
          amt = content.getAmount();
        }
        text.add(Component.translatable("tooltip.chemicalhatch.tank", String.valueOf(amt), String.valueOf(entity.getTank().getCapacity())));

        Font font = Minecraft.getInstance().font;
        guiGraphics.renderTooltip(font, text.stream().map(Component::getVisualOrderText).toList(), x, y);
    }
  }
}
