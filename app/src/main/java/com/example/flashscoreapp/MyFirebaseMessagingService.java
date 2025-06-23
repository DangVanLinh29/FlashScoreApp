package com.example.flashscoreapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "flashscore_channel";

    /**
     * Được gọi khi có một tin nhắn mới được nhận.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Kiểm tra xem tin nhắn có chứa payload thông báo không.
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Title: " + title);
            Log.d(TAG, "Notification Body: " + body);

            // Hiển thị thông báo lên màn hình
            sendNotification(title, body);
        }
    }

    /**
     * Được gọi khi một token mới được tạo ra cho thiết bị.
     * Token này chính là "địa chỉ" để server gửi thông báo đến.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        // TODO: Gửi token này lên backend server của bạn để lưu lại.
        // Ví dụ: sendRegistrationToServer(token);
    }

    /**
     * Tạo và hiển thị một thông báo đơn giản.
     */
    private void sendNotification(String messageTitle, String messageBody) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_football_goal)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // --- BẮT ĐẦU PHẦN SỬA LỖI ---
        // 1. Kiểm tra xem quyền đã được cấp hay chưa
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa được cấp quyền, không làm gì cả và thoát khỏi hàm
            Log.d(TAG, "Quyền POST_NOTIFICATIONS chưa được cấp.");
            return;
        }
        // 2. Nếu đã có quyền, thì mới hiển thị thông báo
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        // --- KẾT THÚC PHẦN SỬA LỖI ---
    }

    /**
     * Tạo Notification Channel, bắt buộc cho Android 8.0 (API 26) trở lên.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FlashScore Notifications";
            String description = "Channel for FlashScore match events";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}