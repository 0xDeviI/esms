package com.arminapps.esms.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences preferences;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
    }

    public void setBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public void setString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public void setLong(String key, long value) {
        preferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        return preferences.getLong(key, -1);
    }

    public void setInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return preferences.getInt(key, -1);
    }
}
