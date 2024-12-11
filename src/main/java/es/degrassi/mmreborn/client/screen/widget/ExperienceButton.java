package es.degrassi.mmreborn.client.screen.widget;

import es.degrassi.mmreborn.common.util.TextureSizeHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.stream.Stream;

public class ExperienceButton extends Button {

  private final ExperienceButtonType type;

  public interface OnPressT {
    void onPress(ExperienceButtonType type);
  }

  public ExperienceButton(int x, int y, OnPressT onPress, ExperienceButtonType type) {
    super(
        x,
        y,
        TextureSizeHelper.getWidth(type.base()),
        TextureSizeHelper.getHeight(type.base()),
        Component.literal(type.getSerializedName()),
        btn -> onPress.onPress(type),
        DEFAULT_NARRATION
    );
    setTooltip(Tooltip.create(type.component()));
    this.type = type;
  }

  @Override
  protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    ResourceLocation texture = !isActive() ? type.disabled() : (isHoveredOrFocused() ? type.hovered() : type.base());
    int width = TextureSizeHelper.getWidth(texture);
    int height = TextureSizeHelper.getHeight(texture);
    guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0, width, height, width, height);
  }

  public List<FormattedCharSequence> getTooltipMessage() {
    return Stream.of(type.component())
        .map(Component::getVisualOrderText)
        .toList();
  }
}
