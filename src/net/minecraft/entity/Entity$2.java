/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.util.concurrent.Callable;
import net.minecraft.entity.Entity;

public class Entity$2
implements Callable<String> {
    public Entity field_96564_a;

    public Entity$2(Entity targetEntity) {
        this.field_96564_a = targetEntity;
    }

    @Override
    public String call() {
        return this.field_96564_a.getName();
    }
}

