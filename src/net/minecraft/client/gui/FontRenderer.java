package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import kawaii.config.PropertyStore;
import kawaii.util.SeededRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import optfine.Config;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements ISpectatorMenuRecipient {
   public static String obfuscationId = "CL_00000660";
   public static ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
   public boolean unicodeFlag;
   public boolean underlineStyle;
   public float blue;
   public ResourceLocation locationFontTextureBase;
   public boolean bidiFlag;
   public boolean enabled;
   public int textColor;
   public float red;
   public float scaleFactor;
   public boolean randomStyle;
   public int[] colorCode;
   public float posY;
   public ResourceLocation locationFontTexture;
   public boolean italicStyle;
   public boolean strikethroughStyle;
   public Random fontRandom;
   public float[] charWidth = new float[256];
   public byte[] glyphWidth;
   public TextureManager renderEngine;
   public float green;
   public float posX;
   public boolean boldStyle;
   public GameSettings gameSettings;
   public float alpha;
   public int FONT_HEIGHT = 9;

   public static ResourceLocation getHdFontLocation(ResourceLocation location) {
      if (!Config.isCustomFonts()) {
         return location;
      } else if (location == null) {
         return location;
      } else {
         String path = location.gHr();
         String texturePrefix = "textures/";
         String mcpatcherPrefix = "mcpatcher/";
         if (!path.startsWith(texturePrefix)) {
            return location;
         } else {
            path = path.substring(texturePrefix.length());
            path = mcpatcherPrefix + path;
            ResourceLocation hdLocation = new ResourceLocation(location.ebr(), path);
            return Config.hasResource(Config.getResourceManager(), hdLocation) ? hdLocation : location;
         }
      }
   }

   public String wrapFormattedStringToWidth(String text, int maxWidth) {
      int splitIndex = this.sizeStringToWidth(text, maxWidth);
      if (text.length() <= splitIndex) {
         return text;
      } else {
         String firstLine = text.substring(0, splitIndex);
         char splitChar = text.charAt(splitIndex);
         boolean skipSpace = splitChar == ' ' || splitChar == '\n';
         String remaining = getFormatFromString(firstLine) + text.substring(splitIndex + (skipSpace ? 1 : 0));
         return firstLine + "\n" + this.wrapFormattedStringToWidth(remaining, maxWidth);
      }
   }

   public void readFontTexture() {
      BufferedImage image;
      try {
         image = TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(this.locationFontTexture).getInputStream());
      } catch (Exception exception) {
         throw new RuntimeException(exception);
      }

      int imageWidth = image.getWidth();
      int imageHeight = image.getHeight();
      int cellWidth = imageWidth / 16;
      int cellHeight = imageHeight / 16;
      float scale = (float)imageWidth / 128.0F;
      this.scaleFactor = scale;
      int[] pixels = new int[imageWidth * imageHeight];
      image.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);

      for (int charIndex = 0; charIndex < 256; charIndex++) {
         int column = charIndex % 16;
         int row = charIndex / 16;
         int pixelWidth = 0;

         for (pixelWidth = cellWidth - 1; pixelWidth >= 0; pixelWidth--) {
            int pixelX = column * cellWidth + pixelWidth;
            boolean columnClear = true;

            for (int pixelRow = 0; pixelRow < cellHeight && columnClear; pixelRow++) {
               int rowOffset = (row * cellHeight + pixelRow) * imageWidth;
               int argb = pixels[pixelX + rowOffset];
               int pixelAlpha = argb >> 24 & 0xFF;
               if (pixelAlpha > 16) {
                  columnClear = false;
               }
            }

            if (!columnClear) {
               break;
            }
         }

         if (charIndex == 65) {
            charIndex = charIndex;
         }

         if (charIndex == 32) {
            if (cellWidth <= 8) {
               pixelWidth = (int)(2.0F * scale);
            } else {
               pixelWidth = (int)(1.5F * scale);
            }
         }

         this.charWidth[charIndex] = (float)(pixelWidth + 1) / scale + 1.0F;
      }

      this.readCustomCharWidths();
   }

   public static boolean isFormatColor(char ch) {
      return ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F';
   }

   public boolean getUnicodeFlag() {
      return this.unicodeFlag;
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.locationFontTexture = getHdFontLocation(this.locationFontTextureBase);
      int pageIndex = 0;

      while (pageIndex < unicodePageLocations.length) {
         unicodePageLocations[pageIndex] = null;
         pageIndex += 44;
         pageIndex -= 43;
      }

      this.readFontTexture();
   }

   public int renderString(String text, float x, float y, int color, boolean shadow) {
      if (text == null) {
         return 0;
      } else {
         if (this.bidiFlag) {
            text = this.bidiReorder(text);
         }

         if ((color & -67108864) == 0) {
            color |= -16777216;
         }

         if (shadow) {
            color = (color & 16579836) >> 2 | color & 0xFF000000;
         }

         this.red = (float)(color >> 16 & 0xFF) / 255.0F;
         this.blue = (float)(color >> 8 & 0xFF) / 255.0F;
         this.green = (float)(color & 0xFF) / 255.0F;
         this.alpha = (float)(color >> 24 & 0xFF) / 255.0F;
         GlStateManager.color(this.red, this.blue, this.green, this.alpha);
         this.posX = x;
         this.posY = y;
         this.renderStringAtPos(text, shadow);
         return (int)this.posX;
      }
   }

   public ResourceLocation getUnicodePageLocation(int page) {
      if (unicodePageLocations[page] == null) {
         unicodePageLocations[page] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", page));
         unicodePageLocations[page] = getHdFontLocation(unicodePageLocations[page]);
      }

      return unicodePageLocations[page];
   }

   public String bidiReorder(String text) {
      try {
         Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException shapingException) {
         return text;
      }
   }

   public int splitStringWidth(String text, int maxWidth) {
      return this.FONT_HEIGHT * this.listFormattedStringToWidth(text, maxWidth).size();
   }

   public int renderStringAligned(String text, int x, int y, int width, int color, boolean shadow) {
      if (this.bidiFlag) {
         int stringWidth = this.getStringWidth(this.bidiReorder(text));
         x = x + width - stringWidth;
      }

      return this.renderString(text, (float)x, (float)y, color, shadow);
   }

   public void loadGlyphTexture(int page) {
      this.renderEngine.bindTexture(this.getUnicodePageLocation(page));
   }

   public int getRoundedCharWidth(char ch) {
      return Math.round(this.getCharWidthFloat(ch));
   }

   public void setUnicodeFlag(boolean flag) {
      this.bidiFlag = flag;
   }

   public void renderSplitString(String text, int x, int y, int maxWidth, boolean shadow) {
      for (Object line : this.listFormattedStringToWidth(text, maxWidth)) {
         this.renderStringAligned((String)line, x, y, maxWidth, this.textColor, shadow);
         y += this.FONT_HEIGHT;
      }
   }

   public String trimStringToWidth(String text, int maxWidth, boolean reverse) {
      StringBuilder builder = new StringBuilder();
      float accumulatedWidth = 0.0F;
      int startIndex = reverse ? text.length() - 1 : 0;
      int step = reverse ? -1 : 1;
      boolean afterFormatCode = false;
      boolean boldActive = false;

      for (int index = startIndex; index >= 0 && index < text.length() && accumulatedWidth < (float)maxWidth; index += step) {
         char ch = text.charAt(index);
         float advance = this.getCharWidthFloat(ch);
         if (afterFormatCode) {
            afterFormatCode = false;
            if (ch == 'l' || ch == 'L') {
               boldActive = true;
            } else if (ch == 'r' || ch == 'R') {
               boldActive = false;
            }
         } else if (advance < 0.0F) {
            afterFormatCode = true;
         } else {
            accumulatedWidth += advance;
            if (boldActive) {
               accumulatedWidth++;
            }
         }

         if (accumulatedWidth > (float)maxWidth) {
            break;
         }

         if (reverse) {
            builder.insert(0, ch);
         } else {
            builder.append(ch);
         }
      }

      return builder.toString();
   }

   public int getStringWidth(String text) {
      if (text == null) {
         return 0;
      } else {
         float totalWidth = 0.0F;
         boolean boldActive = false;

         for (int index = 0; index < text.length(); index++) {
            char ch = text.charAt(index);
            float advance = this.getCharWidthFloat(ch);
            if (advance < 0.0F && index < text.length() - 1) {
               int shifted = index - 57;
               index = shifted + 58;
               ch = text.charAt(index);
               if (ch == 'l' || ch == 'L') {
                  boldActive = true;
               } else if (ch == 'r' || ch == 'R') {
                  boldActive = false;
               }

               advance = 0.0F;
            }

            totalWidth += advance;
            if (boldActive && advance > 0.0F) {
               totalWidth++;
            }
         }

         return (int)totalWidth;
      }
   }

   public void resetStyles() {
      this.randomStyle = false;
      this.boldStyle = false;
      this.italicStyle = false;
      this.underlineStyle = false;
      this.strikethroughStyle = false;
   }

   public void renderStringAtPos(String text, boolean shadow) {
      int index = 0;

      while (index < text.length()) {
         char ch = text.charAt(index);
         if (ch == 167 && index + 1 < text.length()) {
            int styleCode = "0123456789abcdefklmnor".indexOf(text.toLowerCase().charAt(index + 1));
            if (styleCode < 16) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               if (styleCode < 0 || styleCode > 15) {
                  styleCode = 15;
               }

               if (shadow) {
                  styleCode += 16;
               }

               int colorValue = this.colorCode[styleCode];
               this.textColor = colorValue;
               GlStateManager.color((float)(colorValue >> 16) / 255.0F, (float)(colorValue >> 8 & 0xFF) / 255.0F, (float)(colorValue & 0xFF) / 255.0F, this.alpha);
            } else if (styleCode == 16) {
               this.randomStyle = true;
            } else if (styleCode == 17) {
               this.boldStyle = true;
            } else if (styleCode == 18) {
               this.strikethroughStyle = true;
            } else if (styleCode == 19) {
               this.underlineStyle = true;
            } else if (styleCode == 20) {
               this.italicStyle = true;
            } else if (styleCode == 21) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               GlStateManager.color(this.red, this.blue, this.green, this.alpha);
            }

            index++;
         } else {
            int charIndex = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
               .indexOf(ch);
            if (this.randomStyle && charIndex != -1) {
               int roundedWidth = this.getRoundedCharWidth(ch);

               char randomChar;
               do {
                  charIndex = this.fontRandom.nextInt(256);
                  randomChar = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                     .charAt(charIndex);
               } while (roundedWidth != this.getRoundedCharWidth(randomChar));

               ch = randomChar;
            }

            float offset = this.unicodeFlag ? 0.5F : 1.0F / this.scaleFactor;
            boolean applyShadowShift = (ch == 0 || charIndex == -1 || this.unicodeFlag) && shadow;
            if (applyShadowShift) {
               this.posX -= offset;
               this.posY -= offset;
            }

            float glyphAdvance = this.renderChar(ch, this.italicStyle);
            if (applyShadowShift) {
               this.posX += offset;
               this.posY += offset;
            }

            if (this.boldStyle) {
               this.posX += offset;
               if (applyShadowShift) {
                  this.posX -= offset;
                  this.posY -= offset;
               }

               this.renderChar(ch, this.italicStyle);
               this.posX -= offset;
               if (applyShadowShift) {
                  this.posX += offset;
                  this.posY += offset;
               }

               glyphAdvance += offset;
            }

            if (this.strikethroughStyle) {
               Tessellator strikeTessellator = Tessellator.getInstance();
               WorldRenderer strikeRenderer = strikeTessellator.getWorldRenderer();
               GlStateManager.disableTexture2D();
               strikeRenderer.begin(7, DefaultVertexFormats.jXa);
               strikeRenderer.pos((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0).endVertex();
               strikeRenderer.pos((double)(this.posX + glyphAdvance), (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0).endVertex();
               strikeRenderer.pos((double)(this.posX + glyphAdvance), (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0).endVertex();
               strikeRenderer.pos((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0).endVertex();
               strikeTessellator.draw();
               GlStateManager.enableTexture2D();
            }

            if (this.underlineStyle) {
               Tessellator underlineTessellator = Tessellator.getInstance();
               WorldRenderer underlineRenderer = underlineTessellator.getWorldRenderer();
               GlStateManager.disableTexture2D();
               underlineRenderer.begin(7, DefaultVertexFormats.jXa);
               int underlineOffset = this.underlineStyle ? -1 : 0;
               underlineRenderer.pos((double)(this.posX + (float)underlineOffset), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0).endVertex();
               underlineRenderer.pos((double)(this.posX + glyphAdvance), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0).endVertex();
               underlineRenderer.pos((double)(this.posX + glyphAdvance), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0).endVertex();
               underlineRenderer.pos((double)(this.posX + (float)underlineOffset), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0).endVertex();
               underlineTessellator.draw();
               GlStateManager.enableTexture2D();
            }

            this.posX += glyphAdvance;
         }

         index += 47;
         index -= 46;
      }
   }

   public int getCharWidth(char ch) {
      int index = "0123456789abcdef".indexOf(ch);
      return index >= 0 && index < this.colorCode.length ? this.colorCode[index] : 16777215;
   }

   public FontRenderer(GameSettings settings, ResourceLocation location, TextureManager textureManager, boolean unicode) {
      this.fontRandom = new SeededRandom();
      this.glyphWidth = new byte[65536];
      this.colorCode = new int[32];
      this.enabled = true;
      this.scaleFactor = 1.0F;
      this.gameSettings = settings;
      this.locationFontTextureBase = location;
      this.locationFontTexture = location;
      this.renderEngine = textureManager;
      this.unicodeFlag = unicode;
      this.locationFontTexture = getHdFontLocation(this.locationFontTextureBase);
      textureManager.bindTexture(this.locationFontTexture);

      for (int colorIndex = 0; colorIndex < 32; colorIndex++) {
         int brightness = (colorIndex >> 3 & 1) * 85;
         int redValue = (colorIndex >> 2 & 1) * 170 + brightness;
         int greenValue = (colorIndex >> 1 & 1) * 170 + brightness;
         int blueValue = (colorIndex >> 0 & 1) * 170 + brightness;
         if (colorIndex == 6) {
            redValue += 85;
         }

         if (settings.anaglyph) {
            int luminance = (redValue * 30 + greenValue * 59 + blueValue * 11) / 100;
            int anaglyphGreen = (redValue * 30 + greenValue * 70) / 100;
            int anaglyphBlue = (redValue * 30 + blueValue * 70) / 100;
            redValue = luminance;
            greenValue = anaglyphGreen;
            blueValue = anaglyphBlue;
         }

         if (colorIndex >= 16) {
            redValue /= 4;
            greenValue /= 4;
            blueValue /= 4;
         }

         this.colorCode[colorIndex] = (redValue & 0xFF) << 16 | (greenValue & 0xFF) << 8 | blueValue & 0xFF;
      }

      this.readGlyphSizes();
   }

   public boolean getBidiFlag() {
      return this.bidiFlag;
   }

   public static String getFormatFromString(String text) {
      String result = "";
      int index = -1;
      int textLength = text.length();

      while ((index = text.indexOf(167, index + 1)) != -1) {
         if (index < textLength - 1) {
            char ch = text.charAt(index + 1);
            if (isFormatColor(ch)) {
               result = "§" + ch;
            } else if (isFormatSpecial(ch)) {
               result = result + "§" + ch;
            }
         }
      }

      return result;
   }

   public float renderDefaultChar(int charIndex, boolean italic) {
      float glyphSize = 7.99F;
      float textureSize = 128.0F;
      int texX = charIndex % 16 * 8;
      int texY = charIndex / 16 * 8;
      int italicOffset = italic ? 1 : 0;
      this.renderEngine.bindTexture(this.locationFontTexture);
      float glyphAdvance = this.charWidth[charIndex];
      GL11.glBegin(5);
      GL11.glTexCoord2f((float)texX / textureSize, (float)texY / textureSize);
      GL11.glVertex3f(this.posX + (float)italicOffset, this.posY, 0.0F);
      GL11.glTexCoord2f((float)texX / textureSize, ((float)texY + glyphSize) / textureSize);
      GL11.glVertex3f(this.posX - (float)italicOffset, this.posY + glyphSize, 0.0F);
      GL11.glTexCoord2f(((float)texX + glyphSize - 1.0F) / textureSize, (float)texY / textureSize);
      GL11.glVertex3f(this.posX + glyphSize - 1.0F + (float)italicOffset, this.posY, 0.0F);
      GL11.glTexCoord2f(((float)texX + glyphSize - 1.0F) / textureSize, ((float)texY + glyphSize) / textureSize);
      GL11.glVertex3f(this.posX + glyphSize - 1.0F - (float)italicOffset, this.posY + glyphSize, 0.0F);
      GL11.glEnd();
      return glyphAdvance;
   }

   public int drawString(String text, float x, float y, int color, boolean shadow) {
      GlStateManager.enableAlpha();
      this.resetStyles();
      int renderedWidth;
      if (shadow) {
         renderedWidth = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
         renderedWidth = Math.max(renderedWidth, this.renderString(text, x, y, color, false));
      } else {
         renderedWidth = this.renderString(text, x, y, color, false);
      }

      return renderedWidth;
   }

   public void drawSplitString(String text, int x, int y, int maxWidth, int color) {
      this.resetStyles();
      this.textColor = color;
      text = this.trimStringNewline(text);
      this.renderSplitString(text, x, y, maxWidth, false);
   }

   public int sizeStringToWidth(String text, int maxWidth) {
      int textLength = text.length();
      float accumulatedWidth = 0.0F;
      int index = 0;
      int lastSpaceIndex = -1;
      boolean boldActive = false;

      while (index < textLength) {
         char ch = text.charAt(index);
         switch (ch) {
            case '\n':
               index--;
               break;
            case ' ':
               lastSpaceIndex = index;
            default:
               accumulatedWidth += this.getCharWidthFloat(ch);
               if (boldActive) {
                  accumulatedWidth++;
               }
               break;
            case '§':
               if (index < textLength - 1) {
                  int skipBase = index + 41;
                  index = skipBase - 40;
                  char styleChar = text.charAt(index);
                  if (styleChar == 'l' || styleChar == 'L') {
                     boldActive = true;
                  } else if (styleChar == 'r' || styleChar == 'R' || isFormatColor(styleChar)) {
                     boldActive = false;
                  }
               }
         }

         if (ch == '\n') {
            int nextBase = index + 36;
            index = nextBase - 35;
            lastSpaceIndex = index;
            break;
         }

         if (accumulatedWidth > (float)maxWidth) {
            break;
         }

         index += 23;
         index -= 22;
      }

      return index != textLength && lastSpaceIndex != -1 && lastSpaceIndex < index ? lastSpaceIndex : index;
   }

   public float renderChar(char ch, boolean italic) {
      if (ch == ' ') {
         return this.charWidth[ch];
      } else {
         int charIndex = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
            .indexOf(ch);
         return charIndex != -1 && !this.unicodeFlag ? this.renderDefaultChar(charIndex, italic) : this.renderUnicodeChar(ch, italic);
      }
   }

   public String trimStringToWidth(String text, int maxWidth) {
      return this.trimStringToWidth(text, maxWidth, false);
   }

   public float getCharWidthFloat(char ch) {
      if (ch == 167) {
         return -1.0F;
      } else if (ch == ' ') {
         return this.charWidth[32];
      } else {
         int charIndex = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
            .indexOf(ch);
         if (ch > 0 && charIndex != -1 && !this.unicodeFlag) {
            return this.charWidth[charIndex];
         } else if (this.glyphWidth[ch] != 0) {
            int glyphStart = this.glyphWidth[ch] >>> 4;
            int glyphEnd = this.glyphWidth[ch] & 15;
            if (glyphEnd > 7) {
               glyphEnd = 15;
               glyphStart = 0;
            }

            glyphEnd++;
            return (float)((glyphEnd - glyphStart) / 2 + 1);
         } else {
            return 0.0F;
         }
      }
   }

   public void setBidiFlag(boolean flag) {
      this.unicodeFlag = flag;
   }

   public int drawString(String text, int x, int y, int color) {
      return !this.enabled ? 0 : this.drawString(text, (float)x, (float)y, color, false);
   }

   public String trimStringNewline(String text) {
      while (text != null && text.endsWith("\n")) {
         text = text.substring(0, text.length() - 1);
      }

      return text;
   }

   public int drawStringWithShadow(String text, float x, float y, int color) {
      return this.drawString(text, x, y, color, true);
   }

   public void readCustomCharWidths() {
      String path = this.locationFontTexture.gHr();
      String extension = ".png";
      if (path.endsWith(extension)) {
         String propertiesPath = path.substring(0, path.length() - extension.length()) + ".properties";

         try {
            ResourceLocation propertiesLocation = new ResourceLocation(this.locationFontTexture.ebr(), propertiesPath);
            InputStream propertiesStream = Config.getResourceStream(Config.getResourceManager(), propertiesLocation);
            if (propertiesStream == null) {
               return;
            }

            Config.log("Loading " + propertiesPath);
            PropertyStore store = new PropertyStore();
            store.load(propertiesStream);

            for (Object rawKey : store.keySet()) {
               String keyString = (String)rawKey;
               String widthPrefix = "width.";
               if (keyString.startsWith(widthPrefix)) {
                  String codeText = keyString.substring(widthPrefix.length());
                  int charCode = Config.parseInt(codeText, -1);
                  if (charCode >= 0 && charCode < this.charWidth.length) {
                     String valueString = store.getProperty(keyString);
                     float parsedWidth = Config.parseFloat(valueString, -1.0F);
                     if (parsedWidth >= 0.0F) {
                        this.charWidth[charCode] = parsedWidth;
                     }
                  }
               }
            }
         } catch (FileNotFoundException notFound) {
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      }
   }

   public static boolean isFormatSpecial(char ch) {
      return ch >= 'k' && ch <= 'o' || ch >= 'K' && ch <= 'O' || ch == 'r' || ch == 'R';
   }

   public List<String> listFormattedStringToWidth(String text, int maxWidth) {
      return Arrays.asList(this.wrapFormattedStringToWidth(text, maxWidth).split("\n"));
   }

   public void readGlyphSizes() {
      InputStream stream = null;

      try {
         stream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/glyph_sizes.bin")).getInputStream();
         stream.read(this.glyphWidth);
      } catch (IOException ioException) {
         throw new RuntimeException(ioException);
      } finally {
         IOUtils.closeQuietly(stream);
      }
   }

   public float renderUnicodeChar(char ch, boolean italic) {
      float textureSize = 256.0F;
      if (this.glyphWidth[ch] == 0) {
         return 0.0F;
      } else {
         int page = ch / 256;
         this.loadGlyphTexture(page);
         int glyphStart = this.glyphWidth[ch] >>> 4;
         int glyphEnd = this.glyphWidth[ch] & 15;
         float glyphStartF = (float)glyphStart;
         float glyphEndF = (float)(glyphEnd + 1);
         float texStartX = (float)(ch % 16 * 16) + glyphStartF;
         float texStartY = (float)((ch & 255) / 16 * 16);
         float glyphDrawWidth = glyphEndF - glyphStartF - 0.02F;
         float italicOffset = italic ? 1.0F : 0.0F;
         GL11.glBegin(5);
         GL11.glTexCoord2f(texStartX / textureSize, texStartY / textureSize);
         GL11.glVertex3f(this.posX + italicOffset, this.posY, 0.0F);
         GL11.glTexCoord2f(texStartX / textureSize, (texStartY + 15.98F) / textureSize);
         GL11.glVertex3f(this.posX - italicOffset, this.posY + 7.99F, 0.0F);
         GL11.glTexCoord2f((texStartX + glyphDrawWidth) / textureSize, texStartY / textureSize);
         GL11.glVertex3f(this.posX + glyphDrawWidth / 2.0F + italicOffset, this.posY, 0.0F);
         GL11.glTexCoord2f((texStartX + glyphDrawWidth) / textureSize, (texStartY + 15.98F) / textureSize);
         GL11.glVertex3f(this.posX + glyphDrawWidth / 2.0F - italicOffset, this.posY + 7.99F, 0.0F);
         GL11.glEnd();
         return (glyphEndF - glyphStartF) / 2.0F + 1.0F;
      }
   }
}
