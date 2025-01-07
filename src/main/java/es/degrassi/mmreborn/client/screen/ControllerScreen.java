package es.degrassi.mmreborn.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.client.container.ControllerContainer;
import es.degrassi.mmreborn.client.item.MMRItemTooltipComponent;
import es.degrassi.mmreborn.client.screen.popup.BasePopupScreen;
import es.degrassi.mmreborn.client.screen.widget.StructureBreakWidget;
import es.degrassi.mmreborn.client.screen.widget.StructurePlacerWidget;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.util.CycleTimer;
import es.degrassi.mmreborn.common.util.TextureSizeHelper;
import es.degrassi.mmreborn.common.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ControllerScreen extends BasePopupScreen<ControllerContainer> {
  protected static final ResourceLocation BASE_SLOT = ModularMachineryReborn.rl("textures/gui/base_slot.png");
  protected static final ResourceLocation BASE_SLOT_HOVERED = ModularMachineryReborn.rl("textures/gui/base_slot_hovered.png");
  private final List<Either<FormattedText, TooltipComponent>> components;
  private static final int screenWidth = 158;
  private static final CycleTimer timer = new CycleTimer(() -> 1000, true);

  StructurePlacerWidget placeWidget;
  StructureBreakWidget breakWidget;

  public ControllerScreen(ControllerContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
    super(pMenu, pPlayerInventory, pTitle, TextureSizeHelper.getWidth(getTexture()), TextureSizeHelper.getHeight(getTexture()));
    List<Either<FormattedText, TooltipComponent>> components = new LinkedList<>();
    gatherComponents(components);
    this.components = components;
  }

  @Override
  protected void init() {
    super.init();
    createWidgets();
  }

  @Override
  public void removed() {
    super.removed();
    if (this.minecraft.player != null) {
      this.menu.removed(this.minecraft.player);
    }
  }

  private void createWidgets() {
    placeWidget = addRenderableWidget(new StructurePlacerWidget(
        this,
        x,
        y,
        getMenu().getEntity().getId(),
        getMenu().getEntity().getBlockPos()));
    breakWidget = addRenderableWidget(new StructureBreakWidget(
        this,
        x + placeWidget.getWidth() - 1,
        y,
        getMenu().getEntity().getId(),
        getMenu().getEntity().getBlockPos())
    );
    placeWidget.setTooltip(null);
    breakWidget.setTooltip(null);
    setInitialFocus(getMenu().getEntity().getLastFocus() == 0 ? placeWidget : breakWidget);
  }

  public static ResourceLocation getTexture() {
    return ModularMachineryReborn.rl("textures/gui/guicontroller.png");
  }

  public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    int i = this.x;
    int j = this.y;

    // render image background
    this.renderTransparentBackground(guiGraphics);
    renderBg(guiGraphics, partialTicks, mouseX, mouseY);

    for (Renderable renderable : this.renderables) {
      renderable.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
    // Neo: replicate the super method's implementation to insert the event between background and widgets
    RenderSystem.disableDepthTest();
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate((float)i, (float)j, 0.0F);
    this.hoveredSlot = null;

    for (int k = 0; k < this.menu.slots.size(); k++) {
      Slot slot = this.menu.slots.get(k);
      if (slot.isActive()) {
        this.renderSlot(guiGraphics, slot);
        if (this.isHovering(slot, mouseX, mouseY)) {
          this.hoveredSlot = slot;
          this.renderSlotHighlight(guiGraphics, slot, mouseX, mouseY, partialTicks);
        }
      }
    }

    this.renderLabels(guiGraphics, mouseX, mouseY);
    ItemStack itemstack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
    if (!itemstack.isEmpty()) {
      int l1 = 8;
      int i2 = this.draggingItem.isEmpty() ? 8 : 16;
      String s = null;
      if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
        itemstack = itemstack.copyWithCount(Mth.ceil((float)itemstack.getCount() / 2.0F));
      } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
        itemstack = itemstack.copyWithCount(this.quickCraftingRemainder);
        if (itemstack.isEmpty()) {
          s = ChatFormatting.YELLOW + "0";
        }
      }

      this.renderFloatingItem(guiGraphics, itemstack, mouseX - i - 8, mouseY - j - i2, s);
    }

    if (!this.snapbackItem.isEmpty()) {
      float f = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
      if (f >= 1.0F) {
        f = 1.0F;
        this.snapbackItem = ItemStack.EMPTY;
      }

      int j2 = this.snapbackEnd.x - this.snapbackStartX;
      int k2 = this.snapbackEnd.y - this.snapbackStartY;
      int j1 = this.snapbackStartX + (int)((float)j2 * f);
      int k1 = this.snapbackStartY + (int)((float)k2 * f);
      this.renderFloatingItem(guiGraphics, this.snapbackItem, j1, k1, null);
    }

    guiGraphics.pose().popPose();

    RenderSystem.enableDepthTest();
    List<Either<FormattedText, TooltipComponent>> components = new LinkedList<>();
    gatherComponents(components);
    this.components.clear();
    components.addFirst(Either.left(placeWidget.component));
    this.components.addAll(components);

    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(this.x, this.y, 0);
    float scale = 0.72f;
    guiGraphics.pose().scale(scale, scale, scale);
    int offsetX = 14;
    int offsetY = 14;

    timer.onDraw();

    DynamicMachine machine = getMenu().getEntity().getFoundMachine();
    if (machine != DynamicMachine.DUMMY) {
      // render if the structure of machine is not null
      List<FormattedCharSequence> out = font.split(Component.literal(machine.getLocalizedName()), Mth.floor(screenWidth * (1 / scale)));
      offsetY -= 7;
      for (FormattedCharSequence draw : out) {
        offsetY += 7;
        guiGraphics.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
        offsetY += 7;
      }
      offsetY -= 7;
    } else {
      // render if the structure of machine is null
      Component drawnHead = Component.translatable("gui.controller.structure", Component.translatable("gui.controller.structure.none"));
      guiGraphics.drawString(font, drawnHead, offsetX, offsetY, 0xFFFFFF);
    }
    offsetY += 10;

    if (getMenu().getEntity().isPaused()) {
      // render if redstone paused the machine
      Component drawnStop = Component.translatable("gui.controller.status.redstone_stopped");
      List<FormattedCharSequence> out = font.split(drawnStop, Mth.floor(screenWidth * (1 / scale)));
      for (FormattedCharSequence draw : out) {
        offsetY += 7;
        guiGraphics.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
        offsetY += 7;
      }
      guiGraphics.pose().popPose();
      for (Renderable renderable : this.renderables) {
        renderable.render(guiGraphics, mouseX, mouseY, partialTicks);
      }
      return;
    }

    // render the current status
    MutableComponent status = Component.translatable("gui.controller.status");
    List<FormattedCharSequence> out = font.split(status.append(getMenu().getEntity().getCraftingStatus().getUnlocMessage()), Mth.floor(screenWidth * (1 / scale)));
    for (FormattedCharSequence draw : out) {
      offsetY += 7;
      guiGraphics.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
      offsetY += 7;
    }
    offsetY -= 7;
    offsetY += 10;
    if (getMenu().getEntity().hasActiveRecipe()) {
      // render if the recipe of machine is not null
      String percProgress = Utils.decimalFormatWithPercentage(Mth.clamp(getMenu().getEntity().getCurrentActiveRecipeProgress() * 100F, 0, 100));
      Component progressStr = Component.translatable("gui.controller.status.crafting.progress", percProgress);
      offsetY += 7;
      guiGraphics.drawString(font, progressStr, offsetX, offsetY, 0xFFFFFF);
    }
    guiGraphics.pose().popPose();

    renderTooltip(guiGraphics, mouseX, mouseY);
  }

  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    guiGraphics.pose().pushPose();
    guiGraphics.setColor(1f, 1f, 1f, 1f);
    int leftPos = (this.width - this.xSize) / 2;
    int topPos = (this.height - this.ySize) / 2;
    guiGraphics.blit(getTexture(), leftPos, topPos, 0, 0, xSize, ySize, xSize, ySize);
    guiGraphics.pose().popPose();
  }

  protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
  }

  protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
      ItemStack itemstack = this.hoveredSlot.getItem();
      guiGraphics.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, x, y);
    }
    if (placeWidget != null && placeWidget.isMouseOver(x, y)) {
      guiGraphics.renderComponentTooltipFromElements(
          Minecraft.getInstance().font,
          components,
          x,
          y,
          ItemStack.EMPTY
      );
    }
    if (breakWidget != null && breakWidget.isMouseOver(x, y)) {
      guiGraphics.renderTooltip(
          Minecraft.getInstance().font,
          List.of(breakWidget.component.getVisualOrderText()),
          x,
          y
      );
    }
  }

  private void gatherComponents(List<Either<FormattedText, TooltipComponent>> components) {
    Optional.of(getMenu().getEntity().getFoundMachine())
        .ifPresentOrElse(machine -> {
          if (Screen.hasShiftDown()) {
            components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required").withStyle(ChatFormatting.GRAY)));
            Map<MutableComponent, List<ItemStack>> map = new LinkedHashMap<>();
            machine.getPattern()
                .getPattern()
                .asList()
                .stream()
                .flatMap(List::stream)
                .flatMap(s -> s.chars().mapToObj(c -> (char) c))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> {
                  BlockIngredient ingredient = machine.getPattern().getPattern().asMap().get(entry.getKey());
                  if (ingredient == null) return null;
                  return Pair.of(ingredient.getStacks(entry.getValue().intValue()), ingredient.getNamesUnified());
                })
                .filter(Objects::nonNull)
                .forEachOrdered(pair -> {
                  List<ItemStack> stacks = pair.getFirst();
                  MutableComponent component = pair.getSecond();
                  List<ItemStack> s2 = new LinkedList<>();
                  if (map.containsKey(component)) {
                    stacks.forEach(stack -> map.forEach((c, s) -> {
                      if (c.getString().equals(component.getString())) {
                        s.forEach(s1 -> {
                          if (s1.is(stack.getItem())) {
                            s1.grow(stack.getCount());
                          }
                          s2.add(s1);
                        });
                      }
                    }));
                  } else {
                    s2.addAll(stacks);
                  }

                  map.put(component, s2);
                });
            map.forEach((c, stacks) -> {
              if (c != null && !stacks.isEmpty()) {
                MMRItemTooltipComponent component = new MMRItemTooltipComponent(stacks, timer);
                component.setComponent(
                    Component.translatable(
                        "modular_machinery_reborn.controller.required.block",
                        c
                    ).withStyle(ChatFormatting.GRAY)
                );
                components.add(Either.right(component));
              }
            });
          } else {
            components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required.block.key",
                Component.translatable("modular_machinery_reborn.controller.required.shift").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY)));
          }

          if (Screen.hasControlDown() && !Screen.hasShiftDown()) {
            components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.modifier").withStyle(ChatFormatting.GRAY)));
            Map<MutableComponent, List<ItemStack>> modifierMap = new LinkedHashMap<>();
            machine.getPattern()
                .getPattern()
                .getModifiers()
                .forEach(ingredient -> {
                  MutableComponent component = Component.empty();
                  ingredient.getDescriptionLines().forEach(component::append);
                  modifierMap.put(component, ingredient.getIngredient().getStacks(1));
                });

            if (!modifierMap.isEmpty()) {
              modifierMap.forEach((c, stacks) -> {
                if (c != null && !stacks.isEmpty()) {
                  MMRItemTooltipComponent component = new MMRItemTooltipComponent(stacks, timer);
                  component.setComponent(
                      Component.translatable(
                          "modular_machinery_reborn.controller.required.block",
                          c
                      ).withStyle(ChatFormatting.GRAY)
                  );
                  components.add(Either.right(component));
                }
              });
            }
          } else {
            components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required.block.key",
                Component.translatable("modular_machinery_reborn.controller.required.control").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY)));
          }
        },
        () -> components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.no_machine").withStyle(ChatFormatting.GRAY))));
  }

  protected void renderSlotHighlight(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, float partialTick) {
    if (slot.isHighlightable()) {
      renderSlotHighlight(guiGraphics, slot.x, slot.y, getSlotColor(slot.index));
    }
  }

  public static void renderSlotHighlight(GuiGraphics guiGraphics, int x, int y, int color) {
    guiGraphics.pose().pushPose();
    int width = TextureSizeHelper.getWidth(BASE_SLOT_HOVERED), height = TextureSizeHelper.getHeight(BASE_SLOT_HOVERED);
    guiGraphics.blit(BASE_SLOT_HOVERED, x - 1, y - 1, 0, 0, width, height, width, height);
    guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, color, color, 0);
    guiGraphics.pose().popPose();
  }
}
