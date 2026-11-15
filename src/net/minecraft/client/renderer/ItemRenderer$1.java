/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import net.minecraft.network.play.server.S38PacketPlayerListItem;

public class ItemRenderer$1 {
    public static int[] $SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action = new int[S38PacketPlayerListItem.Action.values().length];

    static {
        try {
            ItemRenderer$1.$SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action[S38PacketPlayerListItem.Action.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ItemRenderer$1.$SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action[S38PacketPlayerListItem.Action.EAT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ItemRenderer$1.$SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action[S38PacketPlayerListItem.Action.DRINK.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ItemRenderer$1.$SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action[S38PacketPlayerListItem.Action.BLOCK.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ItemRenderer$1.$SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action[S38PacketPlayerListItem.Action.BOW.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}

