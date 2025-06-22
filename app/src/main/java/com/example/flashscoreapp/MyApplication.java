package com.example.flashscoreapp;

import android.app.Application;
import com.example.flashscoreapp.util.ThemeHelper;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Lấy theme đã lưu và áp dụng ngay khi ứng dụng khởi động
        int savedTheme = ThemeHelper.getSavedTheme(this);
        ThemeHelper.applyTheme(savedTheme);
    }
}