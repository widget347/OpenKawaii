/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.texture;

import java.util.concurrent.Callable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

public class TextureMap$2
implements Callable<String> {
    public TextureAtlasSprite field_147977_a;
    public TextureMap field_147976_b;
    public static String Gtc = "CL_00001060";

    public TextureMap$2(TextureMap textureMap, TextureAtlasSprite textureAtlasSprite) {
        this.field_147976_b = textureMap;
        this.field_147977_a = textureAtlasSprite;
    }

    @Override
    public String call() {
        return this.field_147977_a.$No() + " x " + this.field_147977_a.jJ();
    }
}

