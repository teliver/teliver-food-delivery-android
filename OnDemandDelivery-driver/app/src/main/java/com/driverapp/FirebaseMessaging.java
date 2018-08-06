package com.driverapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.GsonBuilder;
import com.teliver.sdk.core.Teliver;
import com.teliver.sdk.models.NotificationData;

import java.util.Map;


public class FirebaseMessaging extends FirebaseMessagingService {

    private Application application;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            if (Teliver.isTeliverPush(remoteMessage)) {
                Map<String, String> pushData = remoteMessage.getData();
                final NotificationData data = new GsonBuilder().create().fromJson(pushData.get("description"), NotificationData.class);
                sendPushNotification(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPushNotification(NotificationData data) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setContentTitle("Teliver");
        notification.setContentText(data.getMessage());
        notification.setSmallIcon(R.drawable.ic_scooter);
        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(data.getMessage()).setBigContentTitle("Teliver"));
        notification.setAutoCancel(true);
        notification.setPriority(Notification.PRIORITY_MAX);
        notification.setDefaults(Notification.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification.build());
    }
}


