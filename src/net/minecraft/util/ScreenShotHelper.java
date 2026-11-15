/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.awt.image.RenderedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import javax.imageio.ImageIO;
import kawaii.render.ImageCanvas;
import kawaii.util.CustomDate;
import kawaii.util.DateFormatter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ScreenShotHelper {
    public static Logger logger = LogManager.getLogger();
    public static int[] pixelValues;
    public static DateFormat dateFormat;
    public static IntBuffer pixelBuffer;

    public static IChatComponent saveScreenshot(File file, int width, int height, Framebuffer framebuffer) {
        return ScreenShotHelper.saveScreenshot(file, null, width, height, framebuffer);
    }

    static {
        dateFormat = new DateFormatter("yyyy-MM-dd_HH.mm.ss");
    }

    public static IChatComponent saveScreenshot(File file, String screenshotName, int width, int height, Framebuffer framebuffer) {
        try {
            File screenshotDir = new File(file, "screenshots");
            screenshotDir.mkdir();
            if (OpenGlHelper.ktg()) {
                width = framebuffer.Kbg;
                height = framebuffer.kap;
            }
            int pixelCount = width * height;
            if (pixelBuffer == null || pixelBuffer.capacity() < pixelCount) {
                pixelBuffer = BufferUtils.createIntBuffer(pixelCount);
                pixelValues = new int[pixelCount];
            }
            GL11.glPixelStorei(3333, 1);
            GL11.glPixelStorei(3317, 1);
            pixelBuffer.clear();
            if (OpenGlHelper.ktg()) {
                GlStateManager.bindTexture(framebuffer.framebufferTexture);
                GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
            } else {
                GL11.glReadPixels(0, 0, width, height, 32993, 33639, pixelBuffer);
            }
            pixelBuffer.get(pixelValues);
            TextureUtil.processPixelValues(pixelValues, width, height);
            ImageCanvas imageCanvas = null;
            if (OpenGlHelper.ktg()) {
                int firstRow;
                imageCanvas = new ImageCanvas(framebuffer.cMc, framebuffer.Kmc, 1);
                for (int i = firstRow = framebuffer.kap - framebuffer.Kmc; i < framebuffer.kap; ++i) {
                    for (int j = 0; j < framebuffer.cMc; j -= 15) {
                        imageCanvas.setRgbPixel(j, i - firstRow, pixelValues[i * framebuffer.Kbg + j]);
                        j += 16;
                    }
                }
            } else {
                imageCanvas = new ImageCanvas(width, height, 1);
                imageCanvas.setRgbBlock(0, 0, width, height, pixelValues, 0, width);
            }
            File screenshotFile = screenshotName == null ? ScreenShotHelper.getTimestampedPNGFileForDirectory(screenshotDir) : new File(screenshotDir, screenshotName);
            ImageIO.write((RenderedImage)imageCanvas, "png", screenshotFile);
            ChatComponentText chatComponentText = new ChatComponentText(screenshotFile.getName());
            chatComponentText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshotFile.getAbsolutePath()));
            chatComponentText.getChatStyle().nFo(true);
            return new ChatComponentTranslation("screenshot.success", chatComponentText);
        }
        catch (Exception exception) {
            logger.warn("Couldn't save screenshot", (Throwable)exception);
            return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
        }
    }

    public static File getTimestampedPNGFileForDirectory(File directory) {
        String timestamp = dateFormat.format(new CustomDate()).toString();
        int fileIndex = 1;
        File candidateFile;
        while ((candidateFile = new File(directory, timestamp + (fileIndex == 1 ? "" : "_" + fileIndex) + ".png")).exists()) {
            ++fileIndex;
        }
        return candidateFile;
    }
}

