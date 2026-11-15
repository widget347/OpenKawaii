/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import org.lwjgl.Sys;

public class Minecraft$15
implements Callable<String> {
    public Minecraft field_74503_a;

    public Minecraft$15(Minecraft minecraft) {
        this.field_74503_a = minecraft;
    }

    @Override
    public String call() {
        return Sys.getVersion();
    }
}

