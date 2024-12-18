package es.degrassi.mmreborn.client.screen;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.client.container.ControllerContainer;
import es.degrassi.mmreborn.client.item.MMRItemTooltipComponent;
import es.degrassi.mmreborn.client.screen.widget.StructureBreakWidget;
import es.degrassi.mmreborn.client.screen.widget.StructurePlacerWidget;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import es.degrassi.mmreborn.common.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
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

public class ControllerScreen extends BaseScreen<ControllerContainer, MachineControllerEntity> {
  StructurePlacerWidget placeWidget;
  StructureBreakWidget breakWidget;
  private final List<Either<FormattedText, TooltipComponent>> components;
  private boolean addedPlaceWidget = false;

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
    placeWidget = addRenderableWidget(new StructurePlacerWidget(leftPos + imageWidth, topPos,
        getMenu().getEntity().getId(), getMenu().getEntity().getBlockPos()));
    breakWidget = addRenderableWidget(new StructureBreakWidget(leftPos + imageWidth,
        topPos + placeWidget.getHeight(),
        getMenu().getEntity().getId(), getMenu().getEntity().getBlockPos()));
    placeWidget.setTooltip(placeWidget.getTooltip());
    breakWidget.setTooltip(breakWidget.getTooltip());
    if (!addedPlaceWidget) {
      components.addFirst(Either.left(placeWidget.component));
      addedPlaceWidget = true;
    }

    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(this.leftPos, this.topPos, 0);
    float scale = 0.72f;
    guiGraphics.pose().scale(scale, scale, scale);
    int offsetX = 12;
    int offsetY = 12;

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

    if (entity.isPaused()) {
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
    Optional.of(entity.getFoundMachine()).ifPresentOrElse(machine -> {
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
                  stacks.forEach(stack -> {
                    map.forEach((c, s) -> {
                      if (c.getString().equals(component.getString())) {
                        s.forEach(s1 -> {
                          if (s1.is(stack.getItem())) {
                            s1.grow(stack.getCount());
                          }
                          s2.add(s1);
                        });
                      }
                    });
                  });
                } else {
                  s2.addAll(stacks);
                }

                map.put(component, s2);
              });
          map.forEach((c, stacks) -> {
            if (c != null && !stacks.isEmpty()) {
              MMRItemTooltipComponent component = new MMRItemTooltipComponent(stacks);
              component.setComponent(
                  Component.translatable(
                      "modular_machinery_reborn.controller.required.block",
                      c
                  ).withStyle(ChatFormatting.GRAY)
              );
              components.add(Either.right(component));
            }
          });
        },
        () -> components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.no_machine").withStyle(ChatFormatting.GRAY))));
  }
}
