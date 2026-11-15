/*
 * Decompiled with CFR 0.152.
 */
package kawaii.network;

import net.minecraft.network.play.client.C19PacketResourcePackStatus;

public class ResourcePackActionMap {
    public static int[] resourcePackActionMap = new int[C19PacketResourcePackStatus.Action.values().length];

    static {
        try {
            ResourcePackActionMap.resourcePackActionMap[C19PacketResourcePackStatus.Action.SUCCESS.ordinal()] = 1;
        }
        catch (NoSuchFieldError ignored) {
            // empty catch block
        }
        try {
            ResourcePackActionMap.resourcePackActionMap[C19PacketResourcePackStatus.Action.WARNING.ordinal()] = 2;
        }
        catch (NoSuchFieldError ignored) {
            // empty catch block
        }
        try {
            ResourcePackActionMap.resourcePackActionMap[C19PacketResourcePackStatus.Action.ERROR.ordinal()] = 3;
        }
        catch (NoSuchFieldError ignored) {
            // empty catch block
        }
    }
}

