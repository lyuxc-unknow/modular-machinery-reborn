package es.degrassi.mmreborn.client.screen.widget;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.network.client.CPlaceStructurePacket;
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
import org.jetbrains.annotations.Nullable;

public class StructurePlacerWidget extends AbstractWidget {
  private static final ResourceLocation TEXTURE = ModularMachineryReborn.rl("textures/gui/structure_placer.png");

  private final ResourceLocation machine;
  private final BlockPos controllerPos;

  public final Component component = Component.translatable("modular_machinery_reborn.gui.structure_placer_button");

  public StructurePlacerWidget(int x, int y, ResourceLocation machine, BlockPos controllerPos) {
    super(x, y, TextureSizeHelper.getWidth(TEXTURE), TextureSizeHelper.getHeight(TEXTURE), Component.literal("structure placer"));
    this.machine = machine;
    this.controllerPos = controllerPos;
  }

  @Override
  public @NotNull Tooltip getTooltip() {
    return Tooltip.create(component);
  }

  @Override
  protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    guiGraphics.blit(TEXTURE, getX(), getY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
  }

  @Override
  public void onClick(double mouseX, double mouseY, int button) {
    PacketDistributor.sendToServer(new CPlaceStructurePacket(machine, controllerPos));
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

  }
}
