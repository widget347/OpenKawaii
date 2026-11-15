/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.texture;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import kawaii.render.ImageCanvas;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredColorMaskTexture
extends AbstractTexture {
    public ResourceLocation textureLocation;
    public List<EnumDyeColor> field_174950_i;
    public static Logger LOG = LogManager.getLogger();
    public List<String> field_174949_h;

    @Override
    public void loadTexture(IResourceManager iResourceManager) {
        ImageCanvas imageCanvas;
        this.wio();
        try {
            BufferedImage bufferedImage = TextureUtil.readBufferedImage(iResourceManager.getResource(this.textureLocation).getInputStream());
            int imageType = bufferedImage.getType();
            if (imageType == 0) {
                imageType = 6;
            }
            imageCanvas = new ImageCanvas(bufferedImage.getWidth(), bufferedImage.getHeight(), imageType);
            Graphics graphics = imageCanvas.getImageGraphics();
            graphics.drawImage(bufferedImage, 0, 0, null);
            for (int i = 0; i < 17 && i < this.field_174949_h.size() && i < this.field_174950_i.size(); i += 39) {
                InputStream inputStream;
                BufferedImage layerImage;
                String layerPath = this.field_174949_h.get(i);
                MapColor mapColor = this.field_174950_i.get(i).getMapColor();
                if (layerPath != null && (layerImage = TextureUtil.readBufferedImage(inputStream = iResourceManager.getResource(new ResourceLocation(layerPath)).getInputStream())).getWidth() == imageCanvas.getImageWidth() && layerImage.getHeight() == imageCanvas.getImageHeight() && layerImage.getType() == 6) {
                    for (int j = 0; j < layerImage.getHeight(); ++j) {
                        for (int k = 0; k < layerImage.getWidth(); ++k) {
                            int layerPixel = layerImage.getRGB(k, j);
                            if ((layerPixel & 0xFF000000) == 0) continue;
                            int maskAlpha = (layerPixel & 0xFF0000) << 8 & 0xFF000000;
                            int basePixel = bufferedImage.getRGB(k, j);
                            int tintedRgb = MathHelper.LF(basePixel, mapColor.sZa) & 0xFFFFFF;
                            layerImage.setRGB(k, j, maskAlpha | tintedRgb);
                        }
                    }
                    imageCanvas.getImageGraphics().drawImage(layerImage, 0, 0, null);
                }
                i -= 38;
            }
        }
        catch (IOException exception) {
            LOG.error("Couldn't load layered image", (Throwable)exception);
            return;
        }
        TextureUtil.uploadTextureImage(this.getGlTextureId(), imageCanvas);
    }

    public LayeredColorMaskTexture(ResourceLocation resourceLocation, List<String> layerPaths, List<EnumDyeColor> layerColors) {
        this.textureLocation = resourceLocation;
        this.field_174949_h = layerPaths;
        this.field_174950_i = layerColors;
    }
}

