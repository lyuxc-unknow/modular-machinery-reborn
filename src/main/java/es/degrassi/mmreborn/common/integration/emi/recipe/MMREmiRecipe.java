package es.degrassi.mmreborn.common.integration.emi.recipe;

import com.mojang.datafixers.util.Pair;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import es.degrassi.mmreborn.common.crafting.MachineRecipe;
import es.degrassi.mmreborn.common.integration.emi.EmiComponentRegistry;
import es.degrassi.mmreborn.common.integration.emi.EmiStackRegistry;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MMREmiRecipe extends BasicEmiRecipe {
  @Getter
  private final MachineRecipe recipe;
  public final int initialX = 8, gap = 8;
  @Getter
  protected int width = 256, height = 256;

  public final List<FormattedText> textsToRender = new LinkedList<>();

  public MMREmiRecipe(EmiRecipeCategory category, RecipeHolder<MachineRecipe> recipe) {
    super(category, recipe.id(), recipe.value().getWidth(), recipe.value().getHeight());
    this.recipe = recipe.value();
    this.inputs = this.recipe
        .getCraftingRequirements()
        .stream()
        .filter(requirement -> requirement.getActionType().isInput())
        .filter(requirement -> EmiStackRegistry.hasEmiStack(requirement.getRequirementType()))
        .map(requirement -> requirement.getRequirementType().castRequirement(requirement))
        .map(requirement -> EmiStackRegistry.getStack(requirement.getRequirementType()).create(requirement))
        .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    this.outputs = this.recipe
        .getCraftingRequirements()
        .stream()
        .filter(requirement -> !requirement.getActionType().isInput())
        .filter(requirement -> EmiStackRegistry.hasEmiStack(requirement.getRequirementType()))
        .map(requirement -> requirement.getRequirementType().castRequirement(requirement))
        .map(requirement -> EmiStackRegistry.getStack(requirement.getRequirementType()).create(requirement))
        .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    this.catalysts = this.recipe
        .getCraftingRequirements()
        .stream()
        .filter(requirement -> requirement.getActionType().isInput())
        .filter(requirement -> EmiStackRegistry.hasEmiStack(requirement.getRequirementType()))
        .map(requirement -> requirement.getRequirementType().castRequirement(requirement))
        .map(requirement -> EmiStackRegistry.getStack(requirement.getRequirementType()).create(requirement))
        .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    textsToRender.clear();
    widgets.addFillingArrow(recipe.getProgressPosition().x(), recipe.getProgressPosition().y(), 1_000);
    Font font = Minecraft.getInstance().font;
    textsToRender.addAll(splitLines(font, List.of(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.duration",
        recipe.getRecipeTotalTickTime()
    )), recipe.getWidth() - 8).getFirst());

    recipe.getCraftingRequirements()
        .stream()
        .filter(component -> EmiComponentRegistry.hasEmiComponent(component.getRequirementType()))
        .map(component -> component.getRequirementType().castRequirement(component))
        .map(component -> EmiComponentRegistry.getEmiComponent(component.getRequirementType()).create(component))
        .forEach(requirement -> requirement.addWidgets(widgets, this));

    Language language = Language.getInstance();
    AtomicInteger nextHeight = new AtomicInteger(0);
    AtomicInteger toRemove = new AtomicInteger(0);

    textsToRender.forEach(component -> {
      nextHeight.set(recipe.getHeight() - gap - font.wordWrapHeight(component, recipe.getWidth() - 8) - toRemove.get());
      widgets.addText(language.getVisualOrder(component), initialX, nextHeight.get(), 0xFF000000, false);
      toRemove.getAndAdd(font.wordWrapHeight(component, recipe.getWidth() - 8) + 2);
    });
  }

  private static Pair<List<FormattedText>, Boolean> splitLines(Font font, List<FormattedText> lines, int width) {
    if (lines.isEmpty()) {
      return new Pair<>(List.of(), false);
    }
    if (Integer.MAX_VALUE <= 0) {
      return new Pair<>(List.of(), true);
    }
    if (width <= 0) {
      return new Pair<>(List.copyOf(lines), false);
    }

    StringSplitter splitter = font.getSplitter();
    List<FormattedText> result = new ArrayList<>();
    for (FormattedText line : lines) {
      List<FormattedText> splitLines;
      if (line.getString().isEmpty()) {
        splitLines = List.of(line);
      } else {
        splitLines = splitter.splitLines(line, width, Style.EMPTY);
      }

      for (FormattedText splitLine : splitLines) {
        if (result.size() == Integer.MAX_VALUE) {
          // result is at the max size, but we still have more to add.
          // Truncate the last line to indicate that there is more text that can't be displayed.
          FormattedText last = result.removeLast();
          last = truncateStringToWidth(last, width, font);
          result.add(last);
          return new Pair<>(result, true);
        }
        result.add(splitLine);
      }

    }

    return new Pair<>(result, false);
  }

  private static FormattedText truncateStringToWidth(FormattedText text, int width, Font font) {
    int ellipsisWidth = font.width("...");
    StringSplitter splitter = font.getSplitter();

    FormattedText truncatedText = font.substrByWidth(text, width - ellipsisWidth);

    Style style = splitter.componentStyleAtWidth(text, width - ellipsisWidth);
    if (style == null) {
      style = Style.EMPTY;
    }

    return FormattedText.composite(truncatedText, Component.literal("...").setStyle(style));
  }
}
