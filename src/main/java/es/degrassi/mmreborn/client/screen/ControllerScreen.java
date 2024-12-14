package es.degrassi.mmreborn.client.screen;

import com.mojang.datafixers.util.Either;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.client.container.ControllerContainer;
import es.degrassi.mmreborn.client.item.MMRItemTooltipComponent;
import es.degrassi.mmreborn.client.screen.widget.StructurePlacerWidget;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.util.RedstoneHelper;
import es.degrassi.mmreborn.common.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ControllerScreen extends BaseScreen<ControllerContainer, MachineControllerEntity> {
  StructurePlacerWidget widget;
  private final List<Either<FormattedText, TooltipComponent>> components;
  private boolean addedWidget = false;

  public ControllerScreen(ControllerContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
    super(pMenu, pPlayerInventory, pTitle);
    List<Either<FormattedText, TooltipComponent>> components = new LinkedList<>();
    gatherComponents(components);
    this.components = components;
  }

  @Override
  public ResourceLocation getTexture() {
    return ModularMachineryReborn.rl("textures/gui/guicontroller.png");
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    // render image background
    super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
    clearWidgets();
    widget = addRenderableWidget(new StructurePlacerWidget(leftPos + imageWidth - 5, topPos,
        getMenu().getEntity().getId(), getMenu().getEntity().getBlockPos()));
    widget.setTooltip(widget.getTooltip());
    if (!addedWidget) {
      components.addFirst(Either.left(widget.component));
      addedWidget = true;
    }

    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(this.leftPos, this.topPos, 0);
    float scale = 0.72f;
    guiGraphics.pose().scale(scale, scale, scale);
    int offsetX = 12;
    int offsetY = 12;

    int redstone = RedstoneHelper.getRedstoneLevel(entity);
    if (redstone > 0) {
      // render if redstone paused the machine
      Component drawnStop = Component.translatable("gui.controller.status.redstone_stopped");
      List<FormattedCharSequence> out = font.split(drawnStop, Mth.floor(135 * (1 / scale)));
      for (FormattedCharSequence draw : out) {
        offsetY += 10;
        guiGraphics.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
        offsetY += 10;
      }
      guiGraphics.pose().popPose();
      return;
    }

    DynamicMachine machine = entity.getFoundMachine();
    if (machine != DynamicMachine.DUMMY) {
      // render if the structure of machine is not null
      List<FormattedCharSequence> out = font.split(Component.literal(machine.getLocalizedName()), Mth.floor(135 * (1 / scale)));
      for (FormattedCharSequence draw : out) {
        guiGraphics.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
        offsetY += 10;
      }
      offsetY -= 10;
    } else {
      // render if the structure of machine is null
      Component drawnHead = Component.translatable("gui.controller.structure", Component.translatable("gui.controller.structure.none"));
      guiGraphics.drawString(font, drawnHead, offsetX, offsetY, 0xFFFFFF);
    }
    offsetY += 15;

    // render the current status
    Component status = Component.translatable("gui.controller.status");
    guiGraphics.drawString(font, status, offsetX, offsetY, 0xFFFFFF);
    String statusKey = entity.getCraftingStatus().getUnlocMessage();

    List<FormattedCharSequence> out = font.split(Component.translatable(statusKey), Mth.floor(135 * (1 / scale)));
    for (FormattedCharSequence draw : out) {
      offsetY += 10;
      guiGraphics.drawString(font, draw, offsetX, offsetY, 0xFFFFFF);
    }
    offsetY += 15;
    if (entity.hasActiveRecipe()) {
      // render if the recipe of machine is not null
      String percProgress = Utils.decimalFormatWithPercentage(Mth.clamp(entity.getCurrentActiveRecipeProgress() * 100F, 0, 100));
      Component progressStr = Component.translatable("gui.controller.status.crafting.progress", percProgress);
      guiGraphics.drawString(font, progressStr, offsetX, offsetY, 0xFFFFFF);
    }

    guiGraphics.pose().popPose();
  }

  @Override
  protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
  }

  @Override
  protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    super.renderTooltip(guiGraphics, x, y);
    if (widget != null && widget.isMouseOver(x, y)) {
      guiGraphics.renderComponentTooltipFromElements(
          Minecraft.getInstance().font,
          components,
          x,
          y,
          ItemStack.EMPTY
      );
    }
  }

  private void gatherComponents(List<Either<FormattedText, TooltipComponent>> components) {
    Optional.ofNullable(entity.getFoundMachine()).ifPresentOrElse(machine -> {
          components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required").withStyle(ChatFormatting.GRAY)));
          machine.getPattern()
              .getPattern()
              .asList()
              .stream()
              .flatMap(List::stream)
              .flatMap(s -> s.chars().mapToObj(c -> (char) c))
              .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
              .forEach((key, amount) -> {
                BlockIngredient ingredient = machine.getPattern().getPattern().asMap().get(key);
                if (ingredient != null && amount > 0) {
                  List<ItemStack> stacks = ingredient.getStacks(Math.toIntExact(amount));
                  MMRItemTooltipComponent component = new MMRItemTooltipComponent(stacks);
                  String value = ingredient.getString();
                  if (value.startsWith("[") && value.endsWith("]") && ((ingredient.isTag() && ingredient.getTags().size() == 1) || ingredient.getAll().size() == 1))
                    value = value.substring(1, value.length() - 1);
                  component.setComponent(
                      Component.translatable(
                          "modular_machinery_reborn.controller.required.block",
                          value
                      ).withStyle(ChatFormatting.GRAY)
                  );
                  components.add(Either.right(component));
                }
              });
        },
        () -> components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.no_machine").withStyle(ChatFormatting.GRAY))));
  }
}
