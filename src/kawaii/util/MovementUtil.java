/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

import net.minecraft.client.Minecraft;

public class MovementUtil {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static boolean isMovingForward() {
        return MovementUtil.mc.thePlayer.moveForward > 0.0f;
    }

    public static boolean isMoving() {
        return MovementUtil.mc.thePlayer.moveForward != 0.0f || MovementUtil.mc.thePlayer.moveStrafing != 0.0f;
    }
}

