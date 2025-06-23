package com.example.flashscoreapp;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.flashscoreapp.util.ThemeHelper;

public class MyApplication extends Application {
    private static final String CHANNEL_ID = "flashscore_channel";
    @Override
    public void onCreate() {
        super.onCreate();
        // Lấy theme đã lưu và áp dụng ngay khi ứng dụng khởi động
        int savedTheme = ThemeHelper.getSavedTheme(this);
        ThemeHelper.applyTheme(savedTheme);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FlashScore Notifications";
            String description = "Channel for FlashScore match events";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}