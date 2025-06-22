package com.example.flashscoreapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "FlashScoreAppSession";
    private static final String KEY_USER_EMAIL = "user_email";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveUserSession(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getString(KEY_USER_EMAIL, null) != null;
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    public void logoutUser() {
        // Xóa tất cả dữ liệu trong SharedPreferences
        editor.clear();
        editor.commit();
    }
}