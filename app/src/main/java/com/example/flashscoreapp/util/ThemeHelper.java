package com.example.flashscoreapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    private static final String PREF_NAME = "ThemePrefs";
    private static final String KEY_THEME = "selected_theme";

    // Các hằng số tương ứng với lựa chọn của người dùng
    public static final int LIGHT_MODE = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int DARK_MODE = AppCompatDelegate.MODE_NIGHT_YES;
    public static final int SYSTEM_DEFAULT = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

    // Áp dụng theme cho toàn bộ ứng dụng
    public static void applyTheme(int themeMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    // Lưu lựa chọn của người dùng
    public static void saveTheme(Context context, int themeMode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME, themeMode).apply();
    }

    // Lấy lựa chọn đã lưu
    public static int getSavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Mặc định là chế độ Hệ thống
        return prefs.getInt(KEY_THEME, SYSTEM_DEFAULT);
    }
}