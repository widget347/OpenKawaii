/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import kawaii.render.ImageCanvas;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture
extends AbstractTexture {
    public List<String> layeredTextureNames;
    public static Logger logger = LogManager.getLogger();

    public LayeredTexture(String ... layerNames) {
        this.layeredTextureNames = Lists.newArrayList(layerNames);
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) {
        this.wio();
        ImageCanvas imageCanvas = null;
        try {
            for (String layerName : this.layeredTextureNames) {
                if (layerName == null) continue;
                InputStream inputStream = resourceManager.getResource(new ResourceLocation(layerName)).getInputStream();
                BufferedImage bufferedImage = TextureUtil.readBufferedImage(inputStream);
                if (imageCanvas == null) {
                    imageCanvas = new ImageCanvas(bufferedImage.getWidth(), bufferedImage.getHeight(), 2);
                }
                imageCanvas.getImageGraphics().drawImage(bufferedImage, 0, 0, null);
            }
        }
        catch (IOException exception) {
            logger.error("Couldn't load layered image", (Throwable)exception);
            return;
        }
        TextureUtil.uploadTextureImage(this.getGlTextureId(), imageCanvas);
    }
}

