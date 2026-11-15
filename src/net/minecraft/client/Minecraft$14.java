/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;

public class Minecraft$14
implements Callable<String> {
    public Minecraft field_74421_a;

    @Override
    public String call() {
        return Minecraft.access$000(this.field_74421_a);
    }

    public Minecraft$14(Minecraft minecraft) {
        this.field_74421_a = minecraft;
    }
}

