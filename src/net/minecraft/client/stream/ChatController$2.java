/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.stream;

import net.minecraft.client.stream.ChatController;
import tv.twitch.chat.ChatEvent;

public class ChatController$2 {
    public static int[] $SwitchMap$net$minecraft$client$stream$ChatController$EnumEmoticonMode;
    public static int[] $SwitchMap$net$minecraft$client$stream$ChatController$EnumChannelState;
    public static int[] $SwitchMap$tv$twitch$chat$ChatEvent;

    static {
        $SwitchMap$tv$twitch$chat$ChatEvent = new int[ChatEvent.values().length];
        try {
            ChatController$2.$SwitchMap$tv$twitch$chat$ChatEvent[ChatEvent.TTV_CHAT_JOINED_CHANNEL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChatController$2.$SwitchMap$tv$twitch$chat$ChatEvent[ChatEvent.TTV_CHAT_LEFT_CHANNEL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$net$minecraft$client$stream$ChatController$EnumChannelState = new int[ChatController.EnumChannelState.values().length];
        try {
            ChatController$2.$SwitchMap$net$minecraft$client$stream$ChatController$EnumChannelState[ChatController.EnumChannelState.Created.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChatController$2.$SwitchMap$net$minecraft$client$stream$ChatController$EnumChannelState[ChatController.EnumChannelState.Disconnected.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChatController$2.$SwitchMap$net$minecraft$client$stream$ChatController$EnumChannelState[ChatController.EnumChannelState.Connected.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChatController$2.$SwitchMap$net$minecraft$client$stream$ChatController$EnumChannelState[ChatController.EnumChannelState.Disconnecting.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChatController$2.$SwitchMap$net$minecraft$client$stream$ChatController$EnumChannelState[ChatController.EnumChannelState.Connecting.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$net$minecraft$client$stream$ChatController$EnumEmoticonMode = new int[ChatController.EnumEmoticonMode.values().length];
        try {
            ChatController$2.$SwitchMap$net$minecraft$client$stream$ChatController$EnumEmoticonMode[ChatController.EnumEmoticonMode.TextureAtlas.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChatController$2.$SwitchMap$net$minecraft$client$stream$ChatController$EnumEmoticonMode[ChatController.EnumEmoticonMode.Url.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChatController$2.$SwitchMap$net$minecraft$client$stream$ChatController$EnumEmoticonMode[ChatController.EnumEmoticonMode.None.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}

