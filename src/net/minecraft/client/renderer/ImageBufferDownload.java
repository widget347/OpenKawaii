/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import kawaii.render.ImageCanvas;
import net.minecraft.client.renderer.IImageBuffer;

public class ImageBufferDownload
implements IImageBuffer {
    public int[] imageData;
    public int imageWidth;
    public static String obfuscationId = "CL_00000956";
    public int imageHeight;

    public void setAreaOpaque(int minX, int minY, int maxX, int maxY) {
        for (int i = minX; i < maxX; ++i) {
            for (int j = minY; j < maxY; ++j) {
                int index = i + j * this.imageWidth;
                this.imageData[index] = this.imageData[index] | 0xFF000000;
            }
        }
    }

    @Override
    public void skinAvailable() {
    }

    @Override
    public BufferedImage parseUserSkin(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return null;
        }
        this.imageWidth = 64;
        this.imageHeight = 64;
        int sourceWidth = bufferedImage.getWidth();
        int sourceHeight = bufferedImage.getHeight();
        int scale = 1;
        while (this.imageWidth < sourceWidth || this.imageHeight < sourceHeight) {
            this.imageWidth *= 2;
            this.imageHeight *= 2;
            scale *= 2;
        }
        ImageCanvas imageCanvas = new ImageCanvas(this.imageWidth, this.imageHeight, 2);
        Graphics graphics = imageCanvas.getImageGraphics();
        graphics.drawImage(bufferedImage, 0, 0, null);
        if (bufferedImage.getHeight() == 32 * scale) {
            graphics.drawImage(imageCanvas, 24 * scale, 48 * scale, 20 * scale, 52 * scale, 4 * scale, 16 * scale, 8 * scale, 20 * scale, null);
            graphics.drawImage(imageCanvas, 28 * scale, 48 * scale, 24 * scale, 52 * scale, 8 * scale, 16 * scale, 12 * scale, 20 * scale, null);
            graphics.drawImage(imageCanvas, 20 * scale, 52 * scale, 16 * scale, 64 * scale, 8 * scale, 20 * scale, 12 * scale, 32 * scale, null);
            graphics.drawImage(imageCanvas, 24 * scale, 52 * scale, 20 * scale, 64 * scale, 4 * scale, 20 * scale, 8 * scale, 32 * scale, null);
            graphics.drawImage(imageCanvas, 28 * scale, 52 * scale, 24 * scale, 64 * scale, 0 * scale, 20 * scale, 4 * scale, 32 * scale, null);
            graphics.drawImage(imageCanvas, 32 * scale, 52 * scale, 28 * scale, 64 * scale, 12 * scale, 20 * scale, 16 * scale, 32 * scale, null);
            graphics.drawImage(imageCanvas, 40 * scale, 48 * scale, 36 * scale, 52 * scale, 44 * scale, 16 * scale, 48 * scale, 20 * scale, null);
            graphics.drawImage(imageCanvas, 44 * scale, 48 * scale, 40 * scale, 52 * scale, 48 * scale, 16 * scale, 52 * scale, 20 * scale, null);
            graphics.drawImage(imageCanvas, 36 * scale, 52 * scale, 32 * scale, 64 * scale, 48 * scale, 20 * scale, 52 * scale, 32 * scale, null);
            graphics.drawImage(imageCanvas, 40 * scale, 52 * scale, 36 * scale, 64 * scale, 44 * scale, 20 * scale, 48 * scale, 32 * scale, null);
            graphics.drawImage(imageCanvas, 44 * scale, 52 * scale, 40 * scale, 64 * scale, 40 * scale, 20 * scale, 44 * scale, 32 * scale, null);
            graphics.drawImage(imageCanvas, 48 * scale, 52 * scale, 44 * scale, 64 * scale, 52 * scale, 20 * scale, 56 * scale, 32 * scale, null);
        }
        graphics.dispose();
        this.imageData = ((DataBufferInt)imageCanvas.getImageRaster().getDataBuffer()).getData();
        this.setAreaOpaque(0, 0, 32 * scale, 16 * scale);
        this.setAreaTransparent(32 * scale, 0, 64 * scale, 32 * scale);
        this.setAreaOpaque(0, 16 * scale, 64 * scale, 32 * scale);
        this.setAreaTransparent(0, 32 * scale, 16 * scale, 48 * scale);
        this.setAreaTransparent(16 * scale, 32 * scale, 40 * scale, 48 * scale);
        this.setAreaTransparent(40 * scale, 32 * scale, 56 * scale, 48 * scale);
        this.setAreaTransparent(0, 48 * scale, 16 * scale, 64 * scale);
        this.setAreaOpaque(16 * scale, 48 * scale, 48 * scale, 64 * scale);
        this.setAreaTransparent(48 * scale, 48 * scale, 64 * scale, 64 * scale);
        return imageCanvas;
    }

    public boolean hasTransparency(int minX, int minY, int maxX, int maxY) {
        for (int i = minX; i < maxX; ++i) {
            for (int j = minY; j < maxY; ++j) {
                int argb = this.imageData[i + j * this.imageWidth];
                if ((argb >> 24 & 0xFF) >= 128) continue;
                return true;
            }
        }
        return false;
    }

    public void setAreaTransparent(int minX, int minY, int maxX, int maxY) {
        if (!this.hasTransparency(minX, minY, maxX, maxY)) {
            for (int i = minX; i < maxX; ++i) {
                for (int j = minY; j < maxY; ++j) {
                    int index = i + j * this.imageWidth;
                    this.imageData[index] = this.imageData[index] & 0xFFFFFF;
                }
            }
        }
    }
}

