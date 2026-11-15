/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.texture;

import java.util.concurrent.Callable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

public class TextureMap$3
implements Callable<String> {
    public TextureAtlasSprite field_147974_a;
    public static String AEp = "CL_00001061";
    public TextureMap field_147973_b;

    public TextureMap$3(TextureMap textureMap, TextureAtlasSprite textureAtlasSprite) {
        this.field_147973_b = textureMap;
        this.field_147974_a = textureAtlasSprite;
    }

    @Override
    public String call() {
        return this.field_147974_a.getFrameCount() + " frames";
    }
}

