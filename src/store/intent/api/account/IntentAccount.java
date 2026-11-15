/*
 * Decompiled with CFR 0.152.
 */
package store.intent.api.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IntentAccount {
    @Expose
    @SerializedName(value="username")
    public String username;
    @Expose
    @SerializedName(value="email")
    public String email;
    @Expose
    @SerializedName(value="intent_uid")
    public int intent_uid;
    @Expose
    @SerializedName(value="client_uid")
    public int client_uid;
    @Expose
    @SerializedName(value="discord_tag")
    public String discord_tag;
    @Expose
    @SerializedName(value="discord_id")
    public String discord_id;
    @Expose
    @SerializedName(value="twoFactor")
    public boolean twoFactor;
    @Expose
    @SerializedName(value="api_key")
    public String api_key;
    @Expose
    @SerializedName(value="loggedIn")
    public boolean loggedIn;
}

