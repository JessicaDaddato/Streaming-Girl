package com.pim.streamingmobile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class ApiClient {

    private static final Gson GSON = new Gson();

    private ApiClient() {
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);

        void onError(String message);
    }

    public static void getConteudos(ApiCallback<List<ConteudoItem>> callback) {
        Type type = new TypeToken<List<ConteudoItem>>() {}.getType();
        executeGet("Conteudos", type, callback);
    }

    public static void getPlaylists(ApiCallback<List<PlaylistItem>> callback) {
        Type type = new TypeToken<List<PlaylistItem>>() {}.getType();
        executeGet("Playlists", type, callback);
    }

    public static void getUsuarios(ApiCallback<List<UsuarioItem>> callback) {
        Type type = new TypeToken<List<UsuarioItem>>() {}.getType();
        executeGet("Usuarios", type, callback);
    }

    public static void login(String email, String senha, ApiCallback<LoginResponse> callback) {
        LoginRequestBody body = new LoginRequestBody(email, senha);
        executeJsonRequest("POST", "Auth/login", body, null, LoginResponse.class, callback);
    }

    public static void createPlaylist(String token, String nome, int usuarioId,
                                      ApiCallback<PlaylistItem> callback) {
        PlaylistRequest body = new PlaylistRequest(0, nome, usuarioId);
        executeJsonRequest("POST", "Playlists", body, token, PlaylistItem.class, callback);
    }

    public static void updatePlaylist(String token, int playlistId, String nome, int usuarioId,
                                      ApiCallback<Void> callback) {
        PlaylistRequest body = new PlaylistRequest(playlistId, nome, usuarioId);
        executeJsonRequest("PUT", "Playlists/" + playlistId, body, token, Void.class, callback);
    }

    public static void deletePlaylist(String token, int playlistId, ApiCallback<Void> callback) {
        executeJsonRequest("DELETE", "Playlists/" + playlistId, null, token, Void.class, callback);
    }

    private static <T> void executeGet(String endpoint, Type type, ApiCallback<T> callback) {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(resolveBaseUrl() + endpoint);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                InputStream stream = responseCode >= 200 && responseCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                String body = readBody(stream);

                if (responseCode >= 200 && responseCode < 300) {
                    T result = GSON.fromJson(body, type);
                    callback.onSuccess(result);
                    return;
                }

                callback.onError("Erro HTTP " + responseCode + ": " + body);
            } catch (Exception exception) {
                callback.onError(exception.getMessage() != null
                        ? exception.getMessage()
                        : "Falha ao conectar com o backend.");
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private static <T> void executeJsonRequest(String method, String endpoint, Object payload,
                                               String token, Class<T> responseClass,
                                               ApiCallback<T> callback) {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(resolveBaseUrl() + endpoint);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                if (token != null && !token.isBlank()) {
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                }

                if (payload != null) {
                    connection.setDoOutput(true);
                    String json = GSON.toJson(payload);

                    try (OutputStream outputStream = connection.getOutputStream();
                         BufferedWriter writer = new BufferedWriter(
                                 new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
                        writer.write(json);
                        writer.flush();
                    }
                }

                int responseCode = connection.getResponseCode();
                InputStream stream = responseCode >= 200 && responseCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();
                String body = readBody(stream);

                if (responseCode >= 200 && responseCode < 300) {
                    if (responseClass == Void.class || body.isBlank()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onSuccess(GSON.fromJson(body, responseClass));
                    }
                    return;
                }

                callback.onError("Erro HTTP " + responseCode + ": " + body);
            } catch (Exception exception) {
                callback.onError(exception.getMessage() != null
                        ? exception.getMessage()
                        : "Falha ao conectar com o backend.");
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private static String resolveBaseUrl() {
        if (AppContextHolder.getContext() == null) {
            return ApiConfig.DEFAULT_BASE_URL;
        }

        return ApiSettingsManager.getBaseUrl(AppContextHolder.getContext());
    }

    private static String readBody(InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }

        return builder.toString();
    }
}
