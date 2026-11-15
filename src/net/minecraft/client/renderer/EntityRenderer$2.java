/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

public class EntityRenderer$2
implements Callable<String> {
    public EntityRenderer field_90025_c;
    public static String qBg = "CL_00000948";

    public EntityRenderer$2(EntityRenderer entityRenderer) {
        this.field_90025_c = entityRenderer;
    }

    @Override
    public String call() {
        return Minecraft.getMinecraft().currentScreen.getClass().getCanonicalName();
    }
}

