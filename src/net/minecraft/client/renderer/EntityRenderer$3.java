/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import java.util.concurrent.Callable;
import net.minecraft.client.renderer.EntityRenderer;
import org.lwjgl.input.Mouse;

public class EntityRenderer$3
implements Callable<String> {
    public static String TRa = "CL_00000950";
    public EntityRenderer field_90028_b;
    public int field_181048_a;
    public int field_181049_b;

    public EntityRenderer$3(EntityRenderer entityRenderer, int scaledX, int scaledY) {
        this.field_90028_b = entityRenderer;
        this.field_181048_a = scaledX;
        this.field_181049_b = scaledY;
    }

    @Override
    public String call() {
        return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", this.field_181048_a, this.field_181049_b, Mouse.getX(), Mouse.getY());
    }
}

