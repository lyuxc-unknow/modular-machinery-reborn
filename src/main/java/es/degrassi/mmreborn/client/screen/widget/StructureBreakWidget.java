package es.degrassi.mmreborn.client.screen.widget;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.network.client.CBreakStructurePacket;
import es.degrassi.mmreborn.common.util.TextureSizeHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import static es.degrassi.mmreborn.client.screen.BaseScreen.TAB;
import static es.degrassi.mmreborn.client.screen.BaseScreen.TAB_HOVERED;

public class StructureBreakWidget extends AbstractWidget {
  private static final ResourceLocation TEXTURE = ModularMachineryReborn.rl("textures/gui/structure_breaker.png");

  private final ResourceLocation machine;
  private final BlockPos controllerPos;

  public final Component component = Component.translatable("modular_machinery_reborn.gui.structure_break_button");

  public StructureBreakWidget(int x, int y, ResourceLocation machine, BlockPos controllerPos) {
    super(x, y, TextureSizeHelper.getWidth(TAB), TextureSizeHelper.getHeight(TAB), Component.literal("structure breaker"));
    this.machine = machine;
    this.controllerPos = controllerPos;
  }

  @Override
  public @NotNull Tooltip getTooltip() {
    return Tooltip.create(component);
  }

  @Override
  protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    ResourceLocation tab = isHoveredOrFocused() ? TAB_HOVERED : TAB;
    int x = getX() - (isHoveredOrFocused() ? 2 : 0);
    int y = getY();
    int width = TextureSizeHelper.getWidth(tab), height = TextureSizeHelper.getHeight(tab);
    this.width = width;
    this.height = height;
    guiGraphics.blit(tab, x, y, 0, 0, width, height, width, height);
    width = TextureSizeHelper.getWidth(TEXTURE);
    height = TextureSizeHelper.getHeight(TEXTURE);
    x += isHoveredOrFocused() ? 2 : 0;
    guiGraphics.blit(TEXTURE, x + 5, y + 5, 0, 0, width, height, width, height);
  }

  @Override
  public void onClick(double mouseX, double mouseY, int button) {
    PacketDistributor.sendToServer(new CBreakStructurePacket(machine, controllerPos));
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

  }
}
