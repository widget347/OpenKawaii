/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;

public class Minecraft$18
implements Callable<String> {
    public Minecraft field_82887_a;

    public Minecraft$18(Minecraft minecraft) {
        this.field_82887_a = minecraft;
    }

    @Override
    public String call() {
        return this.field_82887_a.gameSettings.useVbo ? "Yes" : "No";
    }
}

