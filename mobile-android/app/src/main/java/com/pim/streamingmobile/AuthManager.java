package com.pim.streamingmobile;

import android.content.Context;
import android.content.SharedPreferences;

public final class AuthManager {

    private static final String PREFS_NAME = "streaming_auth";
    private static final String KEY_TOKEN = "jwt_token";

    private AuthManager() {
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_TOKEN, "");
    }

    public static boolean isLoggedIn(Context context) {
        String token = getToken(context);
        return token != null && !token.isBlank();
    }

    public static void clearToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_TOKEN).apply();
    }
}
