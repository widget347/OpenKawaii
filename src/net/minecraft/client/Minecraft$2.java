/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;

public class Minecraft$2
implements Callable<String> {
    public Minecraft field_90051_a;

    public Minecraft$2(Minecraft minecraft) {
        this.field_90051_a = minecraft;
    }

    @Override
    public String call() {
        return "Client (map_client.txt)";
    }
}

