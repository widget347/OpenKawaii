/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.network;

import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S45PacketTitle;

public class NetHandlerPlayClient$4 {
    public static int[] $SwitchMap$net$minecraft$network$play$server$S45PacketTitle$Type;
    public static int[] $SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action_2;

    static {
        $SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action_2 = new int[S38PacketPlayerListItem.Action_2.values().length];
        try {
            NetHandlerPlayClient$4.$SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action_2[S38PacketPlayerListItem.Action_2.ADD_PLAYER.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NetHandlerPlayClient$4.$SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action_2[S38PacketPlayerListItem.Action_2.UPDATE_GAME_MODE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NetHandlerPlayClient$4.$SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action_2[S38PacketPlayerListItem.Action_2.UPDATE_LATENCY.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NetHandlerPlayClient$4.$SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action_2[S38PacketPlayerListItem.Action_2.UPDATE_DISPLAY_NAME.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$net$minecraft$network$play$server$S45PacketTitle$Type = new int[S45PacketTitle.Type.values().length];
        try {
            NetHandlerPlayClient$4.$SwitchMap$net$minecraft$network$play$server$S45PacketTitle$Type[S45PacketTitle.Type.TITLE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NetHandlerPlayClient$4.$SwitchMap$net$minecraft$network$play$server$S45PacketTitle$Type[S45PacketTitle.Type.SUBTITLE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NetHandlerPlayClient$4.$SwitchMap$net$minecraft$network$play$server$S45PacketTitle$Type[S45PacketTitle.Type.RESET.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}

