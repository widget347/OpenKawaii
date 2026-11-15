/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import java.util.concurrent.Callable;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

public class RenderItem$3
implements Callable<String> {
    public RenderItem field_147934_b;
    public ItemStack field_147935_a;

    @Override
    public String call() {
        return String.valueOf(this.field_147935_a.getTagCompound());
    }

    public RenderItem$3(RenderItem renderItem, ItemStack itemStack) {
        this.field_147934_b = renderItem;
        this.field_147935_a = itemStack;
    }
}

