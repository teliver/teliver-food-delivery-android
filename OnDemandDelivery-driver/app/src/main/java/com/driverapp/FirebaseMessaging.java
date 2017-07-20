package com.driverapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

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
        Log.d("TELIVER::", "onMessageReceived: PUSH TRY == "+remoteMessage.toString());
        try {
            if (Teliver.isTeliverPush(remoteMessage)) {
                Map<String, String> pushData = remoteMessage.getData();
                final NotificationData data = new GsonBuilder().create().fromJson(pushData.get("description"), NotificationData.class);
                Log.d("TELIVER::", "onMessageReceived: " + "tracking id ==" + data.getTrackingID()
                        + "commnad ==" + data.getCommand() + "message == " + data.getMessage());
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
        notification.setLargeIcon(Utils.getBitmapIcon(this));

        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(data.getMessage()).setBigContentTitle("Teliver"));
        notification.setAutoCancel(true);
        notification.setPriority(Notification.PRIORITY_MAX);
        notification.setDefaults(Notification.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification.build());
    }


}


