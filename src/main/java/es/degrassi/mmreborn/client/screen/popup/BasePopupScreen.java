package es.degrassi.mmreborn.client.screen.popup;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.util.LRU;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class BasePopupScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
  protected static final ResourceLocation BLANK_BACKGROUND = ModularMachineryReborn.rl("background");

  public final Minecraft mc = Minecraft.getInstance();

  //Position of the top left corner of the popup.
  public int x;
  public int y;

  //Size of the screen, not same as width/height which is the size of MC windows.
  public int xSize;
  public int ySize;
  private final LRU<PopupScreen<?>> popups = new LRU<>();
  private final Map<PopupScreen<?>, String> popupToId = new HashMap<>();

  private int freezePopupsTicks;

  @Getter
  private final Inventory playerInventory;

  public BasePopupScreen(T pMenu, Inventory pPlayerInventory, Component component, int xSize, int ySize) {
    super(pMenu, pPlayerInventory, component);
    this.xSize = xSize;
    this.ySize = ySize;
    this.playerInventory = pPlayerInventory;
  }

  public void openPopup(PopupScreen<?> popup) {
    if (this.popups.contains(popup))
      return;
    this.setFocused(null);
    this.popups.add(popup);
    popup.init(Minecraft.getInstance(), this.width, this.height);
  }

  //Prevents opening another popup with same id
  public void openPopup(PopupScreen<?> popup, String id) {
    if (this.popupToId.containsValue(id))
      return;
    this.popupToId.put(popup, id);
    this.openPopup(popup);
    this.freezePopupsTicks = 40;
  }

  public void closePopup(PopupScreen<?> popup) {
    popup.removed();
    this.popups.remove(popup);
    this.popupToId.remove(popup);
  }

  public Collection<PopupScreen<?>> popups() {
    return this.popups;
  }

  @Nullable
  public PopupScreen<?> getPopupUnderMouse(double mouseX, double mouseY) {
    return this.popups.stream()
        .filter(popup -> mouseX >= popup.x && mouseX <= popup.x + popup.xSize && mouseY >= popup.y && mouseY <= popup.y + popup.ySize)
        .findFirst()
        .orElse(null);
  }

  @Override
  public void removed() {
    this.popups.forEach(PopupScreen::removed);
  }

  @Override
  protected void init() {
    super.init();
    this.x = (this.width - this.xSize) / 2;
    this.y = (this.height - this.ySize) / 2;
    this.popups.forEach(popup -> popup.init(Minecraft.getInstance(), this.width, this.height));
  }

  @Override
  public void containerTick() {
    this.popups.forEach(PopupScreen::containerTick);
    if (this.freezePopupsTicks > 0)
      this.freezePopupsTicks--;
  }

  @Override
  public void resize(Minecraft minecraft, int width, int height) {
    this.x = (width - this.xSize) / 2;
    this.y = (height - this.ySize) / 2;
    super.resize(minecraft, width, height);
  }

  public abstract void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    PopupScreen<?> hoveredPopup = this.getPopupUnderMouse(mouseX, mouseY);

    graphics.pose().pushPose();

    if (hoveredPopup != null)
      this.renderBackground(graphics, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTicks);
    else
      this.renderBackground(graphics, mouseX, mouseY, partialTicks);

    for (Iterator<PopupScreen<?>> iterator = this.popups.descendingIterator(); iterator.hasNext(); ) {
      graphics.pose().translate(0, 0, 305); //Items are rendered at z=232, item text count at 300, tooltips z=400
      PopupScreen<?> popup = iterator.next();
      if (hoveredPopup == popup)
        popup.renderWithTooltip(graphics, mouseX, mouseY, partialTicks);
      else
        popup.render(graphics, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTicks);
    }

    graphics.pose().popPose();
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    for (PopupScreen<?> popup : this.popups) {
      if (popup.isMouseOver(mouseX, mouseY)) {
        boolean clicked = popup.mouseClicked(mouseX, mouseY, button);
        if (this.freezePopupsTicks <= 0)
          this.popups.moveUp(popup);
        return clicked;
      }
    }
    for (GuiEventListener guieventlistener : this.children()) {
      if (guieventlistener.mouseClicked(mouseX, mouseY, button)) {
        this.setFocused(guieventlistener);
        if (button == 0) {
          this.setDragging(true);
        }

        return true;
      }
    }

    return false;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    for (PopupScreen<?> popup : this.popups) {
      if (popup.isMouseOver(mouseX, mouseY)) {
        boolean released = popup.mouseReleased(mouseX, mouseY, button);
        if (this.freezePopupsTicks <= 0)
          this.popups.moveUp(popup);
        return released;
      }
    }
    this.setDragging(false);
    if (this.getFocused() != null && this.getFocused().mouseReleased(mouseX, mouseY, button))
      return true;
    return this.getChildAt(mouseX, mouseY).filter(guiEventListener -> guiEventListener.mouseReleased(mouseX, mouseY, button)).isPresent();
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    for (PopupScreen<?> popup : this.popups) {
      boolean dragged = popup.mouseDragged(mouseX, mouseY, button, dragX, dragY);
      if (this.freezePopupsTicks <= 0)
        this.popups.moveUp(popup);
      return dragged;
    }
    return this.getFocused() != null && this.isDragging() && button == 0 && this.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    for (PopupScreen<?> popup : this.popups) {
      if (popup.isMouseOver(mouseX, mouseY)) {
        boolean scrolled = popup.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (this.freezePopupsTicks <= 0)
          this.popups.moveUp(popup);
        return scrolled;
      }
    }
    return this.getChildAt(mouseX, mouseY).filter(p_293596_ -> p_293596_.mouseScrolled(mouseX, mouseY, scrollX, scrollY)).isPresent();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
      if (!this.popups.isEmpty()) {
        PopupScreen<?> toClose = this.getPopupUnderMouse(Minecraft.getInstance().mouseHandler.xpos(),
            Minecraft.getInstance().mouseHandler.ypos());
        if (toClose == null)
          toClose = this.popups.iterator().next();
        this.closePopup(toClose);
        return true;
      }
      if (this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers))
        return true;
      this.onClose();
      return true;
    }

    for (PopupScreen<?> popup : this.popups) {
      if (popup.keyPressed(keyCode, scanCode, modifiers))
        return true;
    }

    if (this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers))
      return true;

    FocusNavigationEvent event = switch (keyCode) {
      case GLFW.GLFW_KEY_LEFT -> new FocusNavigationEvent.ArrowNavigation(ScreenDirection.LEFT);
      case GLFW.GLFW_KEY_RIGHT -> new FocusNavigationEvent.ArrowNavigation(ScreenDirection.RIGHT);
      case GLFW.GLFW_KEY_UP -> new FocusNavigationEvent.ArrowNavigation(ScreenDirection.UP);
      case GLFW.GLFW_KEY_DOWN -> new FocusNavigationEvent.ArrowNavigation(ScreenDirection.DOWN);
      case GLFW.GLFW_KEY_TAB -> new FocusNavigationEvent.TabNavigation(!Screen.hasShiftDown());
      default -> null;
    };

    if (event != null) {
      ComponentPath path = this.popups.stream().findFirst().map(popup -> popup.nextFocusPath(event)).orElse(this.nextFocusPath(event));
      if (path == null && event instanceof FocusNavigationEvent.TabNavigation) {
        ComponentPath componentPath = this.getCurrentFocusPath();
        if (componentPath != null)
          componentPath.applyFocus(false);
        path = super.nextFocusPath(event);
      }

      if (path != null)
        this.changeFocus(path);

      return true;
    }

    if (keyCode == 256 && this.shouldCloseOnEsc()) {
      this.onClose();
      return true;
    } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
      return true;
    } else {
      FocusNavigationEvent focusnavigationevent = switch (keyCode) {
        case 258 -> this.createTabEvent();
        default -> null;
        case 262 -> this.createArrowEvent(ScreenDirection.RIGHT);
        case 263 -> this.createArrowEvent(ScreenDirection.LEFT);
        case 264 -> this.createArrowEvent(ScreenDirection.DOWN);
        case 265 -> this.createArrowEvent(ScreenDirection.UP);
      };
      if (focusnavigationevent != null) {
        ComponentPath componentpath = super.nextFocusPath(focusnavigationevent);
        if (componentpath == null && focusnavigationevent instanceof FocusNavigationEvent.TabNavigation) {
          this.clearFocus();
          componentpath = super.nextFocusPath(focusnavigationevent);
        }

        if (componentpath != null) {
          this.changeFocus(componentpath);
        }
      }

      return false;
    }
  }

  private FocusNavigationEvent.TabNavigation createTabEvent() {
    boolean flag = !hasShiftDown();
    return new FocusNavigationEvent.TabNavigation(flag);
  }

  private FocusNavigationEvent.ArrowNavigation createArrowEvent(ScreenDirection direction) {
    return new FocusNavigationEvent.ArrowNavigation(direction);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    for (PopupScreen<?> popup : this.popups) {
      if (popup.keyReleased(keyCode, scanCode, modifiers))
        return true;
    }
    return this.getFocused() != null && this.getFocused().keyReleased(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {
    for (PopupScreen<?> popup : this.popups) {
      if (popup.charTyped(codePoint, modifiers))
        return true;
    }
    return this.getFocused() != null && this.getFocused().charTyped(codePoint, modifiers);
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    if (this.getPopupUnderMouse(mouseX, mouseY) != null)
      return false;
    return mouseX >= this.x && mouseX <= this.x + this.xSize && mouseY >= this.y && mouseY <= this.y + this.ySize;
  }

  @Override
  public void setFocused(@Nullable GuiEventListener focused) {
    if (this.getFocused() != focused)
      super.setFocused(focused);
    if (focused != null)
      this.popups.forEach(popup -> popup.setFocused(null));
  }

  public static void blankBackground(GuiGraphics graphics, int x, int y, int width, int height) {
    graphics.blitSprite(BLANK_BACKGROUND, x, y, width, height);
  }

  public static void drawCenteredScaledString(GuiGraphics graphics, Font font, Component text, int x, int y, float scale, int color, boolean shadow) {
    graphics.pose().pushPose();
    graphics.pose().scale(scale, scale, 0);
    graphics.drawString(font, text, (int) ((x - (font.width(text) * scale) / 2) / scale), (int) ((y - (float) font.lineHeight / 2) / scale), color, shadow);
    graphics.pose().popPose();
  }

  public static void drawScaledString(GuiGraphics graphics, Font font, Component text, int x, int y, float scale, int color, boolean shadow) {
    graphics.pose().pushPose();
    graphics.pose().scale(scale, scale, 0);
    graphics.drawString(font, text, (int) (x / scale), (int) (y / scale), color, shadow);
    graphics.pose().popPose();
  }
}
