package es.degrassi.mmreborn.common.integration.jei.category.drawable;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.elements.DrawableAnimated;
import mezz.jei.common.gui.elements.DrawableCombined;
import mezz.jei.common.gui.elements.OffsetDrawable;
import mezz.jei.common.gui.textures.Textures;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ProgressDrawable implements IDrawableAnimated {
  private final int ticksPerCycle;
  private final IDrawableAnimated.StartDirection startDirection;
  private final IDrawable drawable;

  public ProgressDrawable(int ticksPerCycle, IDrawableAnimated.StartDirection startDirection) {
    this.ticksPerCycle = ticksPerCycle;
    this.startDirection = startDirection;
    this.drawable = createProgress();
  }

  private IDrawable createProgress() {
    Textures textures = Internal.getTextures();

    IDrawableStatic recipeArrowFilled = textures.getRecipeArrowFilled();
    IDrawable animatedFill = new DrawableAnimated(recipeArrowFilled, ticksPerCycle, startDirection, false);
    IDrawable drawableCombined = new DrawableCombined(textures.getRecipeArrow(), animatedFill);
    return new OffsetDrawable(drawableCombined, 0, 0);
  }

  @Override
  public int getWidth() {
    return drawable.getWidth();
  }

  @Override
  public int getHeight() {
    return drawable.getHeight();
  }

  @Override
  public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
    drawable.draw(guiGraphics, xOffset, yOffset);
  }
}
