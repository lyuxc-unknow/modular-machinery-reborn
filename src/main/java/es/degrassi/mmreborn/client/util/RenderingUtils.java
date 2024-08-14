package es.degrassi.mmreborn.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import org.lwjgl.opengl.GL11;

public class RenderingUtils {
//  static void drawWhiteOutlineCubes(List<BlockPos> positions, float partialTicks) {
//    GlStateManager._enableBlend();
//    GlStateManager.glBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
//    GlStateManager._clearColor(1F, 1F, 1F, 0.4F);
//    GlStateManager._enableColorLogicOp();;
//    GlStateManager._disableCull();
//
//    Entity player = Minecraft.getInstance().player;
//    if (player == null) return;
//
//    BufferBuilder vb = Tesselator.getInstance().getBuilder();
//    vb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//
//    double dX = player.tickCount + (player.getBlockX() - player.tickCount) * (double) partialTicks;
//    double dY = player.tickCount + (player.getBlockY() - player.tickCount) * (double) partialTicks;
//    double dZ = player.tickCount + (player.getBlockZ() - player.tickCount) * (double) partialTicks;
//    for (BlockPos pos : positions) {
//      AABB box = Shapes.block().move(pos.getX(), pos.getY(), pos.getZ()).move((float) -dX, (float) -dY, (float) -dZ).bounds();
//
//      vb.vertex((float) box.minX, (float) box.minY, (float) box.minZ).endVertex();
//      vb.vertex((float) box.maxX, (float) box.minY, (float) box.minZ).endVertex();
//      vb.vertex((float) box.maxX, (float) box.minY, (float) box.maxZ).endVertex();
//      vb.vertex((float) box.minX, (float) box.minY, (float) box.maxZ).endVertex();
//
//      vb.vertex((float) box.minX, (float) box.maxY, (float) box.maxZ).endVertex();
//      vb.vertex((float) box.maxX, (float) box.maxY, (float) box.maxZ).endVertex();
//      vb.vertex((float) box.maxX, (float) box.maxY, (float) box.minZ).endVertex();
//      vb.vertex((float) box.minX, (float) box.maxY, (float) box.minZ).endVertex();
//
//      vb.vertex((float) box.maxX, (float) box.minY, (float) box.minZ).endVertex();
//      vb.vertex((float) box.maxX, (float) box.maxY, (float) box.minZ).endVertex();
//      vb.vertex((float) box.maxX, (float) box.maxY, (float) box.maxZ).endVertex();
//      vb.vertex((float) box.maxX, (float) box.minY, (float) box.maxZ).endVertex();
//
//      vb.vertex((float) box.minX, (float) box.minY, (float) box.maxZ).endVertex();
//      vb.vertex((float) box.minX, (float) box.maxY, (float) box.maxZ).endVertex();
//      vb.vertex((float) box.minX, (float) box.maxY, (float) box.minZ).endVertex();
//      vb.vertex((float) box.minX, (float) box.minY, (float) box.minZ).endVertex();
//
//      vb.vertex((float) box.minX, (float) box.maxY, (float) box.minZ).endVertex();
//      vb.vertex((float) box.maxX, (float) box.maxY, (float) box.minZ).endVertex();
//      vb.vertex((float) box.maxX, (float) box.minY, (float) box.minZ).endVertex();
//      vb.vertex((float) box.minX, (float) box.minY, (float) box.minZ).endVertex();
//
//      vb.vertex((float) box.minX, (float) box.minY, (float) box.maxZ).endVertex();
//      vb.vertex((float) box.maxX, (float) box.minY, (float) box.maxZ).endVertex();
//      vb.vertex((float) box.maxX, (float) box.maxY, (float) box.maxZ).endVertex();
//      vb.vertex((float) box.minX, (float) box.maxY, (float) box.maxZ).endVertex();
//    }
//    vb.setQuadSorting(VertexSorting.byDistance((float) player.getX(), (float) player.getY(), (float) player.getZ()));
//
//    BufferUploader.drawWithShader(vb.end());
//
//    GlStateManager._enableCull();
//    GlStateManager._disableColorLogicOp();
//    GlStateManager._clearColor(1F, 1F, 1F, 1F);
//  }
//
//  public static void renderBlueStackTooltip(int x, int y, List<Tuple<ItemStack, String>> tooltipData, Font fr, ItemRenderer ri, GuiGraphics guiGraphics) {
//    renderStackTooltip(x, y, tooltipData, new Color(0x000037), new Color(0x000000), Color.WHITE, fr, ri, guiGraphics);
//  }
//
//  public static void renderStackTooltip(int x, int y, List<Tuple<ItemStack, String>> tooltipData, Color color, Color colorFade, Color strColor, Font font, ItemRenderer ri, GuiGraphics guiGraphics) {
//    RenderSystem.setShaderTexture(0, new ResourceLocation("textures/atlas/blocks.png"));
//    RenderSystem.setShader(GameRenderer::getPositionTexShader);
//    if (!tooltipData.isEmpty()) {
//      int esWidth = 0;
//      for (Tuple<ItemStack, String> toolTip : tooltipData) {
//        int width = font.width(toolTip.getB()) + 17;
//        if (width > esWidth)
//          esWidth = width;
//      }
//      ScaledResolution sr = new ScaledResolution(Minecraft.getInstance());
//      if(x + 15 + esWidth > sr.getScaledWidth()) {
//        x -= esWidth + 24;
//      }
//      int sumLineHeight = 8;
//      int lastAdded = 0;
//      if (tooltipData.size() > 1) {
//        sumLineHeight += 2;
//        for (Tuple<ItemStack, String> tooltipEntry : tooltipData) {
//          int height = tooltipEntry.getA().isEmpty() ? 10 : 17;
//          sumLineHeight += height;
//          lastAdded = height;
//        }
//        sumLineHeight -= lastAdded;
//      }
//
//      if(y + sumLineHeight > sr.getScaledHeight()) {
//        y = (sr.getScaledHeight() - sumLineHeight);
//        y = Math.max(25, y);
//      }
//
//      int pX = x + 12;
//      int pY = y - 12;
//
//      float z = 300F;
//
//      GlStateManager._disableDepthTest();
//      drawGradientRect(pX - 3,           pY - 4,                 z, pX + esWidth + 3, pY - 3,                 color, colorFade);
//      drawGradientRect(pX - 3,           pY + sumLineHeight + 3, z, pX + esWidth + 3, pY + sumLineHeight + 4, color, colorFade);
//      drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY + sumLineHeight + 3, color, colorFade);
//      drawGradientRect(pX - 4,           pY - 3,                 z, pX - 3,           pY + sumLineHeight + 3, color, colorFade);
//      drawGradientRect(pX + esWidth + 3, pY - 3,                 z, pX + esWidth + 4, pY + sumLineHeight + 3, color, colorFade);
//
//      int rgb = color.getRGB();
//      int col = (rgb & 0x00FFFFFF) | rgb & 0xFF000000;
//      Color colOp = new Color(col);
//      drawGradientRect(pX - 3,           pY - 3 + 1,             z, pX - 3 + 1,       pY + sumLineHeight + 3 - 1, color, colOp);
//      drawGradientRect(pX + esWidth + 2, pY - 3 + 1,             z, pX + esWidth + 3, pY + sumLineHeight + 3 - 1, color, colOp);
//      drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY - 3 + 1,                 colOp, colOp);
//      drawGradientRect(pX - 3,           pY + sumLineHeight + 2, z, pX + esWidth + 3, pY + sumLineHeight + 3,     color, color);
//
//      for (Tuple<ItemStack, String> stackDesc : tooltipData) {
//        if(!stackDesc.getA().isEmpty()) {
//          guiGraphics.drawString(font, stackDesc.getB(), pX + 17, pY, strColor.getRGB());
//          GlStateManager._clearColor(1F, 1F, 1F, 1F);
//          guiGraphics.pose().pushPose();
////          ri.renderItemAndEffectIntoGUI(stackDesc.getA(), pX - 1, pY - 5);
//          guiGraphics.pose().popPose();
//          pY += 17;
//        } else if(stackDesc.getB().isEmpty()) {
//          pY += 6;
//        } else {
//          guiGraphics.drawString(font, stackDesc.getB(), pX, pY, strColor.getRGB());
//          GlStateManager._clearColor(1F, 1F, 1F, 1F);
//          pY += 10;
//        }
//        GlStateManager._enableBlend();
//        GlStateManager._blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);
//      }
//      GlStateManager._clearColor(1F, 1F, 1F, 1F);
//      GlStateManager._enableDepthTest();
//    }
//
//    GlStateManager._clearColor(1F, 1F, 1F, 1F);
//    RenderSystem.setShaderTexture(0, new ResourceLocation("textures/atlas/blocks.png"));
//    RenderSystem.setShader(GameRenderer::getPositionTexShader);
//  }
//
//  public static void renderBlueTooltip(int x, int y, List<String> tooltipData, Font fontRenderer, GuiGraphics guiGraphics) {
//    renderTooltip(x, y, tooltipData, new Color(0x000037), new Color(0x000000), Color.WHITE, fontRenderer, guiGraphics);
//  }
//
//  public static void renderTooltip(int x, int y, List<String> tooltipData, Color color, Color colorFade, Color strColor, Font font, GuiGraphics guiGraphics) {
//    RenderSystem.setShaderTexture(0, new ResourceLocation("textures/atlas/blocks.png"));
//    RenderSystem.setShader(GameRenderer::getPositionTexShader);
//    boolean lighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
////    if (lighting)
////      RenderHelper.disableStandardItemLighting();
//
//    if (!tooltipData.isEmpty()) {
//      int esWidth = 0;
//      for (String toolTip : tooltipData) {
//        int width = font.width(toolTip);
//        if (width > esWidth)
//          esWidth = width;
//      }
//      ScaledResolution sr = new ScaledResolution(Minecraft.getInstance());
//      if(x + 15 + esWidth > sr.getScaledWidth()) {
//        x -= esWidth + 24;
//      }
//
//      int sumLineHeight = 8;
//      if (tooltipData.size() > 1)
//        sumLineHeight += 2 + (tooltipData.size() - 1) * 10;
//
//      if(y + sumLineHeight > sr.getScaledHeight()) {
//        y = (sr.getScaledHeight() - sumLineHeight);
//        y = Math.max(25, y);
//      }
//
//      int pX = x + 12;
//      int pY = y - 12;
//
//      float z = 300F;
//
//      drawGradientRect(pX - 3,           pY - 4,                 z, pX + esWidth + 3, pY - 3,                 color, colorFade);
//      drawGradientRect(pX - 3,           pY + sumLineHeight + 3, z, pX + esWidth + 3, pY + sumLineHeight + 4, color, colorFade);
//      drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY + sumLineHeight + 3, color, colorFade);
//      drawGradientRect(pX - 4,           pY - 3,                 z, pX - 3,           pY + sumLineHeight + 3, color, colorFade);
//      drawGradientRect(pX + esWidth + 3, pY - 3,                 z, pX + esWidth + 4, pY + sumLineHeight + 3, color, colorFade);
//
//      int rgb = color.getRGB();
//      int col = (rgb & 0x00FFFFFF) | rgb & 0xFF000000;
//      Color colOp = new Color(col);
//      drawGradientRect(pX - 3,           pY - 3 + 1,             z, pX - 3 + 1,       pY + sumLineHeight + 3 - 1, color, colOp);
//      drawGradientRect(pX + esWidth + 2, pY - 3 + 1,             z, pX + esWidth + 3, pY + sumLineHeight + 3 - 1, color, colOp);
//      drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY - 3 + 1,                 colOp, colOp);
//      drawGradientRect(pX - 3,           pY + sumLineHeight + 2, z, pX + esWidth + 3, pY + sumLineHeight + 3,     color, color);
//
//      GlStateManager._disableDepthTest();
//      for (int i = 0; i < tooltipData.size(); ++i) {
//        String str = tooltipData.get(i);
//        guiGraphics.drawString(font, str, pX, pY, strColor.getRGB());
//        if (i == 0)
//          pY += 2;
//        pY += 10;
//      }
//      GlStateManager._clearColor(1F, 1F, 1F, 1F);
//      GlStateManager._enableDepthTest();
//    }
//
////    if (lighting)
////      RenderHelper.enableStandardItemLighting();
//    GlStateManager._clearColor(1F, 1F, 1F, 1F);
//    RenderSystem.setShaderTexture(0, new ResourceLocation("textures/atlas/blocks.png"));
//    RenderSystem.setShader(GameRenderer::getPositionTexShader);
//  }
//
//  public static void drawGradientRect(int x, int y, float z, int toX, int toY, Color color, Color colorFade) {
//    GlStateManager._enableBlend();
//    GlStateManager._blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);
//    GlStateManager.glCreateShader(GL11.GL_SMOOTH);
//    BufferBuilder vb = Tesselator.getInstance().getBuilder();
//    vb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//    vb.vertex(toX, y,   z).color(color.getRed(),     color.getGreen(),     color.getBlue(),     color.getAlpha()).endVertex();
//    vb.vertex(x,   y,   z).color(color.getRed(),     color.getGreen(),     color.getBlue(),     color.getAlpha()).endVertex();
//    vb.vertex(x,   toY, z).color(colorFade.getRed(), colorFade.getGreen(), colorFade.getBlue(), colorFade.getAlpha()).endVertex();
//    vb.vertex(toX, toY, z).color(colorFade.getRed(), colorFade.getGreen(), colorFade.getBlue(), colorFade.getAlpha()).endVertex();
//    BufferUploader.drawWithShader(vb.end());
//    GlStateManager.glCreateShader(GL11.GL_FLAT);
//    GlStateManager._disableBlend();
//  }

}
