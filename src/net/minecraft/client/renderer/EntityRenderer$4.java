/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import java.util.concurrent.Callable;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;

public class EntityRenderer$4
implements Callable<String> {
    public ScaledResolution scaledResolution;
    public static String Dng = "CL_00000951";
    public EntityRenderer entityRenderer;

    public EntityRenderer$4(EntityRenderer entityRenderer, ScaledResolution scaledResolution) {
        this.entityRenderer = entityRenderer;
        this.scaledResolution = scaledResolution;
    }

    @Override
    public String call() {
        return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", this.scaledResolution.getScaledWidth(), this.scaledResolution.getScaledHeight(), EntityRenderer.access$000((EntityRenderer)this.entityRenderer).displayWidth, EntityRenderer.access$000((EntityRenderer)this.entityRenderer).displayHeight, this.scaledResolution.getScaleFactor());
    }
}

