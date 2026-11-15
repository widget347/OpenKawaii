/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class RenderItem$9
implements ItemMeshDefinition {
    public RenderItem field_178114_a;

    public RenderItem$9(RenderItem renderItem) {
        this.field_178114_a = renderItem;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack) {
        return new ModelResourceLocation("filled_map", "inventory");
    }
}

