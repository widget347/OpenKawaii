/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;

public class Minecraft$12
implements Callable<String> {
    public Minecraft field_90055_a;

    public Minecraft$12(Minecraft minecraft) {
        this.field_90055_a = minecraft;
    }

    @Override
    public String call() {
        return this.field_90055_a.currentScreen.getClass().getCanonicalName();
    }
}

