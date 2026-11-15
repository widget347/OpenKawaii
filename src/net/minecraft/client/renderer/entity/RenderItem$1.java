/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import java.util.concurrent.Callable;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

public class RenderItem$1
implements Callable<String> {
    public ItemStack field_147929_a;
    public RenderItem field_147928_b;

    public RenderItem$1(RenderItem renderer, ItemStack stack) {
        this.field_147928_b = renderer;
        this.field_147929_a = stack;
    }

    @Override
    public String call() {
        return String.valueOf(this.field_147929_a.getItem());
    }
}

