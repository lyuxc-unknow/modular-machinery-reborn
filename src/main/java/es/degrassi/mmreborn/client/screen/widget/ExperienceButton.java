package es.degrassi.mmreborn.client.screen.widget;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.stream.Stream;

public class ExperienceButton extends IconButton {

  private final ExperienceButtonType type;

  public interface OnPressT {
    void onPress(ExperienceButtonType type);
  }

  public ExperienceButton(int x, int y, OnPressT onPress, ExperienceButtonType type) {
    super(x, y, btn -> onPress.onPress(type));
    setTooltip(Tooltip.create(type.component()));
    setIcon(type.icon());
    this.type = type;
  }

  public List<Component> getTooltipMessage() {
    return Stream.of(type.component()).toList();
  }
}
