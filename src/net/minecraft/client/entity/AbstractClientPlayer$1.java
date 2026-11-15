/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.entity;

import java.awt.image.BufferedImage;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class AbstractClientPlayer$1
implements IImageBuffer {
    public ResourceLocation capeLocation;
    public ImageBufferDownload imageBufferDownload;
    public AbstractClientPlayer this$0;

    @Override
    public BufferedImage parseUserSkin(BufferedImage bufferedImage) {
        return AbstractClientPlayer.parseUserSkin(this.this$0, bufferedImage);
    }

    @Override
    public void skinAvailable() {
        AbstractClientPlayer.setLocationOfCape(this.this$0, this.capeLocation);
    }

    public AbstractClientPlayer$1(AbstractClientPlayer clientPlayer, ResourceLocation capeTexture) {
        this.this$0 = clientPlayer;
        this.capeLocation = capeTexture;
        this.imageBufferDownload = new ImageBufferDownload();
    }
}

