/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import kawaii.render.ColorWrapper;
import kawaii.render.ImageCanvas;
import kawaii.ui.font.GlyphInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CustomFontRenderer {
    public boolean antiAlias;
    public float textureSize = 512.0f;
    public Font font;
    public int fontHeight = -1;
    public DynamicTexture texture;
    public boolean fractionalMetrics;
    public int charSpacing = 0;
    public GlyphInfo[] glyphs = new GlyphInfo[256];

    public Font getFont() {
        return this.font;
    }

    public void setFractionalMetrics(boolean useFractionalMetrics) {
        if (this.fractionalMetrics != useFractionalMetrics) {
            this.fractionalMetrics = useFractionalMetrics;
            this.texture = this.createTexture(this.font, useFractionalMetrics, this.antiAlias, this.glyphs);
        }
    }

    public boolean isAntiAlias() {
        return this.antiAlias;
    }

    public boolean isFractionalMetrics() {
        return this.fractionalMetrics;
    }

    public void setFont(Font font) {
        this.font = font;
        this.texture = this.createTexture(font, this.fractionalMetrics, this.antiAlias, this.glyphs);
    }

    public int getStringHeight(String text) {
        return this.getHeight();
    }

    public BufferedImage createFontImage(Font font, boolean textAntiAlias, boolean useFractionalMetrics, GlyphInfo[] glyphInfoArray) {
        int canvasSize = (int)this.textureSize;
        ImageCanvas imageCanvas = new ImageCanvas(canvasSize, canvasSize, 2);
        Graphics2D graphics2D = (Graphics2D)imageCanvas.getImageGraphics();
        graphics2D.setFont(font);
        graphics2D.setColor(new ColorWrapper(255, 255, 255, 0));
        graphics2D.fillRect(0, 0, canvasSize, canvasSize);
        graphics2D.setColor(Color.WHITE);
        graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, useFractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, textAntiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, textAntiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int rowHeight = 0;
        int cursorX = 0;
        int cursorY = 1;
        for (int i = 0; i < glyphInfoArray.length; ++i) {
            char c = (char)(i & 0xFFFF);
            GlyphInfo glyphInfo = new GlyphInfo();
            Rectangle2D rectangle2D = fontMetrics.getStringBounds(String.valueOf(c), graphics2D);
            glyphInfo.advanceWidth = rectangle2D.getBounds().width + 8;
            glyphInfo.height = rectangle2D.getBounds().height;
            if (cursorX + glyphInfo.advanceWidth >= canvasSize) {
                cursorX = 0;
                cursorY += rowHeight;
                rowHeight = 0;
            }
            if (glyphInfo.height > rowHeight) {
                rowHeight = glyphInfo.height;
            }
            glyphInfo.textureX = cursorX;
            glyphInfo.textureY = cursorY;
            if (glyphInfo.height > this.fontHeight) {
                this.fontHeight = glyphInfo.height;
            }
            glyphInfoArray[i] = glyphInfo;
            graphics2D.drawString(String.valueOf(c), cursorX + 2, cursorY + fontMetrics.getAscent());
            cursorX += glyphInfo.advanceWidth;
        }
        return imageCanvas;
    }

    public void setAntiAlias(boolean useAntiAlias) {
        if (this.antiAlias != useAntiAlias) {
            this.antiAlias = useAntiAlias;
            this.texture = this.createTexture(this.font, this.fractionalMetrics, useAntiAlias, this.glyphs);
        }
    }

    public void drawQuad(float x, float y, float width, float height, float texX, float texY, float texWidth, float texHeight) {
        float normTexX = texX / this.textureSize;
        float normTexY = texY / this.textureSize;
        float normTexWidth = texWidth / this.textureSize;
        float normTexHeight = texHeight / this.textureSize;
        GL11.glTexCoord2f(normTexX + normTexWidth, normTexY);
        GL11.glVertex2d(x + width, y);
        GL11.glTexCoord2f(normTexX, normTexY);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2f(normTexX, normTexY + normTexHeight);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(normTexX, normTexY + normTexHeight);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(normTexX + normTexWidth, normTexY + normTexHeight);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2f(normTexX + normTexWidth, normTexY);
        GL11.glVertex2d(x + width, y);
    }

    public DynamicTexture createTexture(Font font, boolean useFractionalMetrics, boolean useAntiAlias, GlyphInfo[] glyphInfoArray) {
        BufferedImage bufferedImage = this.createFontImage(font, useFractionalMetrics, useAntiAlias, glyphInfoArray);
        try {
            return new DynamicTexture(bufferedImage);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int getStringWidth(String text) {
        int width = 0;
        char[] cArray = text.toCharArray();
        int charCount = cArray.length;
        for (int i = 0; i < charCount; ++i) {
            char c = cArray[i];
            if (c < this.glyphs.length) {
                width += this.glyphs[c].advanceWidth - 8 + this.charSpacing;
            }
        }
        return width / 2;
    }

    public void drawChar(GlyphInfo[] glyphInfoArray, char character, float x, float y) {
        try {
            this.drawQuad(x, y, glyphInfoArray[character].advanceWidth, glyphInfoArray[character].height, glyphInfoArray[character].textureX, glyphInfoArray[character].textureY, glyphInfoArray[character].advanceWidth, glyphInfoArray[character].height);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public int getHeight() {
        return (this.fontHeight - 8) / 2;
    }

    public CustomFontRenderer(Font font, boolean useFractionalMetrics, boolean useAntiAlias) {
        this.font = font;
        this.fractionalMetrics = useFractionalMetrics;
        this.antiAlias = useAntiAlias;
        this.texture = this.createTexture(font, useFractionalMetrics, useAntiAlias, this.glyphs);
    }
}

