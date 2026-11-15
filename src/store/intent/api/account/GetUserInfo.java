/*
 * Decompiled with CFR 0.152.
 */
package store.intent.api.account;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import store.intent.api.EnvironmentConstants;
import store.intent.api.account.IntentAccount;
import store.intent.api.util.ConstructableEntry;
import store.intent.api.web.Browser;

public class GetUserInfo
implements EnvironmentConstants {
    static final String COMBO_LOGIN_ENDPOINT = "login?email=%s&password=%s";
    static final String API_KEY_LOGIN_ENDPOINT = "login?key=%s";
    public static final IntentAccount loginFailure = new IntentAccount();
    public static final IntentAccount parseFailure = new IntentAccount();

    public IntentAccount getIntentAccount(String apiKey) {
        try {
            apiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8.toString());
            String response = Browser.getResponse(String.format(API_KEY_LOGIN_ENDPOINT, apiKey));
            if (response.equals("Invalid Login Credentials")) {
                return loginFailure;
            }
            try {
                return new Gson().fromJson(response, IntentAccount.class);
            }
            catch (JsonSyntaxException jsonSyntaxException) {
                jsonSyntaxException.printStackTrace();
                return parseFailure;
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return loginFailure;
        }
    }

    public IntentAccount getIntentAccount(String email, String password) {
        try {
            String response = Browser.postResponse("login", new ConstructableEntry<String, String>("email", email), new ConstructableEntry<String, String>("password", password));
            if (response.equals("Invalid Login Credentials")) {
                return loginFailure;
            }
            try {
                return new Gson().fromJson(response, IntentAccount.class);
            }
            catch (JsonSyntaxException jsonSyntaxException) {
                jsonSyntaxException.printStackTrace();
                return parseFailure;
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return loginFailure;
        }
    }
}

