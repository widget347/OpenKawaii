/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

public class RenderItem$5
implements ItemMeshDefinition {
    public RenderItem field_178117_a;

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack) {
        return ItemPotion.isSplash(itemStack.Xs()) ? new ModelResourceLocation("bottle_splash", "inventory") : new ModelResourceLocation("bottle_drinkable", "inventory");
    }

    public RenderItem$5(RenderItem renderItem) {
        this.field_178117_a = renderItem;
    }
}

