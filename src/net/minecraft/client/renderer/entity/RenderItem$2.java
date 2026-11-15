/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import java.util.concurrent.Callable;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

public class RenderItem$2
implements Callable<String> {
    public ItemStack field_147926_a;
    public RenderItem field_147925_b;

    @Override
    public String call() {
        return String.valueOf(this.field_147926_a.Xs());
    }

    public RenderItem$2(RenderItem renderItem, ItemStack itemStack) {
        this.field_147925_b = renderItem;
        this.field_147926_a = itemStack;
    }
}

