/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class RenderItem$6
implements ItemMeshDefinition {
    public RenderItem field_178118_a;

    public RenderItem$6(RenderItem renderItem) {
        this.field_178118_a = renderItem;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack) {
        return new ModelResourceLocation("spawn_egg", "inventory");
    }
}

