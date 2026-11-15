/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.font;

import java.awt.Font;
import java.util.List;
import kawaii.render.ColorThemes;
import kawaii.render.ColorWrapper;
import kawaii.ui.font.CustomFontRenderer;
import kawaii.ui.font.GlyphInfo;
import kawaii.util.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CFontRenderer
extends CustomFontRenderer {
    public GlyphInfo[] boldGlyphs = new GlyphInfo[256];
    public DynamicTexture boldTexture;
    public GlyphInfo[] boldItalicGlyphs;
    public DynamicTexture boldItalicTexture;
    public int[] colorCodes;
    public DynamicTexture italicTexture;
    public GlyphInfo[] italicGlyphs = new GlyphInfo[256];

    @Override
    public void setAntiAlias(boolean useAntiAlias) {
        super.setAntiAlias(useAntiAlias);
        this.reloadFont();
    }

    public CFontRenderer(Font font, boolean useFractionalMetrics, boolean useAntiAlias) {
        super(font, useFractionalMetrics, useAntiAlias);
        this.boldItalicGlyphs = new GlyphInfo[256];
        this.colorCodes = new int[32];
        this.initColorCodes();
        this.reloadFont();
    }

    public double drawThemedString(String string, double x, double y, int color) {
        double offsetX = 0.0;
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char ch = chars[i];
            this.drawString(String.valueOf(ch), x + offsetX, y, ColorThemes.getThemeColor((int)((double)color + offsetX) * 2));
            if (chars.length != i + 1) {
                offsetX += (double)this.getStringWidth(String.valueOf(ch)) + 0.2;
            }
        }
        return offsetX;
    }

    public void reloadFont() {
        this.boldTexture = this.createTexture(this.font.deriveFont(1), this.fractionalMetrics, this.antiAlias, this.boldGlyphs);
        this.italicTexture = this.createTexture(this.font.deriveFont(2), this.fractionalMetrics, this.antiAlias, this.italicGlyphs);
        this.boldItalicTexture = this.createTexture(this.font.deriveFont(3), this.fractionalMetrics, this.antiAlias, this.boldItalicGlyphs);
    }

    public float drawStringWithShadow(String string, float x, float y, int color) {
        float shadowEndX = this.drawStringInternal(string, (double)x + 1.0, (double)y + 0.5, color, true);
        return Math.max(shadowEndX, this.drawStringInternal(string, x, y, color, false));
    }

    public List<String> wrapLine(String string, double maxWidth) {
        ObjectList lines = new ObjectList();
        String line = "";
        char formatCode = '\uffff';
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char ch = chars[i];
            if (String.valueOf(ch).equals("\u00a7") && i < chars.length - 1) {
                formatCode = chars[i + 1];
            }
            StringBuilder stringBuilder = new StringBuilder();
            if ((double)this.getStringWidth(stringBuilder.append(line).append(ch).toString()) < maxWidth) {
                line = line + ch;
                continue;
            }
            lines.add(line);
            line = "" + formatCode + String.valueOf(ch);
        }
        if (line.length() > 0) {
            lines.add(line);
        }
        return lines;
    }

    public List wrapText(String string, double maxWidth) {
        ObjectList lines = new ObjectList();
        if ((double)this.getStringWidth(string) > maxWidth) {
            String[] words = string.split(" ");
            String line = "";
            char formatCode = '\uffff';
            for (String word : words) {
                for (int i = 0; i < word.toCharArray().length; ++i) {
                    char ch = word.toCharArray()[i];
                    if (!String.valueOf(ch).equals("\u00a7") || i >= word.toCharArray().length - 1) continue;
                    formatCode = word.toCharArray()[i + 1];
                }
                StringBuilder stringBuilder = new StringBuilder();
                if ((double)this.getStringWidth(stringBuilder.append(line).append(word).append(" ").toString()) < maxWidth) {
                    line = line + word + " ";
                    continue;
                }
                lines.add(line);
                line = "" + formatCode + word + " ";
            }
            if (line.length() > 0) {
                if ((double)this.getStringWidth(line) < maxWidth) {
                    lines.add("" + formatCode + line + " ");
                    line = "";
                } else {
                    for (String wrappedLine : this.wrapLine(line, maxWidth)) {
                        lines.add(wrappedLine);
                    }
                }
            }
        } else {
            lines.add(string);
        }
        return lines;
    }

    public float drawCenteredStringWithShadow(String string, double x, double y, int color) {
        float shadowEndX = this.drawStringInternal(string, x - (double)(this.getStringWidth(string) / 2) + 0.45, y + 0.5, color, true);
        return this.drawString(string, x - (double)(this.getStringWidth(string) / 2), y, color);
    }

    public void drawStringWithOutline(String string, double x, double y, int textColor) {
        this.drawString(string, x - 0.5, y, 0);
        this.drawString(string, x + 0.5, y, 0);
        this.drawString(string, x, y - 0.5, 0);
        this.drawString(string, x, y + 0.5, 0);
        this.drawString(string, x, y, textColor);
    }

    public int getTrimmedStringWidth(String string) {
        if (string == null) {
            return 0;
        }
        int width = 0;
        GlyphInfo[] activeGlyphs = this.glyphs;
        boolean boldOn = false;
        boolean italicOn = false;
        int textLength = string.length();
        for (int i = 0; i < textLength; ++i) {
            char ch = string.charAt(i);
            if (String.valueOf(ch).equals("\u00a7") && i < textLength) {
                int codeIndex = "0123456789abcdefklmnor".indexOf(ch);
                if (codeIndex < 16) {
                    boldOn = false;
                    italicOn = false;
                } else if (codeIndex == 17) {
                    boldOn = true;
                    activeGlyphs = italicOn ? this.boldItalicGlyphs : this.boldGlyphs;
                } else if (codeIndex == 20) {
                    italicOn = true;
                    activeGlyphs = boldOn ? this.boldItalicGlyphs : this.italicGlyphs;
                } else if (codeIndex == 21) {
                    boldOn = false;
                    italicOn = false;
                    activeGlyphs = this.glyphs;
                }
                ++i;
            } else if (ch < activeGlyphs.length && ch >= '\u0000') {
                width += activeGlyphs[ch].advanceWidth - 8 + this.charSpacing;
            }
        }
        return (width - this.charSpacing) / 2;
    }

    public void initColorCodes() {
        for (int i = 0; i < 32; ++i) {
            int brightness = (i >> 3 & 1) * 85;
            int red = (i >> 2 & 1) * 170 + brightness;
            int green = (i >> 1 & 1) * 170 + brightness;
            int blue = (i >> 0 & 1) * 170 + brightness;
            if (i == 6) {
                red += 85;
            }
            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCodes[i] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
        }
    }

    public float drawString(String string, double x, double y, int color) {
        return this.drawStringInternal(string, x, y, color, false);
    }

    public float drawCenteredString(String string, double x, double y, int color) {
        return this.drawString(string, x - (double)(this.getStringWidth(string) / 2), y, color);
    }

    public float drawString(String string, float x, float y, int color) {
        return this.drawStringInternal(string, x, y, color, false);
    }

    public void drawLine(double x1, double y1, double x2, double y2, float lineWidth) {
        GL11.glDisable(3553);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    public float drawStringInternal(String string, double x, double y, int textColor, boolean shadow) {
        float channelMax = 255.0f;
        Minecraft minecraft = Minecraft.getMinecraft();
        double startX = x;
        x -= 1.0;
        if (string == null) {
            return 0.0f;
        }
        if (textColor == 0x20FFFFFF) {
            textColor = 0xFFFFFF;
        }
        if ((textColor & 0xFC000000) == 0) {
            textColor |= 0xFF000000;
        }
        if (shadow) {
            textColor = (textColor & 0xFCFCFC) >> 2 | textColor & new ColorWrapper(20, 20, 20, 200).rgbValue();
        }
        GlyphInfo[] activeGlyphs = this.glyphs;
        float textAlpha = (float)(textColor >> 24 & 0xFF) / channelMax;
        boolean obfuscated = false;
        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underline = false;
        x *= 2.0;
        y = (y - 3.0) * 2.0;
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color((float)(textColor >> 16 & 0xFF) / channelMax, (float)(textColor >> 8 & 0xFF) / channelMax, (float)(textColor & 0xFF) / channelMax, textAlpha);
        int stringLength = string.length();
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.texture.getGlTextureId());
        GL11.glBindTexture(3553, this.texture.getGlTextureId());
        for (int i = 0; i < stringLength; ++i) {
            char ch = string.charAt(i);
            if (String.valueOf(ch).equals("\u00a7") && i < stringLength) {
                int codeIndex = 21;
                try {
                    codeIndex = "0123456789abcdefklmnor".indexOf(string.charAt(i + 1));
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (codeIndex < 16) {
                    bold = false;
                    italic = false;
                    obfuscated = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.bindTexture(this.texture.getGlTextureId());
                    activeGlyphs = this.glyphs;
                    if (codeIndex < 0 || codeIndex > 15) {
                        codeIndex = 15;
                    }
                    if (shadow) {
                        codeIndex += 16;
                    }
                    int colorValue = this.colorCodes[codeIndex];
                    GlStateManager.color((float)(colorValue >> 16 & 0xFF) / channelMax, (float)(colorValue >> 8 & 0xFF) / channelMax, (float)(colorValue & 0xFF) / channelMax, textAlpha);
                } else if (codeIndex == 16) {
                    obfuscated = true;
                } else if (codeIndex == 17) {
                    bold = true;
                    if (italic) {
                        GlStateManager.bindTexture(this.boldItalicTexture.getGlTextureId());
                        activeGlyphs = this.boldItalicGlyphs;
                    } else {
                        GlStateManager.bindTexture(this.boldTexture.getGlTextureId());
                        activeGlyphs = this.boldGlyphs;
                    }
                } else if (codeIndex == 18) {
                    strikethrough = true;
                } else if (codeIndex == 19) {
                    underline = true;
                } else if (codeIndex == 20) {
                    italic = true;
                    if (bold) {
                        GlStateManager.bindTexture(this.boldItalicTexture.getGlTextureId());
                        activeGlyphs = this.boldItalicGlyphs;
                    } else {
                        GlStateManager.bindTexture(this.italicTexture.getGlTextureId());
                        activeGlyphs = this.italicGlyphs;
                    }
                } else if (codeIndex == 21) {
                    bold = false;
                    italic = false;
                    obfuscated = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.color((float)(textColor >> 16 & 0xFF) / channelMax, (float)(textColor >> 8 & 0xFF) / channelMax, (float)(textColor & 0xFF) / channelMax, textAlpha);
                    GlStateManager.bindTexture(this.texture.getGlTextureId());
                    activeGlyphs = this.glyphs;
                }
                ++i;
                continue;
            }
            if (ch >= activeGlyphs.length || ch < '\u0000') continue;
            GL11.glBegin(4);
            this.drawChar(activeGlyphs, ch, (float)x, (float)y);
            GL11.glEnd();
            if (strikethrough) {
                this.drawLine(x, y + (double)(activeGlyphs[ch].height / 2), x + (double)activeGlyphs[ch].advanceWidth - 8.0, y + (double)(activeGlyphs[ch].height / 2), 1.0f);
            }
            if (underline) {
                this.drawLine(x, y + (double)activeGlyphs[ch].height - 2.0, x + (double)activeGlyphs[ch].advanceWidth - 8.0, y + (double)activeGlyphs[ch].height - 2.0, 1.0f);
            }
            x += (double)(activeGlyphs[ch].advanceWidth - 8 + this.charSpacing);
        }
        GL11.glHint(3155, 4352);
        GL11.glPopMatrix();
        return (float)startX + (float)this.getStringWidth(string) - 1.0f;
    }

    public float drawStringWithShadow(String string, double x, double y, int color) {
        float shadowEndX = this.drawStringInternal(string, x + 1.0, y + 0.5, color, true);
        return Math.max(shadowEndX, this.drawStringInternal(string, x, y, color, false));
    }

    public float drawCenteredStringWithShadow(String string, float x, float y, int color) {
        float shadowEndX = this.drawStringInternal(string, (double)(x - (float)(this.getStringWidth(string) / 2)) + 0.45, (double)y + 0.5, color, true);
        return this.drawString(string, x - (float)(this.getStringWidth(string) / 2), y, color);
    }

    @Override
    public void setFractionalMetrics(boolean useFractionalMetrics) {
        super.setFractionalMetrics(useFractionalMetrics);
        this.reloadFont();
    }

    public float drawCenteredString(String string, float x, float y, int color) {
        return this.drawString(string, x - (float)(this.getStringWidth(string) / 2), y, color);
    }

    @Override
    public int getStringWidth(String string) {
        if (string == null) {
            return 0;
        }
        int width = 0;
        GlyphInfo[] activeGlyphs = this.glyphs;
        boolean boldOn = false;
        boolean italicOn = false;
        int textLength = string.length();
        for (int i = 0; i < textLength; ++i) {
            char ch = string.charAt(i);
            if (String.valueOf(ch).equals("\u00a7") && i < textLength) {
                int codeIndex = "0123456789abcdefklmnor".indexOf(ch);
                if (codeIndex < 16) {
                    boldOn = false;
                    italicOn = false;
                } else if (codeIndex == 17) {
                    boldOn = true;
                    activeGlyphs = italicOn ? this.boldItalicGlyphs : this.boldGlyphs;
                } else if (codeIndex == 20) {
                    italicOn = true;
                    activeGlyphs = boldOn ? this.boldItalicGlyphs : this.italicGlyphs;
                } else if (codeIndex == 21) {
                    boldOn = false;
                    italicOn = false;
                    activeGlyphs = this.glyphs;
                }
                ++i;
            } else if (ch < activeGlyphs.length && ch >= '\u0000') {
                width += activeGlyphs[ch].advanceWidth - 8 + this.charSpacing;
            }
        }
        return width / 2;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.reloadFont();
    }

    public void drawCenteredStringWithOutline(String string, double x, double y, int textColor) {
        this.drawCenteredString(string, x - 0.5, y, 0);
        this.drawCenteredString(string, x + 0.5, y, 0);
        this.drawCenteredString(string, x, y - 0.5, 0);
        this.drawCenteredString(string, x, y + 0.5, 0);
        this.drawCenteredString(string, x, y, textColor);
    }
}

