/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;

public class Minecraft$3
implements Callable<String> {
    public Minecraft field_90046_a;

    @Override
    public String call() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String resourcePack : this.field_90046_a.gameSettings.resourcePacks) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append((Object)resourcePack);
            if (!this.field_90046_a.gameSettings.incompatibleResourcePacks.contains(resourcePack)) continue;
            stringBuilder.append(" (incompatible)");
        }
        return stringBuilder.toString();
    }

    public Minecraft$3(Minecraft minecraft) {
        this.field_90046_a = minecraft;
    }
}

