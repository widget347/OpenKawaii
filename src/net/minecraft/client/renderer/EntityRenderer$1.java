/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import com.google.common.base.Predicate;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;

public class EntityRenderer$1
implements Predicate<Entity> {
    public EntityRenderer field_90032_a;

    @Override
    public boolean apply(Entity entity) {
        return entity.canBeCollidedWith();
    }

    public EntityRenderer$1(EntityRenderer entityRenderer) {
        this.field_90032_a = entityRenderer;
    }
}

