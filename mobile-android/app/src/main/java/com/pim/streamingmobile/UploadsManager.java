package com.pim.streamingmobile;

import android.content.Context;
import android.content.SharedPreferences;

public final class UploadsManager {

    private static final String PREFS_NAME = "streaming_uploads";
    private static final String KEY_UPLOAD_COUNT = "upload_count";
    private static final String KEY_LAST_UPLOAD_NAME = "last_upload_name";

    private UploadsManager() {
    }

    public static void registrarUpload(Context context, String fileName) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int count = preferences.getInt(KEY_UPLOAD_COUNT, 0);
        preferences.edit()
                .putInt(KEY_UPLOAD_COUNT, count + 1)
                .putString(KEY_LAST_UPLOAD_NAME, fileName == null ? "Arquivo selecionado" : fileName)
                .apply();
    }

    public static int getQuantidadeUploads(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_UPLOAD_COUNT, 0);
    }

    public static String getUltimoUpload(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_LAST_UPLOAD_NAME, "Nenhum arquivo selecionado.");
    }
}
