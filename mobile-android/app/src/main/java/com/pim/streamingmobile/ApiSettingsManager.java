package com.pim.streamingmobile;

import android.content.Context;
import android.content.SharedPreferences;

public final class ApiSettingsManager {

    private static final String PREFS_NAME = "streaming_api";
    private static final String KEY_BASE_URL = "base_url";

    private ApiSettingsManager() {
    }

    public static String getBaseUrl(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_BASE_URL, ApiConfig.DEFAULT_BASE_URL);
    }

    public static void saveBaseUrl(Context context, String baseUrl) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_BASE_URL, normalize(baseUrl)).apply();
    }

    public static String normalize(String baseUrl) {
        String normalized = baseUrl == null ? "" : baseUrl.trim();

        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "http://" + normalized;
        }

        if (!normalized.endsWith("/")) {
            normalized += "/";
        }

        if (!normalized.endsWith("api/")) {
            normalized += "api/";
        }

        return normalized;
    }
}
