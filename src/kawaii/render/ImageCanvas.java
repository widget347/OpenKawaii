/*
 * Decompiled with CFR 0.152.
 */
package kawaii.render;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ImageCanvas
extends BufferedImage {
    public int getImageHeight() {
        return super.getHeight();
    }

    public void setRgbBlock(int startX, int startY, int blockWidth, int blockHeight, int[] rgbArray, int offset, int scanSize) {
        super.setRGB(startX, startY, blockWidth, blockHeight, rgbArray, offset, scanSize);
    }

    public WritableRaster getImageRaster() {
        return super.getRaster();
    }

    public int getImageWidth() {
        return super.getWidth();
    }

    public Graphics getImageGraphics() {
        return super.getGraphics();
    }

    public ImageCanvas(int canvasWidth, int canvasHeight, int canvasImageType) {
        super(canvasWidth, canvasHeight, canvasImageType);
    }

    public void setRgbPixel(int pixelX, int pixelY, int rgb) {
        super.setRGB(pixelX, pixelY, rgb);
    }
}

