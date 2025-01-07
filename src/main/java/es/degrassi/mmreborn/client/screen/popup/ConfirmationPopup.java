package es.degrassi.mmreborn.client.screen.popup;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfirmationPopup<T extends AbstractContainerMenu> extends PopupScreen<T> {

  public Component CONFIRM = Component.translatable("mmr.gui.popup.confirm").withStyle(ChatFormatting.GREEN);
  public Component CANCEL = Component.translatable("mmr.gui.popup.cancel").withStyle(ChatFormatting.RED);

  private final Runnable onConfirm;
  private final List<Component> text = new ArrayList<>();
  private Component title;
  @Nullable
  private Runnable onCancel;

  public ConfirmationPopup(BasePopupScreen<T> parent, int xSize, int ySize,
                           Runnable onConfirm) {
    super(parent, xSize, ySize);
    this.onConfirm = onConfirm;
    this.onCancel = null;
  }

  public ConfirmationPopup<T> confirmText(Component confirmText) {
    CONFIRM = confirmText;
    return this;
  }

  public ConfirmationPopup<T> cancelText(Component cancelText) {
    CANCEL = cancelText;
    return this;
  }

  public ConfirmationPopup<T> cancelCallback(Runnable callback) {
    this.onCancel = callback;
    return this;
  }

  public ConfirmationPopup<T> title(Component title) {
    this.title = title;
    return this;
  }

  public ConfirmationPopup<T> text(Component... components) {
    this.text.addAll(Arrays.asList(components));
    return this;
  }

  public void confirm() {
    this.onConfirm.run();
    this.parent.closePopup(this);
  }

  public void cancel() {
    if (this.onCancel != null)
      this.onCancel.run();
    this.parent.closePopup(this);
  }

  @Override
  protected void init() {
    super.init();
    // TODO: add close button
    GridLayout layout = new GridLayout(this.x, this.y).columnSpacing(10);
    GridLayout.RowHelper row = layout.createRowHelper(2);
    row.defaultCellSetting().padding(5).alignHorizontallyCenter();

    if (this.title != null)
      row.addChild(new StringWidget(this.xSize, 10, this.title, Minecraft.getInstance().font).alignCenter(), 2);

    if (!this.text.isEmpty()) {
      MutableComponent text = Component.empty();
      for (Component component : this.text)
        text.append("\n").append(component);
      MultiLineTextWidget textWidget = new MultiLineTextWidget(text, Minecraft.getInstance().font).setCentered(true).setMaxWidth(this.xSize - 10);
      this.ySize = textWidget.getHeight() + 50;
      row.addChild(textWidget, 2, row.newCellSettings().alignHorizontallyCenter());
    }

    row.addChild(Button.builder(CONFIRM, b -> this.confirm()).bounds(0, 0, 75, 20).build(),
        row.newCellSettings().alignHorizontallyCenter());
    row.addChild(Button.builder(CANCEL, b -> this.cancel()).bounds(0, 0, 75, 20).build(),
        row.newCellSettings().alignHorizontallyCenter());
    layout.arrangeElements();
    layout.visitWidgets(this::addRenderableWidget);
  }
}
