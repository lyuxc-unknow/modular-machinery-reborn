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

public class StructureBreakWidget extends AbstractWidget {
  private static final ResourceLocation TAB = ModularMachineryReborn.rl("textures/gui/widget/base_tab.png");
  private static final ResourceLocation TAB_BOTTOM = ModularMachineryReborn.rl("textures/gui/widget/base_tab_to_bottom.png");
  private static final ResourceLocation TEXTURE = ModularMachineryReborn.rl("textures/gui/structure_breaker.png");

  private final ResourceLocation machine;
  private final BlockPos controllerPos;
  private final boolean bottom;

  public final Component component = Component.translatable("modular_machinery_reborn.gui.structure_break_button");

  public StructureBreakWidget(int x, int y, ResourceLocation machine, BlockPos controllerPos, boolean bottom) {
    super(x, y, TextureSizeHelper.getWidth(bottom ? TAB_BOTTOM : TAB), TextureSizeHelper.getHeight(bottom ? TAB_BOTTOM : TAB), Component.literal("structure breaker"));
    this.machine = machine;
    this.controllerPos = controllerPos;
    this.bottom = bottom;
  }

  @Override
  public @NotNull Tooltip getTooltip() {
    return Tooltip.create(component);
  }

  @Override
  protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    int width = TextureSizeHelper.getWidth(TEXTURE), height = TextureSizeHelper.getHeight(TEXTURE);
    guiGraphics.blit(bottom ? TAB_BOTTOM : TAB, getX(), getY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
    guiGraphics.blit(TEXTURE, getX() + 10, getY() + 5, 0, 0, width, height, width, height);
  }

  @Override
  public void onClick(double mouseX, double mouseY, int button) {
    PacketDistributor.sendToServer(new CBreakStructurePacket(machine, controllerPos));
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

  }
}
