/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.util.concurrent.Callable;
import net.minecraft.entity.Entity;

public class Entity$3
implements Callable<String> {
    public Entity field_180119_a;

    @Override
    public String call() {
        return this.field_180119_a.ridingEntity.toString();
    }

    public Entity$3(Entity entity) {
        this.field_180119_a = entity;
    }
}

