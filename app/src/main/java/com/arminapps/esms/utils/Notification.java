package com.arminapps.esms.utils;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.arminapps.esms.R;
import com.arminapps.esms.views.lock.LockActivity;

public class Notification {
    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "eSMS";
            String description = "Sends encrypted SMS messages (eSMS).";
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, IMPORTANCE_HIGH);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static void sendNotification(Context context, String title, String message, int conversationId) {
        createNotificationChannel(context);

        // 2. Create an Intent to launch an Activity when the notification is tapped
        Intent intent = new Intent(context, LockActivity.class)
                .putExtra("action_load_conversation", true)
                .putExtra("conversation_id", conversationId)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create a PendingIntent
        // Use FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT for security/compatibility
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) conversationId % Integer.MAX_VALUE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 3. Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.esms_e_logo))
                .setSmallIcon(R.drawable.esms_e_logo) // Required small icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set notification priority
                .setContentIntent(pendingIntent) // Set the intent that fires when the user taps the notification
                .setAutoCancel(true); // Automatically removes the notification when tapped

        // 4. Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // A unique ID for the notification. Using the same ID updates the existing one.
        int notificationId = 1;

        // Check for runtime permission (required for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        notificationManager.notify(notificationId, builder.build());
    }

}
