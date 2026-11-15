/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.stream;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import kawaii.event.HandlerMap;
import net.minecraft.client.stream.ChatController$1;
import net.minecraft.client.stream.TwitchStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.twitch.AuthToken;
import tv.twitch.Core;
import tv.twitch.ErrorCode;
import tv.twitch.StandardCoreAPI;
import tv.twitch.chat.Chat;
import tv.twitch.chat.ChatBadgeData;
import tv.twitch.chat.ChatChannelInfo;
import tv.twitch.chat.ChatEmoticonData;
import tv.twitch.chat.ChatEvent;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizationOption;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.IChatAPIListener;
import tv.twitch.chat.IChatChannelListener;
import tv.twitch.chat.StandardChatAPI;

public class ChatController {
    public AuthToken field_153012_j;
    public Core field_175992_e = null;
    public String channelName = "";
    public IChatAPIListener field_175999_p;
    public int maxStoredMessages = 128;
    public static Logger LOGGER = LogManager.getLogger();
    public ChatListener field_153003_a = null;
    public Chat field_153008_f = null;
    public ChatState field_153011_i = ChatState.Uninitialized;
    public int userChangeEventInterval = 2000;
    public String username = "";
    public int messageFlushInterval = 500;
    public EnumEmoticonMode field_175997_k;
    public EnumEmoticonMode field_175995_l;
    public HashMap<String, ChatChannelListener> field_175998_i;
    public String field_153006_d = "";
    public ChatEmoticonData field_175996_m = null;

    public void setAuthToken(AuthToken authToken) {
        this.field_153012_j = authToken;
    }

    public boolean reconnectChannel(String string) {
        if (this.field_153011_i != ChatState.Initialized) {
            return false;
        }
        if (!this.field_175998_i.containsKey(string)) {
            this.logError("Not in channel: " + string);
            return false;
        }
        ChatChannelListener chatChannelListener = this.field_175998_i.get(string);
        return chatChannelListener.reconnect();
    }

    public void prepareEmoticonData() {
        if (this.field_175996_m == null) {
            this.field_175996_m = new ChatEmoticonData();
            ErrorCode errorCode = this.field_153008_f.getEmoticonData(this.field_175996_m);
            if (ErrorCode.succeeded(errorCode)) {
                try {
                    if (this.field_153003_a != null) {
                        this.field_153003_a.onEmoticonDataReady();
                    }
                }
                catch (Exception exception) {
                    this.logError(exception.toString());
                }
            } else {
                this.logError("Error preparing emoticon data: " + ErrorCode.getString(errorCode));
            }
        }
    }

    public void setClientId(String string) {
        this.field_153006_d = string;
    }

    public boolean joinChannelAuthenticated(String string) {
        return this.joinChannel(string, false);
    }

    public ChatController() {
        this.field_153012_j = new AuthToken();
        this.field_175998_i = new HandlerMap();
        this.field_175997_k = EnumEmoticonMode.TextureAtlas;
        this.field_175995_l = EnumEmoticonMode.TextureAtlas;
        this.field_175999_p = new ChatController$1(this);
        this.field_175992_e = Core.getInstance();
        if (this.field_175992_e == null) {
            this.field_175992_e = new Core(new StandardCoreAPI());
        }
        this.field_153008_f = new Chat(new StandardChatAPI());
    }

    public void clearEmoticonData() {
        if (this.field_175996_m != null) {
            ErrorCode errorCode = this.field_153008_f.clearEmoticonData();
            if (ErrorCode.succeeded(errorCode)) {
                this.field_175996_m = null;
                try {
                    if (this.field_153003_a != null) {
                        this.field_153003_a.onEmoticonDataCleared();
                    }
                }
                catch (Exception exception) {
                    this.logError(exception.toString());
                }
            } else {
                this.logError("Error clearing emoticon data: " + ErrorCode.getString(errorCode));
            }
        }
    }

    public ChatState getChatState() {
        return this.field_153011_i;
    }

    public boolean joinChannel(String string, boolean anonymous) {
        if (this.field_153011_i != ChatState.Initialized) {
            return false;
        }
        if (this.field_175998_i.containsKey(string)) {
            this.logError("Already in channel: " + string);
            return false;
        }
        if (string != null && !string.equals("")) {
            ChatChannelListener chatChannelListener = new ChatChannelListener(this, string);
            this.field_175998_i.put(string, chatChannelListener);
            boolean connected = chatChannelListener.connect(anonymous);
            if (!connected) {
                this.field_175998_i.remove(string);
            }
            return connected;
        }
        return false;
    }

    public boolean initializeChat() {
        if (this.field_153011_i != ChatState.Uninitialized) {
            return false;
        }
        this.setChatState(ChatState.Initializing);
        ErrorCode errorCode = this.field_175992_e.initialize(this.field_153006_d, null);
        if (ErrorCode.failed(errorCode)) {
            this.setChatState(ChatState.Uninitialized);
            String string = ErrorCode.getString(errorCode);
            this.logError(String.format("Error initializing Twitch sdk: %s", string));
            return false;
        }
        this.field_175995_l = this.field_175997_k;
        HashSet<ChatTokenizationOption> hashSet = new HashSet<ChatTokenizationOption>();
        switch (this.field_175997_k) {
            case TextureAtlas: {
                hashSet.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_NONE);
                break;
            }
            case Url: {
                hashSet.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS);
                break;
            }
            case None: {
                hashSet.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES);
            }
        }
        errorCode = this.field_153008_f.initialize(hashSet, this.field_175999_p);
        if (ErrorCode.failed(errorCode)) {
            this.field_175992_e.shutdown();
            this.setChatState(ChatState.Uninitialized);
            String string = ErrorCode.getString(errorCode);
            this.logError(String.format("Error initializing Twitch chat: %s", string));
            return false;
        }
        this.setChatState(ChatState.Initialized);
        return true;
    }

    public void flushEvents() {
        ErrorCode errorCode;
        if (this.field_153011_i != ChatState.Uninitialized && ErrorCode.failed(errorCode = this.field_153008_f.flushEvents())) {
            String string = ErrorCode.getString(errorCode);
            this.logError(String.format("Error flushing chat events: %s", string));
        }
    }

    public void shutdownBlocking() {
        if (this.getChatState() != ChatState.Uninitialized) {
            this.shutdownChat();
            if (this.getChatState() == ChatState.ShuttingDown) {
                while (this.getChatState() != ChatState.Uninitialized) {
                    try {
                        Thread.sleep(200L);
                        this.flushEvents();
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
        }
    }

    public void downloadEmoticonData() {
        ErrorCode errorCode;
        if (this.field_175995_l != EnumEmoticonMode.TextureAtlas && this.field_175996_m == null && ErrorCode.failed(errorCode = this.field_153008_f.downloadEmoticonData())) {
            String string = ErrorCode.getString(errorCode);
            this.logError(String.format("Error trying to download emoticon data: %s", string));
        }
    }

    public EnumChannelState getChannelState(String string) {
        if (!this.field_175998_i.containsKey(string)) {
            return EnumChannelState.Disconnecting;
        }
        ChatChannelListener chatChannelListener = this.field_175998_i.get(string);
        return chatChannelListener.getChannelState();
    }

    public void setChatListener(ChatListener chatListener) {
        this.field_153003_a = chatListener;
    }

    public boolean isChannelCreated(String string) {
        if (!this.field_175998_i.containsKey(string)) {
            return false;
        }
        ChatChannelListener chatChannelListener = this.field_175998_i.get(string);
        return chatChannelListener.getChannelState() == EnumChannelState.Created;
    }

    public void setUsername(String string) {
        this.username = string;
    }

    public boolean sendChatMessage(String string, String message) {
        if (this.field_153011_i != ChatState.Initialized) {
            return false;
        }
        if (!this.field_175998_i.containsKey(string)) {
            this.logError("Not in channel: " + string);
            return false;
        }
        ChatChannelListener chatChannelListener = this.field_175998_i.get(string);
        return chatChannelListener.sendMessage(message);
    }

    public void setChatState(ChatState chatState) {
        if (chatState != this.field_153011_i) {
            this.field_153011_i = chatState;
            try {
                if (this.field_153003_a != null) {
                    this.field_153003_a.onChatStateChanged(chatState);
                }
            }
            catch (Exception exception) {
                this.logError(exception.toString());
            }
        }
    }

    public void logError(String string) {
        LOGGER.error(TwitchStream.STREAM_MARKER, "[Chat controller] {}", string);
    }

    public boolean shutdownChat() {
        if (this.field_153011_i != ChatState.Initialized) {
            return false;
        }
        ErrorCode errorCode = this.field_153008_f.shutdown();
        if (ErrorCode.failed(errorCode)) {
            String string = ErrorCode.getString(errorCode);
            this.logError(String.format("Error shutting down chat: %s", string));
            return false;
        }
        this.clearEmoticonData();
        this.setChatState(ChatState.ShuttingDown);
        return true;
    }

    public static class ChatChannelListener
    implements IChatChannelListener {
        public boolean field_176046_b;
        public LinkedList<ChatRawMessage> field_176045_e;
        public List<ChatUserInfo> field_176044_d;
        public String field_176048_a;
        public LinkedList<ChatTokenizedMessage> field_176042_f;
        public EnumChannelState field_176047_c;
        public ChatController field_176049_h;
        public ChatBadgeData field_176043_g;

        @Override
        public void chatChannelUserChangeCallback(String string, ChatUserInfo[] chatUserInfoArray, ChatUserInfo[] chatUserInfoArray2, ChatUserInfo[] chatUserInfoArray3) {
            int index;
            int loopIndex;
            for (loopIndex = 0; loopIndex < chatUserInfoArray2.length; ++loopIndex) {
                index = this.field_176044_d.indexOf(chatUserInfoArray2[loopIndex]);
                if (index < 0) continue;
                this.field_176044_d.remove(index);
            }
            for (loopIndex = 0; loopIndex < chatUserInfoArray3.length; loopIndex += 38) {
                index = this.field_176044_d.indexOf(chatUserInfoArray3[loopIndex]);
                if (index >= 0) {
                    this.field_176044_d.remove(index);
                }
                this.field_176044_d.add(chatUserInfoArray3[loopIndex]);
                loopIndex -= 37;
            }
            for (loopIndex = 0; loopIndex < chatUserInfoArray.length; ++loopIndex) {
                this.field_176044_d.add(chatUserInfoArray[loopIndex]);
            }
            try {
                if (this.field_176049_h.field_153003_a != null) {
                    this.field_176049_h.field_153003_a.onChannelUsersChanged(this.field_176048_a, chatUserInfoArray, chatUserInfoArray2, chatUserInfoArray3);
                }
            }
            catch (Exception exception) {
                this.field_176049_h.logError(exception.toString());
            }
        }

        public void disconnect() {
            if (this.field_176047_c != EnumChannelState.Disconnecting) {
                this.setChannelState(EnumChannelState.Disconnecting);
                this.notifyChannelDisconnected(this.field_176048_a);
                this.clearBadgeData();
            }
        }

        public void setChannelState(EnumChannelState enumChannelState) {
            if (enumChannelState != this.field_176047_c) {
                this.field_176047_c = enumChannelState;
            }
        }

        public void clearMessages(String string) {
            if (this.field_176049_h.field_175995_l == EnumEmoticonMode.TextureAtlas) {
                this.field_176045_e.clear();
                this.field_176042_f.clear();
            } else {
                Object item;
                ListIterator listIterator;
                if (this.field_176045_e.size() > 0) {
                    listIterator = this.field_176045_e.listIterator();
                    while (listIterator.hasNext()) {
                        item = (ChatRawMessage)listIterator.next();
                        if (!((ChatRawMessage)item).userName.equals(string)) continue;
                        listIterator.remove();
                    }
                }
                if (this.field_176042_f.size() > 0) {
                    listIterator = this.field_176042_f.listIterator();
                    while (listIterator.hasNext()) {
                        item = (ChatTokenizedMessage)listIterator.next();
                        if (!((ChatTokenizedMessage)item).displayName.equals(string)) continue;
                        listIterator.remove();
                    }
                }
            }
            try {
                if (this.field_176049_h.field_153003_a != null) {
                    this.field_176049_h.field_153003_a.onUserMessagesCleared(this.field_176048_a, string);
                }
            }
            catch (Exception exception) {
                this.field_176049_h.logError(exception.toString());
            }
        }

        @Override
        public void chatStatusCallback(String string, ErrorCode errorCode) {
            if (!ErrorCode.succeeded(errorCode)) {
                this.field_176049_h.field_175998_i.remove(string);
                this.disconnect();
            }
        }

        public void notifyChannelDisconnected(String string) {
            try {
                if (this.field_176049_h.field_153003_a != null) {
                    this.field_176049_h.field_153003_a.onChannelDisconnected(string);
                }
            }
            catch (Exception exception) {
                this.field_176049_h.logError(exception.toString());
            }
        }

        public boolean reconnect() {
            switch (this.field_176047_c) {
                case Created: 
                case Disconnected: {
                    ErrorCode errorCode = this.field_176049_h.field_153008_f.disconnect(this.field_176048_a);
                    if (ErrorCode.failed(errorCode)) {
                        String string = ErrorCode.getString(errorCode);
                        this.field_176049_h.logError(String.format("Error disconnecting: %s", string));
                        return false;
                    }
                    this.setChannelState(EnumChannelState.Connecting);
                    return true;
                }
            }
            return false;
        }

        public EnumChannelState getChannelState() {
            return this.field_176047_c;
        }

        @Override
        public void chatClearCallback(String string, String userName) {
            this.clearMessages(userName);
        }

        public ChatChannelListener(ChatController chatController, String string) {
            this.field_176049_h = chatController;
            this.field_176048_a = null;
            this.field_176046_b = false;
            this.field_176047_c = EnumChannelState.Connected;
            this.field_176044_d = Lists.newArrayList();
            this.field_176045_e = new LinkedList();
            this.field_176042_f = new LinkedList();
            this.field_176043_g = null;
            this.field_176048_a = string;
        }

        public void clearBadgeData() {
            if (this.field_176043_g != null) {
                ErrorCode errorCode = this.field_176049_h.field_153008_f.clearBadgeData(this.field_176048_a);
                if (ErrorCode.succeeded(errorCode)) {
                    this.field_176043_g = null;
                    try {
                        if (this.field_176049_h.field_153003_a != null) {
                            this.field_176049_h.field_153003_a.onBadgeDataCleared(this.field_176048_a);
                        }
                    }
                    catch (Exception exception) {
                        this.field_176049_h.logError(exception.toString());
                    }
                } else {
                    this.field_176049_h.logError("Error releasing badge data: " + ErrorCode.getString(errorCode));
                }
            }
        }

        @Override
        public void chatBadgeDataDownloadCallback(String string, ErrorCode errorCode) {
            if (ErrorCode.succeeded(errorCode)) {
                this.prepareBadgeData();
            }
        }

        @Override
        public void chatChannelRawMessageCallback(String string, ChatRawMessage[] chatRawMessageArray) {
            for (int i = 0; i < chatRawMessageArray.length; i -= 35) {
                this.field_176045_e.addLast(chatRawMessageArray[i]);
                i += 36;
            }
            try {
                if (this.field_176049_h.field_153003_a != null) {
                    this.field_176049_h.field_153003_a.onRawMessages(this.field_176048_a, chatRawMessageArray);
                }
            }
            catch (Exception exception) {
                this.field_176049_h.logError(exception.toString());
            }
            while (this.field_176045_e.size() > this.field_176049_h.maxStoredMessages) {
                this.field_176045_e.removeFirst();
            }
        }

        @Override
        public void chatChannelMembershipCallback(String string, ChatEvent chatEvent, ChatChannelInfo chatChannelInfo) {
            switch (chatEvent) {
                case TTV_CHAT_JOINED_CHANNEL: {
                    this.setChannelState(EnumChannelState.Created);
                    this.notifyChannelJoined(string);
                    break;
                }
                case TTV_CHAT_LEFT_CHANNEL: {
                    this.disconnect();
                }
            }
        }

        public void notifyChannelJoined(String string) {
            try {
                if (this.field_176049_h.field_153003_a != null) {
                    this.field_176049_h.field_153003_a.onChannelJoined(string);
                }
            }
            catch (Exception exception) {
                this.field_176049_h.logError(exception.toString());
            }
        }

        @Override
        public void chatChannelTokenizedMessageCallback(String string, ChatTokenizedMessage[] chatTokenizedMessageArray) {
            for (int i = 0; i < chatTokenizedMessageArray.length; i -= 43) {
                this.field_176042_f.addLast(chatTokenizedMessageArray[i]);
                i += 44;
            }
            try {
                if (this.field_176049_h.field_153003_a != null) {
                    this.field_176049_h.field_153003_a.onTokenizedMessages(this.field_176048_a, chatTokenizedMessageArray);
                }
            }
            catch (Exception exception) {
                this.field_176049_h.logError(exception.toString());
            }
            while (this.field_176042_f.size() > this.field_176049_h.maxStoredMessages) {
                this.field_176042_f.removeFirst();
            }
        }

        public void prepareBadgeData() {
            if (this.field_176043_g == null) {
                this.field_176043_g = new ChatBadgeData();
                ErrorCode errorCode = this.field_176049_h.field_153008_f.getBadgeData(this.field_176048_a, this.field_176043_g);
                if (ErrorCode.succeeded(errorCode)) {
                    try {
                        if (this.field_176049_h.field_153003_a != null) {
                            this.field_176049_h.field_153003_a.onBadgeDataReady(this.field_176048_a);
                        }
                    }
                    catch (Exception exception) {
                        this.field_176049_h.logError(exception.toString());
                    }
                } else {
                    this.field_176049_h.logError("Error preparing badge data: " + ErrorCode.getString(errorCode));
                }
            }
        }

        public boolean connect(boolean anonymous) {
            this.field_176046_b = anonymous;
            ErrorCode errorCode = ErrorCode.TTV_EC_SUCCESS;
            errorCode = anonymous ? this.field_176049_h.field_153008_f.connectAnonymous(this.field_176048_a, this) : this.field_176049_h.field_153008_f.connect(this.field_176048_a, this.field_176049_h.username, this.field_176049_h.field_153012_j.data, this);
            if (ErrorCode.failed(errorCode)) {
                String string = ErrorCode.getString(errorCode);
                this.field_176049_h.logError(String.format("Error connecting: %s", string));
                this.notifyChannelDisconnected(this.field_176048_a);
                return false;
            }
            this.setChannelState(EnumChannelState.Disconnected);
            this.downloadBadgeData();
            return true;
        }

        public void downloadBadgeData() {
            ErrorCode errorCode;
            if (this.field_176049_h.field_175995_l != EnumEmoticonMode.TextureAtlas && this.field_176043_g == null && ErrorCode.failed(errorCode = this.field_176049_h.field_153008_f.downloadBadgeData(this.field_176048_a))) {
                String string = ErrorCode.getString(errorCode);
                this.field_176049_h.logError(String.format("Error trying to download badge data: %s", string));
            }
        }

        public boolean sendMessage(String string) {
            if (this.field_176047_c != EnumChannelState.Created) {
                return false;
            }
            ErrorCode errorCode = this.field_176049_h.field_153008_f.sendMessage(this.field_176048_a, string);
            if (ErrorCode.failed(errorCode)) {
                String errorMessage = ErrorCode.getString(errorCode);
                this.field_176049_h.logError(String.format("Error sending chat message: %s", errorMessage));
                return false;
            }
            return true;
        }
    }

    public static enum EnumChannelState {
        Connected,
        Disconnected,
        Created,
        Connecting,
        Disconnecting;

    }

    public static enum EnumEmoticonMode {
        TextureAtlas,
        Url,
        None;

    }

    public static interface ChatListener {
        public void onChannelDisconnected(String channel);

        public void onChatStateChanged(ChatState state);

        public void onTokenizedMessages(String channel, ChatTokenizedMessage[] messages);

        public void onBadgeDataCleared(String channel);

        public void onChatError(ErrorCode error);

        public void onRawMessages(String channel, ChatRawMessage[] messages);

        public void onUserMessagesCleared(String channel, String userName);

        public void onChannelError(ErrorCode error);

        public void onChannelUsersChanged(String channel, ChatUserInfo[] joinedUsers, ChatUserInfo[] partedUsers, ChatUserInfo[] modifiedUsers);

        public void onBadgeDataReady(String channel);

        public void onEmoticonDataReady();

        public void onEmoticonDataCleared();

        public void onChannelJoined(String channel);
    }

    public static enum ChatState {
        Uninitialized,
        Initializing,
        Initialized,
        ShuttingDown;

    }
}

