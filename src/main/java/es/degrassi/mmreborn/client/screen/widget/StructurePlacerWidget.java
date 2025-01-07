package es.degrassi.mmreborn.client.screen.widget;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.screen.ControllerScreen;
import es.degrassi.mmreborn.client.screen.popup.ConfirmationPopup;
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

import static es.degrassi.mmreborn.client.screen.BaseScreen.TAB;
import static es.degrassi.mmreborn.client.screen.BaseScreen.TAB_HOVERED;

public class StructurePlacerWidget extends AbstractWidget {
  private static final ResourceLocation TEXTURE = ModularMachineryReborn.rl("textures/gui/structure_placer.png");

  private final ControllerScreen parentScreen;

  private final ResourceLocation machine;
  private final BlockPos controllerPos;

  public final Component component = Component.translatable("modular_machinery_reborn.gui.structure_placer_button");

  public StructurePlacerWidget(ControllerScreen parentScreen, int x, int y, ResourceLocation machine, BlockPos controllerPos) {
    super(
        x,
        y - TextureSizeHelper.getHeight(TAB),
        TextureSizeHelper.getWidth(TAB),
        TextureSizeHelper.getHeight(TAB),
        Component.literal("structure placer")
    );
    this.parentScreen = parentScreen;
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
    int x = getX();
    int y = getY();
    int width = TextureSizeHelper.getWidth(tab), height = TextureSizeHelper.getHeight(tab);
    this.width = width;
    this.height = height;
    guiGraphics.blit(tab, x, y, 0, 0, width, height, width, height);
    width = TextureSizeHelper.getWidth(TEXTURE);
    height = TextureSizeHelper.getHeight(TEXTURE);
    guiGraphics.blit(TEXTURE, x + 5, y + 5, 0, 0, width, height, width, height);
  }

  @Override
  public void onClick(double mouseX, double mouseY, int button) {
    parentScreen.setFocused(this);
    parentScreen.getMenu().getEntity().setLastFocus(0);
    parentScreen.openPopup(new ConfirmationPopup<>(
        parentScreen,
        180,
        96,
        () -> parentScreen.openPopup(
            new ConfirmationPopup<>(
                parentScreen,
                180,
                96,
                () -> PacketDistributor.sendToServer(new CPlaceStructurePacket(machine, controllerPos, true))
            )
                .cancelCallback(() -> onClick(mouseX, mouseY, button))
                .text(Component.translatable("mmr.gui.structure.place.modifier.true"), Component.empty(), Component.empty())
        )
    )
        .confirmText(Component.translatable("mmr.gui.structure.place.confirm.modifier"))
        .cancelText(Component.translatable("mmr.gui.structure.place.cancel.modifier"))
        .text(Component.translatable("mmr.gui.structure.place.modifier"), Component.empty(), Component.empty())
        .cancelCallback(() -> parentScreen.openPopup(
                new ConfirmationPopup<>(
                    parentScreen,
                    180,
                    96,
                    () -> PacketDistributor.sendToServer(new CPlaceStructurePacket(machine, controllerPos, false))
                )
                    .cancelCallback(() -> onClick(mouseX, mouseY, button))
                    .text(Component.translatable("mmr.gui.structure.place.modifier.false"), Component.empty(), Component.empty())
            )
        ), "popup");
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

  }
}
