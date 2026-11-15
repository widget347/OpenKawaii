package store.intent.api.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import javax.net.ssl.HttpsURLConnection;
import store.intent.api.EnvironmentConstants;
import store.intent.api.util.ConstructableEntry;

public class Browser implements EnvironmentConstants {
   public static String getResponse(String endpoint) throws IOException {
      HttpsURLConnection connection = (HttpsURLConnection)new URL("https://intent.store/api/" + endpoint).openConnection();
      connection.addRequestProperty("User-Agent", "Intent-API/1.0 Kawaii");
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder responseBuilder = new StringBuilder();

      String line;
      while ((line = reader.readLine()) != null) {
         responseBuilder.append(line);
      }

      return responseBuilder.toString();
   }

   @SafeVarargs
   public static String postResponse(String endpoint, ConstructableEntry<String, String>... params) throws IOException {
      HttpsURLConnection connection = (HttpsURLConnection)new URL("https://intent.store/api/" + endpoint).openConnection();
      connection.addRequestProperty("User-Agent", "Intent-API/1.0 Kawaii");
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      StringJoiner joiner = new StringJoiner("&");

      for (ConstructableEntry entry : params) {
         joiner.add(URLEncoder.encode((String)entry.getKey(), "UTF-8") + "=" + URLEncoder.encode((String)entry.getValue(), "UTF-8"));
      }

      byte[] body = joiner.toString().getBytes(StandardCharsets.UTF_8);
      int bodyLength = body.length;
      connection.setFixedLengthStreamingMode(bodyLength);
      connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
      connection.connect();

      try (OutputStream outputStream = connection.getOutputStream()) {
         outputStream.write(body);
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder responseBuilder = new StringBuilder();

      String line;
      while ((line = reader.readLine()) != null) {
         responseBuilder.append(line);
      }

      return responseBuilder.toString();
   }
}
