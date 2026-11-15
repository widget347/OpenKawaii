/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;

public class Minecraft$4
implements Callable<String> {
    public Minecraft field_90048_a;

    public Minecraft$4(Minecraft minecraft) {
        this.field_90048_a = minecraft;
    }

    @Override
    public String call() {
        return Minecraft.access$100(this.field_90048_a).getCurrentLanguage().toString();
    }
}

