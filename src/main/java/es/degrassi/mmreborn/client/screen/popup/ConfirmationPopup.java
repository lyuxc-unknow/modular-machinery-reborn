package es.degrassi.mmreborn.client.screen.popup;

import com.mojang.blaze3d.systems.RenderSystem;
import es.degrassi.mmreborn.ModularMachineryReborn;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
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

  private boolean closeButton = false;

  public ConfirmationPopup(BasePopupScreen<T> parent, int xSize, int ySize,
                           Runnable onConfirm) {
    super(parent, xSize, ySize);
    this.onConfirm = onConfirm;
    this.onCancel = null;
  }

  public ConfirmationPopup<T> addCloseButton() {
    this.closeButton = true;
    return this;
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
    Font font = Minecraft.getInstance().font;
    GridLayout layout = new GridLayout(this.x, this.y).columnSpacing(10);
    GridLayout.RowHelper row = layout.createRowHelper(2);
    row.defaultCellSetting().padding(5).alignHorizontallyCenter();

    if (closeButton)
      row.addChild(
          Button
              .builder(Component.literal("X"), b -> this.parent.closePopup(this))
              .bounds(
                  0,
                  0,
                  20,
                  20
              )
              .build(builder -> new CustomButton(builder, true)),
          2,
          row.newCellSettings().alignHorizontallyRight()
      );

    if (this.title != null)
      row.addChild(new StringWidget(this.xSize, 10, title, font).alignCenter(), 2);

    if (!this.text.isEmpty()) {
      MutableComponent text = Component.empty();
      for (Component component : this.text)
        text.append("\n").append(component);
      MultiLineTextWidget textWidget = new MultiLineTextWidget(text, font).setCentered(true).setMaxWidth(this.xSize - 10);
      row.addChild(textWidget, 2, row.newCellSettings().alignHorizontallyLeft());
    }

    row.addChild(Button.builder(CONFIRM, b -> this.confirm()).bounds(0, 0, 75, 20).build(builder -> new CustomButton(builder, true)),
        row.newCellSettings().alignHorizontallyLeft());
    row.addChild(Button.builder(CANCEL, b -> this.cancel()).bounds(0, 0, 75, 20).build(builder -> new CustomButton(builder, true)),
        row.newCellSettings().alignHorizontallyLeft());
    layout.arrangeElements();
    layout.visitWidgets(this::addRenderableWidget);
    this.ySize = layout.getHeight() + 5;
  }

  @ParametersAreNonnullByDefault
  private static class CustomButton extends Button {
    protected static final WidgetSprites SPRITES = new WidgetSprites(
        ModularMachineryReborn.rl("widget/button/button"),
        ModularMachineryReborn.rl("widget/button/button_disabled"),
        ModularMachineryReborn.rl("widget/button/button_highlighted")
    );

    private final boolean renderString;

    protected CustomButton(Builder builder, boolean renderString) {
      super(builder);
      this.renderString = renderString;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      Minecraft minecraft = Minecraft.getInstance();
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      RenderSystem.enableDepthTest();
      guiGraphics.blitSprite(
          SPRITES.get(this.active, this.isHoveredOrFocused()),
          this.getX(), this.getY(),
          this.getWidth(), this.getHeight());
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      int i = getFGColor();
      if (renderString)
        this.renderString(guiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, int width, int color) {
      int i = this.getX() + width;
      int j = this.getX() + this.getWidth() - width;
      renderScrollingString(guiGraphics, font, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight() - 3, color);
    }
  }
}
