package es.degrassi.mmreborn.client.screen.widget.tabs;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.client.screen.ControllerScreen;
import es.degrassi.mmreborn.client.screen.popup.CoreGridPopupScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class CoreTabWidget extends TabWidget {
  private static final ResourceLocation TEXTURE = ModularMachineryReborn.rl("textures/gui/check.png");

  private final ControllerScreen parentScreen;

  public final Component component = Component.translatable("modular_machinery_reborn.gui.core_button");

  public CoreTabWidget(ControllerScreen parentScreen) {
    super(0, 0, TEXTURE);
    this.parentScreen = parentScreen;
  }

  @Override
  public void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
      guiGraphics.renderTooltip(
          Minecraft.getInstance().font,
          List.of(component.getVisualOrderText()),
          x,
          y
      );
  }

  @Override
  public void onClick(double mouseX, double mouseY, int button) {
    parentScreen.setFocused(this);
    parentScreen.getMenu().getEntity().setLastFocus(2);
    parentScreen.openPopup(new CoreGridPopupScreen(
        parentScreen,
        10 * 16 + 5*3 + 5*9,
        9 * 20
    ).addCloseButton(), "popup");
  }
}
