/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.stream;

import net.minecraft.client.stream.ChatController;
import tv.twitch.ErrorCode;
import tv.twitch.chat.IChatAPIListener;

public class ChatController$1
implements IChatAPIListener {
    public ChatController field_176000_a;

    @Override
    public void chatEmoticonDataDownloadCallback(ErrorCode errorCode) {
        if (ErrorCode.succeeded(errorCode)) {
            this.field_176000_a.prepareEmoticonData();
        }
    }

    @Override
    public void chatInitializationCallback(ErrorCode errorCode) {
        if (ErrorCode.succeeded(errorCode)) {
            this.field_176000_a.field_153008_f.setMessageFlushInterval(this.field_176000_a.messageFlushInterval);
            this.field_176000_a.field_153008_f.setUserChangeEventInterval(this.field_176000_a.userChangeEventInterval);
            this.field_176000_a.downloadEmoticonData();
            this.field_176000_a.setChatState(ChatController.ChatState.Initialized);
        } else {
            this.field_176000_a.setChatState(ChatController.ChatState.Uninitialized);
        }
        try {
            if (this.field_176000_a.field_153003_a != null) {
                this.field_176000_a.field_153003_a.onChannelError(errorCode);
            }
        }
        catch (Exception exception) {
            this.field_176000_a.logError(exception.toString());
        }
    }

    public ChatController$1(ChatController chatController) {
        this.field_176000_a = chatController;
    }

    @Override
    public void chatShutdownCallback(ErrorCode errorCode) {
        if (ErrorCode.succeeded(errorCode)) {
            ErrorCode shutdownErrorCode = this.field_176000_a.field_175992_e.shutdown();
            if (ErrorCode.failed(shutdownErrorCode)) {
                String errorMessage = ErrorCode.getString(shutdownErrorCode);
                this.field_176000_a.logError(String.format("Error shutting down the Twitch sdk: %s", errorMessage));
            }
            this.field_176000_a.setChatState(ChatController.ChatState.Uninitialized);
        } else {
            this.field_176000_a.setChatState(ChatController.ChatState.Initialized);
            this.field_176000_a.logError(String.format("Error shutting down Twith chat: %s", new Object[]{errorCode}));
        }
        try {
            if (this.field_176000_a.field_153003_a != null) {
                this.field_176000_a.field_153003_a.onChatError(errorCode);
            }
        }
        catch (Exception exception) {
            this.field_176000_a.logError(exception.toString());
        }
    }
}

