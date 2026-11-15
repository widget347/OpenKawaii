/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;

public class Minecraft$5
implements Callable<String> {
    public Minecraft field_142056_a;

    @Override
    public String call() {
        return this.field_142056_a.mcProfiler.plp ? this.field_142056_a.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
    }

    public Minecraft$5(Minecraft minecraft) {
        this.field_142056_a = minecraft;
    }
}

