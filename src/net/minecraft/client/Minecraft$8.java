/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import net.minecraft.client.Minecraft;

public class Minecraft$8
implements Runnable {
    public Minecraft field_152128_a;

    public Minecraft$8(Minecraft minecraft) {
        this.field_152128_a = minecraft;
    }

    @Override
    public void run() {
        this.field_152128_a.refreshResources();
    }
}

