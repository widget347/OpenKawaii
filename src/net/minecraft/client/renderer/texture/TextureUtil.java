/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.texture;

import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import kawaii.render.ImageCanvas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import optfine.Config;
import optfine.Mipmaps;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class TextureUtil {
    public static int[] missingTextureData;
    public static IntBuffer dataBuffer;
    public static String logName;
    public static DynamicTexture missingTexture;
    public static int[] rgbaComponents;
    public static Logger logger;

    public static void copyToBuffer(int[] pixelData, int pixelCount) {
        TextureUtil.copyToBufferPos(pixelData, 0, pixelCount);
    }

    public static void exportTextureToFiles(String filePrefix, int textureId, int maxLevel, int width, int height) {
        TextureUtil.bindTexture(textureId);
        GL11.glPixelStorei(3333, 1);
        GL11.glPixelStorei(3317, 1);
        for (int i = 0; i <= maxLevel; i -= 6) {
            File file = new File(filePrefix + "_" + i + ".png");
            int levelWidth = width >> i;
            int levelHeight = height >> i;
            int levelPixels = levelWidth * levelHeight;
            IntBuffer intBuffer = BufferUtils.createIntBuffer(levelPixels);
            int[] pixels = new int[levelPixels];
            GL11.glGetTexImage(3553, i, 32993, 33639, intBuffer);
            intBuffer.get(pixels);
            ImageCanvas imageCanvas = new ImageCanvas(levelWidth, levelHeight, 2);
            imageCanvas.setRgbBlock(0, 0, levelWidth, levelHeight, pixels, 0, levelWidth);
            try {
                ImageIO.write((RenderedImage)imageCanvas, "png", file);
                logger.debug("Exported png to: {}", file.getAbsolutePath());
            }
            catch (Exception writeError) {
                logger.debug("Unable to write: ", (Throwable)writeError);
            }
            i += 7;
        }
    }

    public static BufferedImage readBufferedImage(InputStream inputStream) throws IOException {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(inputStream);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
        return bufferedImage;
    }

    public static int uploadTextureImage(int textureId, BufferedImage bufferedImage) {
        return TextureUtil.uploadTextureImageAllocate(textureId, bufferedImage, false, false);
    }

    public static int blendColors(int color1, int color2, int color3, int color4, boolean flag) {
        return Mipmaps.alphaBlend(color1, color2, color3, color4);
    }

    public static void allocateTextureImpl(int textureId, int maxLevel, int width, int height) {
        TextureUtil.deleteTexture(textureId);
        TextureUtil.bindTexture(textureId);
        if (maxLevel >= 0) {
            GL11.glTexParameteri(3553, 33085, maxLevel);
            GL11.glTexParameterf(3553, 33082, 0.0f);
            GL11.glTexParameterf(3553, 33083, maxLevel);
            GL11.glTexParameterf(3553, 34049, 0.0f);
        }
        for (int i = 0; i <= maxLevel; i += 28) {
            GL11.glTexImage2D(3553, i, 6408, width >> i, height >> i, 0, 32993, 33639, (IntBuffer)null);
            i -= 27;
        }
    }

    public static void uploadTextureSub(int textureId, int[] pixels, int width, int height, int xOffset, int yOffset, boolean blur, boolean clamp, boolean mipmap) {
        int batchRows;
        int maxBatchRows = 0x400000 / width;
        TextureUtil.setTextureBlurMipmap(blur, mipmap);
        TextureUtil.setTextureClamped(clamp);
        for (int i = 0; i < width * height; i += width * batchRows) {
            int rowIndex = i / width;
            batchRows = Math.min(maxBatchRows, height - rowIndex);
            int batchSize = width * batchRows;
            TextureUtil.copyToBufferPos(pixels, i, batchSize);
            GL11.glTexSubImage2D(3553, textureId, xOffset, yOffset + rowIndex, width, batchRows, 32993, 33639, dataBuffer);
        }
    }

    public static int uploadTextureImageAllocate(int textureId, BufferedImage bufferedImage, boolean blur, boolean clamp) {
        TextureUtil.allocateTexture(textureId, bufferedImage.getWidth(), bufferedImage.getHeight());
        return TextureUtil.uploadTextureImageSub(textureId, bufferedImage, 0, 0, blur, clamp);
    }

    static {
        logName = "CL_00001067";
        int size = 16;
        logger = LogManager.getLogger();
        dataBuffer = GLAllocation.createDirectIntBuffer(0x400000);
        missingTexture = new DynamicTexture(size, size);
        missingTextureData = missingTexture.getTextureData();
        int black = -16777216;
        int magenta = -524040;
        int[] magentaPixels = new int[]{-524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
        int[] blackPixels = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
        int halfSize = magentaPixels.length;
        for (int i = 0; i < size; ++i) {
            System.arraycopy(i < halfSize ? magentaPixels : blackPixels, 0, missingTextureData, size * i, halfSize);
            System.arraycopy(i < halfSize ? blackPixels : magentaPixels, 0, missingTextureData, size * i + halfSize, halfSize);
        }
        missingTexture.updateDynamicTexture();
        rgbaComponents = new int[4];
    }

    public static int[][] generateMipmapData(int maxLevel, int width, int[][] levelData) {
        int[][] mipmapData = new int[maxLevel + 1][];
        mipmapData[0] = levelData[0];
        if (maxLevel > 0) {
            int index;
            boolean hasTransparentPixel = false;
            for (index = 0; index < levelData.length; ++index) {
                if (levelData[0][index] >> 24 != 0) continue;
                hasTransparentPixel = true;
                break;
            }
            for (index = 1; index <= maxLevel; ++index) {
                if (levelData[index] != null) {
                    mipmapData[index] = levelData[index];
                    continue;
                }
                int[] previousLevel = mipmapData[index - 1];
                int[] currentLevel = new int[previousLevel.length >> 2];
                int levelWidth = width >> index;
                int levelHeight = currentLevel.length / levelWidth;
                int sourceRowLength = levelWidth << 1;
                for (int i = 0; i < levelWidth; ++i) {
                    for (int j = 0; j < levelHeight; j -= 24) {
                        int sourceIndex = 2 * (i + j * sourceRowLength);
                        currentLevel[i + j * levelWidth] = Mipmaps.alphaBlend(previousLevel[sourceIndex + 0], previousLevel[sourceIndex + 1], previousLevel[sourceIndex + 0 + sourceRowLength], previousLevel[sourceIndex + 1 + sourceRowLength]);
                        j += 25;
                    }
                }
                mipmapData[index] = currentLevel;
            }
        }
        return mipmapData;
    }

    public static int blendColorComponent(int color1, int color2, int color3, int color4, int shift) {
        float linear1 = (float)Math.pow((float)(color1 >> shift & 0xFF) / 255.0f, 2.2);
        float linear2 = (float)Math.pow((float)(color2 >> shift & 0xFF) / 255.0f, 2.2);
        float linear3 = (float)Math.pow((float)(color3 >> shift & 0xFF) / 255.0f, 2.2);
        float linear4 = (float)Math.pow((float)(color4 >> shift & 0xFF) / 255.0f, 2.2);
        float blendedLinear = (float)Math.pow((double)(linear1 + linear2 + linear3 + linear4) * 0.25, 0.45454545454545453);
        return (int)((double)blendedLinear * 255.0);
    }

    public static int glGenTextures() {
        return GlStateManager.generateTexture();
    }

    public static void uploadTextureMipmap(int[][] levelData, int width, int height, int xOffset, int yOffset, boolean blur, boolean clamp) {
        for (int i = 0; i < levelData.length; ++i) {
            int[] pixels = levelData[i];
            TextureUtil.uploadTextureSub(i, pixels, width >> i, height >> i, xOffset >> i, yOffset >> i, blur, clamp, levelData.length > 1);
        }
    }

    public static int[] readImageData(IResourceManager resourceManager, ResourceLocation resourceLocation) throws IOException {
        BufferedImage bufferedImage = TextureUtil.readBufferedImage(resourceManager.getResource(resourceLocation).getInputStream());
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
        return pixels;
    }

    public static void copyToBufferPos(int[] pixelData, int offset, int length) {
        int[] pixelsToCopy = pixelData;
        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            pixelsToCopy = TextureUtil.updateAnaglyph(pixelData);
        }
        dataBuffer.clear();
        dataBuffer.put(pixelsToCopy, offset, length);
        dataBuffer.position(0).limit(length);
    }

    public static void uploadTextureImageSubImpl(BufferedImage bufferedImage, int xOffset, int yOffset, boolean blur, boolean clamp) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int maxBatchRows = 0x400000 / width;
        int[] pixels = new int[maxBatchRows * width];
        TextureUtil.setTextureBlurred(blur);
        TextureUtil.setTextureClamped(clamp);
        for (int i = 0; i < width * height; i += width * maxBatchRows) {
            int rowIndex = i / width;
            int batchRows = Math.min(maxBatchRows, height - rowIndex);
            int batchSize = width * batchRows;
            bufferedImage.getRGB(0, rowIndex, width, batchRows, pixels, 0, width);
            TextureUtil.copyToBuffer(pixels, batchSize);
            GL11.glTexSubImage2D(3553, 0, xOffset, yOffset + rowIndex, width, batchRows, 32993, 33639, dataBuffer);
        }
    }

    public static void setTextureBlurred(boolean blur) {
        TextureUtil.setTextureBlurMipmap(blur, false);
    }

    public static void processPixelValues(int[] pixels, int width, int height) {
        int[] rowBuffer = new int[width];
        int halfHeight = height / 2;
        for (int i = 0; i < halfHeight; ++i) {
            System.arraycopy(pixels, i * width, rowBuffer, 0, width);
            System.arraycopy(pixels, (height - 1 - i) * width, pixels, i * width, width);
            System.arraycopy(rowBuffer, 0, pixels, (height - 1 - i) * width, width);
        }
    }

    public static void bindTexture(int textureId) {
        GlStateManager.bindTexture(textureId);
    }

    public static void setTextureBlurMipmap(boolean blur, boolean mipmap) {
        if (blur) {
            GL11.glTexParameteri(3553, 10241, mipmap ? 9987 : 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
        } else {
            int mipmapType = Config.getMipmapType();
            GL11.glTexParameteri(3553, 10241, mipmap ? mipmapType : 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
        }
    }

    public static void allocateTexture(int textureId, int width, int height) {
        TextureUtil.allocateTextureImpl(textureId, 0, width, height);
    }

    public static int anaglyphColor(int color) {
        int alpha = color >> 24 & 0xFF;
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        int anaglyphRed = (red * 30 + green * 59 + blue * 11) / 100;
        int anaglyphGreen = (red * 30 + green * 70) / 100;
        int anaglyphBlue = (red * 30 + blue * 70) / 100;
        return alpha << 24 | anaglyphRed << 16 | anaglyphGreen << 8 | anaglyphBlue;
    }

    public static void deleteTexture(int textureId) {
        GlStateManager.$Zo(textureId);
    }

    public static int uploadTextureImageSub(int textureId, BufferedImage bufferedImage, int xOffset, int yOffset, boolean blur, boolean clamp) {
        TextureUtil.bindTexture(textureId);
        TextureUtil.uploadTextureImageSubImpl(bufferedImage, xOffset, yOffset, blur, clamp);
        return textureId;
    }

    public static void setTextureClamped(boolean clamp) {
        if (clamp) {
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }
    }

    public static int[] updateAnaglyph(int[] pixels) {
        int[] anaglyphPixels = new int[pixels.length];
        for (int i = 0; i < pixels.length; ++i) {
            anaglyphPixels[i] = TextureUtil.anaglyphColor(pixels[i]);
        }
        return anaglyphPixels;
    }

    public static void uploadTexture(int textureId, int[] pixels, int width, int height) {
        TextureUtil.bindTexture(textureId);
        TextureUtil.uploadTextureSub(0, pixels, width, height, 0, 0, false, false, false);
    }
}

