/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;

public class Minecraft$6
implements Callable<String> {
    public Minecraft field_151425_a;

    @Override
    public String call() {
        return OpenGlHelper.mpr();
    }

    public Minecraft$6(Minecraft minecraft) {
        this.field_151425_a = minecraft;
    }
}

