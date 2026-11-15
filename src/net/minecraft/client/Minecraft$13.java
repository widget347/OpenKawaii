/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;

public class Minecraft$13
implements Callable<String> {
    public Minecraft field_90053_a;

    @Override
    public String call() {
        return this.field_90053_a.currentScreen.getClass().getCanonicalName();
    }

    public Minecraft$13(Minecraft minecraft) {
        this.field_90053_a = minecraft;
    }
}

