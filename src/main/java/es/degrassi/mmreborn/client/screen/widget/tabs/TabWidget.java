package es.degrassi.mmreborn.client.screen.widget.tabs;

import com.mojang.datafixers.util.Either;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.util.TextureSizeHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class TabWidget extends AbstractWidget {
  private static final ResourceLocation TAB = ModularMachineryReborn.rl("textures/gui/widget/base_tab.png");
  private static final ResourceLocation TAB_HOVERED = ModularMachineryReborn.rl("textures/gui/widget/base_tab_hovered.png");

  private final ResourceLocation icon;
  @Nullable
  private final OnClick onClick;

  public TabWidget(int x, int y, @Nullable ResourceLocation icon, @Nullable OnClick onClick) {
    super(x, y - TextureSizeHelper.getHeight(TAB), TextureSizeHelper.getWidth(TAB), TextureSizeHelper.getHeight(TAB), Component.empty());
    this.icon = icon;
    this.onClick = onClick;
  }

  public TabWidget(int x, int y, @Nullable ResourceLocation icon) {
    this(x, y, icon, null);
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
    if (icon != null) {
      width = TextureSizeHelper.getWidth(icon);
      height = TextureSizeHelper.getHeight(icon);
      guiGraphics.blit(icon, x + 5, y + 5, 0, 0, width, height, width, height);
    }
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
  }

  public void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
  }

  @Override
  public void onClick(double mouseX, double mouseY, int button) {
    if (onClick != null) {
      onClick.onClick(mouseX, mouseY, button);
    }
  }

  public void gatherComponents(List<Either<FormattedText, TooltipComponent>> components) {
  }

  public interface OnClick {
    void onClick(double mouseX, double mouseY, int button);
  }
}
