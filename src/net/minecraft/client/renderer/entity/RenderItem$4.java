/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import java.util.concurrent.Callable;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

public class RenderItem$4
implements Callable<String> {
    public RenderItem field_147931_b;
    public ItemStack field_147932_a;

    public RenderItem$4(RenderItem renderItem, ItemStack itemStack) {
        this.field_147931_b = renderItem;
        this.field_147932_a = itemStack;
    }

    @Override
    public String call() {
        return String.valueOf(this.field_147932_a.hasEffect());
    }
}

