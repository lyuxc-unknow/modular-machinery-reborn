package es.degrassi.mmreborn.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;

public enum Blending {
  DEFAULT(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA),
  ALPHA(GL11.GL_ONE, GL11.GL_SRC_ALPHA),
  PREALPHA(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA),
  MULTIPLY(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA),
  ADDITIVE(GL11.GL_ONE, GL11.GL_ONE),
  ADDITIVEDARK(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR),
  OVERLAYDARK(GL11.GL_SRC_COLOR, GL11.GL_ONE),
  ADDITIVE_ALPHA(GL11.GL_SRC_ALPHA, GL11.GL_ONE),
  CONSTANT_ALPHA(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA),
  INVERTEDADD(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);

  public final int sfactor;
  public final int dfactor;

  Blending(int s, int d) {
    sfactor = s;
    dfactor = d;
  }

  public void apply() {
    GL11.glBlendFunc(sfactor, dfactor);
  }

  public void applyStateManager() {
    GlStateManager._blendFunc(sfactor, dfactor);
  }
}
