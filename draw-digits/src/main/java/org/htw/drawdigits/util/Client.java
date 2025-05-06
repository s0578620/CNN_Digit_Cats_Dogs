package org.htw.drawdigits.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class Client extends OkHttpClient {

  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  public static final String URL = "http://localhost:8000/%s";

  public static String convertToJSON(String key, String value) {
    return new JSONObject(Map.of(key, value)).toString();
  }

  public static JSONObject convertToJSON(String response) {
    return new JSONObject(response);
  }

  public Response post(String method, String json) {
    RequestBody body = RequestBody.create(json, JSON);
    Request request = new Request.Builder()
        .url(format(URL, method))
        .post(body)
        .build();
    try {
      return newCall(request).execute();
    } catch (IOException e) {
      System.out.printf("error while performing post: %s%n", e.getMessage());
      return null;
    }
  }

  public String get(String method) {
    Request request = new Request.Builder()
        .url(format(URL, method))
        .build();
    try (Response response = newCall(request).execute()) {
      return requireNonNull(response.body()).string();
    } catch (IOException e) {
      System.out.printf("error while performing get: %s%n", e.getMessage());
      return "";
    }
  }

  public Response get() throws IOException {
    Request request = new Request.Builder()
        .url(format(URL, ""))
        .build();
    return newCall(request).execute();
  }

  public JSONObject getModels() {
    return convertToJSON(get("model"));
  }
}
