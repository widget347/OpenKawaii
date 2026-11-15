/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;

public class Minecraft$17
implements Callable<String> {
    public Minecraft field_74500_a;

    @Override
    public String call() {
        return OpenGlHelper.pN();
    }

    public Minecraft$17(Minecraft minecraft) {
        this.field_74500_a = minecraft;
    }
}

