/*
 * Decompiled with CFR 0.152.
 */
package kawaii.render;

import java.awt.Color;
import kawaii.render.ColorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderUtils {
    public static float zLevel;

    public static void setAlphaFunc(float alphaPercent) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, (float)((double)alphaPercent * 0.01));
    }

    public static void drawRoundedOutline(double x, double y, double width, double height, double radius, float lineWidth, int color) {
        int i;
        double pi = 3.141592653589793;
        double halfTurnDegrees = 180.0;
        double scaleFactor = 2.0;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double right = x + width;
        double bottom = y + height;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        x *= scaleFactor;
        y *= scaleFactor;
        right *= scaleFactor;
        bottom *= scaleFactor;
        GL11.glLineWidth(lineWidth);
        GL11.glDisable(3553);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glEnable(2848);
        GL11.glBegin(2);
        for (i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin((double)i * pi / halfTurnDegrees) * (radius * -1.0), y + radius + Math.cos((double)i * pi / halfTurnDegrees) * (radius * -1.0));
        }
        for (i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin((double)i * pi / halfTurnDegrees) * (radius * -1.0), bottom - radius + Math.cos((double)i * pi / halfTurnDegrees) * (radius * -1.0));
        }
        for (i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(right - radius + Math.sin((double)i * pi / halfTurnDegrees) * radius, bottom - radius + Math.cos((double)i * pi / halfTurnDegrees) * radius);
        }
        for (i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(right - radius + Math.sin((double)i * pi / halfTurnDegrees) * radius, y + radius + Math.cos((double)i * pi / halfTurnDegrees) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);
        GL11.glPopAttrib();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void applyScissor(double left, double top, double right, double bottom) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int scaleFactor = scaledResolution.getScaleFactor();
        GL11.glScissor((int)(left * (double)scaleFactor), (int)(((double)scaledResolution.getScaledHeight() - bottom) * (double)scaleFactor), (int)((right - left) * (double)scaleFactor), (int)((bottom - top) * (double)scaleFactor));
    }

    public static void drawRoundedRect(double x, double y, double width, double height, double radius, int color) {
        int i;
        double pi = Math.PI;
        double halfTurnDegrees = 180.0;
        double scaleFactor = 2.0;
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double right = x + width;
        double bottom = y + height;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        x *= scaleFactor;
        y *= scaleFactor;
        right *= scaleFactor;
        bottom *= scaleFactor;
        GL11.glDisable(3553);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        for (i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin((double)i * pi / halfTurnDegrees) * (radius * -1.0), y + radius + Math.cos((double)i * pi / halfTurnDegrees) * (radius * -1.0));
        }
        for (i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + Math.sin((double)i * pi / halfTurnDegrees) * (radius * -1.0), bottom - radius + Math.cos((double)i * pi / halfTurnDegrees) * (radius * -1.0));
        }
        for (i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(right - radius + Math.sin((double)i * pi / halfTurnDegrees) * radius, bottom - radius + Math.cos((double)i * pi / halfTurnDegrees) * radius);
        }
        for (i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(right - radius + Math.sin((double)i * pi / halfTurnDegrees) * radius, y + radius + Math.cos((double)i * pi / halfTurnDegrees) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);
        GL11.glPopAttrib();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void drawRect(double left, double top, double right, double bottom, double thickness, int color) {
        Gui.drawRect(left, top, left + thickness, bottom, color);
        Gui.drawRect(left, top, right, top + thickness, color);
        Gui.drawRect(right, top, right + thickness, bottom, color);
        Gui.drawRect(left, bottom, right + thickness, bottom + thickness, color);
    }

    public static void drawImage(double x, double y, int width, int height, ResourceLocation resourceLocation, Color color) {
        GL11.glEnable(3042);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9729);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glDisable(3042);
    }

    public static float lerp(double end, double start, double progress) {
        return (float)(start + (end - start) * progress);
    }

    public static void setGlColor(int color) {
        ColorWrapper colorWrapper = new ColorWrapper(color);
        GL11.glColor4f((float)colorWrapper.redValue() / 255.0f, (float)colorWrapper.greenValue() / 255.0f, (float)colorWrapper.blueValue() / 255.0f, (float)colorWrapper.alphaValue() / 255.0f);
    }

    public static void drawImageWithAlpha(double x, double y, int width, int height, ResourceLocation resourceLocation, double alpha) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, (float)alpha);
        GL11.glEnable(3042);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9729);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glDisable(3042);
    }

    public static void setColor(int color) {
        ColorWrapper colorWrapper = new ColorWrapper(color);
        GL11.glColor4f(colorWrapper.redValue(), colorWrapper.greenValue(), colorWrapper.blueValue(), colorWrapper.alphaValue());
    }

    public static Color setColorWithAlpha(int color, float alpha) {
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
        return new ColorWrapper(red, green, blue, alpha);
    }

    public static void drawRoundedRectBounds(double left, double top, double right, double bottom, double radius, int color) {
        int i;
        double pi = 3.141592653589793;
        double halfTurnDegrees = 180.0;
        double scaleFactor = 2.0;
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double scaledRight = right;
        double scaledBottom = bottom;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        left *= scaleFactor;
        top *= scaleFactor;
        scaledRight *= scaleFactor;
        scaledBottom *= scaleFactor;
        GL11.glDisable(3553);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        for (i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(left + radius + Math.sin((double)i * pi / halfTurnDegrees) * (radius * -1.0), top + radius + Math.cos((double)i * pi / halfTurnDegrees) * (radius * -1.0));
        }
        for (i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(left + radius + Math.sin((double)i * pi / halfTurnDegrees) * (radius * -1.0), scaledBottom - radius + Math.cos((double)i * pi / halfTurnDegrees) * (radius * -1.0));
        }
        for (i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(scaledRight - radius + Math.sin((double)i * pi / halfTurnDegrees) * radius, scaledBottom - radius + Math.cos((double)i * pi / halfTurnDegrees) * radius);
        }
        for (i = 90; i <= 180; --i) {
            GL11.glVertex2d(scaledRight - radius + Math.sin((double)i * pi / halfTurnDegrees) * radius, top + radius + Math.cos((double)i * pi / halfTurnDegrees) * radius);
            i += 4;
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);
        GL11.glPopAttrib();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void resetColor() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        RenderUtils.setAlphaFunc(0.0f);
        RenderUtils.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glShadeModel(7425);
        GL11.glHint(3154, 4354);
        GL11.glBegin(7);
        RenderUtils.setGlColor(startColor);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        RenderUtils.setGlColor(endColor);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        RenderUtils.resetColor();
    }

    public static void drawRoundedRectCorners(double left, double top, double right, double bottom, double radius, int color) {
        int i;
        double pi = 3.141592653589793;
        double halfTurnDegrees = 180.0;
        double scaleFactor = 2.0;
        GL11.glScaled(0.5, 0.5, 0.5);
        left *= scaleFactor;
        top *= scaleFactor;
        right *= scaleFactor;
        bottom *= scaleFactor;
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GlStateManager.enableBlend();
        RenderUtils.setColorWithAlpha(color, 1.0f);
        GL11.glBegin(9);
        for (i = 0; i <= 90; ++i) {
            GL11.glVertex2d(left + radius + Math.sin((double)i * pi / halfTurnDegrees) * radius * -1.0, top + radius + Math.cos((double)i * pi / halfTurnDegrees) * radius * -1.0);
        }
        for (i = 90; i <= 180; ++i) {
            GL11.glVertex2d(left + radius + Math.sin((double)i * pi / halfTurnDegrees) * radius * -1.0, bottom - radius + Math.cos((double)i * pi / halfTurnDegrees) * radius * -1.0);
        }
        for (i = 0; i <= 90; ++i) {
            GL11.glVertex2d(right - radius + Math.sin((double)i * pi / halfTurnDegrees) * radius, bottom - radius + Math.cos((double)i * pi / halfTurnDegrees) * radius);
        }
        for (i = 90; i <= 180; ++i) {
            GL11.glVertex2d(right - radius + Math.sin((double)i * pi / halfTurnDegrees) * radius, top + radius + Math.cos((double)i * pi / halfTurnDegrees) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
    }

    public static void drawGradientQuad(double coordA, double coordB, double coordC, double coordD, int startColor, int endColor) {
        float channelMax = 255.0f;
        float startAlpha = (float)(startColor >> 24 & 0xFF) / channelMax;
        float startRed = (float)(startColor >> 16 & 0xFF) / channelMax;
        float startGreen = (float)(startColor >> 8 & 0xFF) / channelMax;
        float startBlue = (float)(startColor & 0xFF) / channelMax;
        float endAlpha = (float)(endColor >> 24 & 0xFF) / channelMax;
        float endRed = (float)(endColor >> 16 & 0xFF) / channelMax;
        float endGreen = (float)(endColor >> 8 & 0xFF) / channelMax;
        float endBlue = (float)(endColor & 0xFF) / channelMax;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(coordD, coordB, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        worldRenderer.pos(coordA, coordC, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        worldRenderer.pos(coordB, coordD, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        worldRenderer.pos(coordC, coordA, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}

