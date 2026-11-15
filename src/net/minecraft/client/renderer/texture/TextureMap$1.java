/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.texture;

import java.util.concurrent.Callable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

public class TextureMap$1
implements Callable<String> {
    public static String xCa = "CL_00001059";
    public TextureAtlasSprite field_147980_a;
    public TextureMap field_147979_b;

    public TextureMap$1(TextureMap textureMap, TextureAtlasSprite textureAtlasSprite) {
        this.field_147979_b = textureMap;
        this.field_147980_a = textureAtlasSprite;
    }

    @Override
    public String call() {
        return this.field_147980_a.DAr();
    }
}

