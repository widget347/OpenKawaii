/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class RenderItem$7
implements ItemMeshDefinition {
    public RenderItem field_178115_a;

    public RenderItem$7(RenderItem renderItem) {
        this.field_178115_a = renderItem;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack) {
        return new ModelResourceLocation("banner", "inventory");
    }
}

