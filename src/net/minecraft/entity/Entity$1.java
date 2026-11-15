/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.util.concurrent.Callable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

public class Entity$1
implements Callable<String> {
    public Entity Lgc;

    @Override
    public String call() {
        return EntityList.HZ(this.Lgc) + " (" + this.Lgc.getClass().getCanonicalName() + ")";
    }

    public Entity$1(Entity entity) {
        this.Lgc = entity;
    }
}

