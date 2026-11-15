/*
 * Decompiled with CFR 0.152.
 */
package store.intent.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;
import kawaii.Kawaii;
import store.intent.api.account.GetUserInfo;
import store.intent.api.account.IntentAccount;

public class Cloud {
    public String apiKey;
    public String product = "64";
    public String uid;
    public String username;
    public boolean loggedIn;

    public Cloud(String key) {
        this.apiKey = key;
        new Thread(() -> this.lambda_new_0(key)).start();
    }

    public JsonObject request(String path, Map<String, String> queryParams, Map<String, String> bodyParams) throws MalformedURLException, IOException {
        path = path + (queryParams.isEmpty() ? "" : "?") + queryParams.entrySet().stream().map(Cloud::lambda_request_1).collect(Collectors.joining("&"));
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection)new URL("https://intent.store/api/configuration/" + path).openConnection();
        httpsURLConnection.setRequestProperty("User-Agent", "Intent-API/1.0 " + Kawaii.INSTANCE.getName() + "/" + Kawaii.INSTANCE.getVersion());
        httpsURLConnection.setRequestMethod("POST");
        httpsURLConnection.setDoOutput(true);
        StringJoiner stringJoiner = new StringJoiner("&");
        for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
            stringJoiner.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        byte[] bodyBytes = stringJoiner.toString().getBytes(StandardCharsets.UTF_8);
        int bodyLength = bodyBytes.length;
        httpsURLConnection.setFixedLengthStreamingMode(bodyLength);
        httpsURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpsURLConnection.connect();
        Closeable closeable = httpsURLConnection.getOutputStream();
        Object current = null;
        try {
            ((java.io.OutputStream)closeable).write(bodyBytes);
        }
        catch (Throwable throwable) {
            current = throwable;
            throw throwable;
        }
        finally {
            if (closeable != null) {
                if (current != null) {
                    try {
                        closeable.close();
                    }
                    catch (Throwable throwable) {
                        ((Throwable)current).addSuppressed(throwable);
                    }
                } else {
                    closeable.close();
                }
            }
        }
        Closeable responseStream = closeable = httpsURLConnection.getResponseCode() / 100 == 2 ? httpsURLConnection.getInputStream() : httpsURLConnection.getErrorStream();
        if (closeable == null) {
            return new JsonObject();
        }
        current = new BufferedReader(new InputStreamReader((InputStream)closeable, StandardCharsets.UTF_8));
        JsonReader jsonReader = new JsonReader((Reader)current);
        jsonReader.setLenient(true);
        JsonElement jsonElement = new JsonParser().parse(jsonReader);
        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        }
        return new JsonObject();
    }

    public String postConfig(String name, String description, String meta, String body) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", this.apiKey);
        params.put("product_code", this.product);
        params.put("name", name);
        params.put("description", description);
        params.put("meta", meta);
        HashMap<String, String> bodyParams = new HashMap<String, String>();
        bodyParams.put("body", body);
        try {
            JsonObject jsonObject = this.request("post", params, bodyParams);
            if (jsonObject.has("response") && jsonObject.get("response").getAsString().equals("success")) {
                JsonObject configuration = jsonObject.get("configuration").getAsJsonObject();
                String uuid = configuration.get("uuid").getAsString();
                String shareCode = configuration.get("share_code").getAsString();
                return shareCode;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public boolean updateConfig(String shareCode, String meta, String body) {
        return this.updateConfig(shareCode, null, meta, body);
    }

    public boolean updateConfig(String shareCode, String name, String meta, String body) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", this.apiKey);
        if (name != null && !name.isEmpty()) {
            params.put("name", name);
        }
        params.put("product_code", this.product);
        params.put("share_code", shareCode);
        params.put("description", "");
        params.put("meta", meta);
        HashMap<String, String> bodyParams = new HashMap<String, String>();
        bodyParams.put("body", body);
        String responseText = "";
        try {
            JsonObject jsonObject = this.request("update", params, bodyParams);
            responseText = jsonObject.toString();
            if (jsonObject.has("response") && jsonObject.get("response").getAsString().equals("success")) {
                JsonObject configuration = jsonObject.get("configuration").getAsJsonObject();
                String uuid = configuration.get("uuid").getAsString();
                return true;
            }
        }
        catch (Exception exception) {
            System.err.println(responseText);
            exception.printStackTrace();
        }
        return false;
    }

    public JsonArray listConfigs(String meta) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", this.apiKey);
        params.put("product_code", this.product);
        params.put("meta", meta);
        HashMap<String, String> bodyParams = new HashMap<String, String>();
        try {
            JsonObject jsonObject = this.request("list", params, bodyParams);
            if (jsonObject.has("response") && jsonObject.get("response").getAsString().equals("success")) {
                JsonArray configurations = jsonObject.get("configurations").getAsJsonArray();
                return configurations;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public JsonObject getConfig(String shareCode) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", this.apiKey);
        params.put("product_code", this.product);
        params.put("share_code", shareCode);
        HashMap<String, String> bodyParams = new HashMap<String, String>();
        try {
            JsonObject jsonObject = this.request("get", params, bodyParams);
            if (jsonObject.has("response") && jsonObject.get("response").getAsString().equals("success")) {
                JsonObject configuration = jsonObject.get("configuration").getAsJsonObject();
                String uuid = configuration.get("uuid").getAsString();
                String name = configuration.get("name").getAsString();
                String meta = configuration.get("meta").getAsString();
                JsonObject body = configuration.get("body").getAsJsonObject();
                return body;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public boolean deleteConfig(String shareCode) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", this.apiKey);
        params.put("product_code", this.product);
        params.put("share_code", shareCode);
        HashMap<String, String> bodyParams = new HashMap<String, String>();
        try {
            JsonObject jsonObject = this.request("delete", params, bodyParams);
            if (jsonObject.has("response") && jsonObject.get("response").getAsString().equals("success")) {
                return true;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    private static String lambda_request_1(Map.Entry entry) {
        return URLEncoder.encode((String)entry.getKey()) + "=" + URLEncoder.encode((String)entry.getValue());
    }

    private void lambda_new_0(String key) {
        try {
            IntentAccount intentAccount = new GetUserInfo().getIntentAccount(key);
            if (intentAccount != null && intentAccount != GetUserInfo.parseFailure && intentAccount != GetUserInfo.loginFailure) {
                this.uid = Integer.toString(intentAccount.client_uid);
                this.username = intentAccount.username;
                this.loggedIn = true;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

