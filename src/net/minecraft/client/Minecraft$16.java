/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class Minecraft$16
implements Callable<String> {
    public Minecraft field_79002_a;

    public Minecraft$16(Minecraft minecraft) {
        this.field_79002_a = minecraft;
    }

    @Override
    public String call() {
        return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
    }
}

