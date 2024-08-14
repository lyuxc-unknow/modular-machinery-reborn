package es.degrassi.mmreborn.client.util;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScaledResolution {
  private final double scaledWidthD;
  private final double scaledHeightD;
  @Getter
  private int scaledWidth;
  @Getter
  private int scaledHeight;
  @Getter
  private int scaleFactor;

  public ScaledResolution(Minecraft minecraftClient) {
    this.scaledWidth = minecraftClient.getWindow().getGuiScaledWidth();
    this.scaledHeight = minecraftClient.getWindow().getGuiScaledHeight();
    this.scaleFactor = 1;
    boolean flag = minecraftClient.isEnforceUnicode();
    int i = minecraftClient.options.guiScale().get();

    if (i == 0) {
      i = 1000;
    }

    while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
      ++this.scaleFactor;
    }

    if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
      --this.scaleFactor;
    }

    this.scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
    this.scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
    this.scaledWidth = Mth.ceil(this.scaledWidthD);
    this.scaledHeight = Mth.ceil(this.scaledHeightD);
  }

  public double getScaledWidth_double() {
    return this.scaledWidthD;
  }

  public double getScaledHeight_double() {
    return this.scaledHeightD;
  }
}
