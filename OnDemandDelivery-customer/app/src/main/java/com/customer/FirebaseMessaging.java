package com.customer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.customer.activities.ActivityHome;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.GsonBuilder;
import com.teliver.sdk.core.Teliver;
import com.teliver.sdk.models.NotificationData;

import org.json.JSONObject;

import java.util.Map;


public class FirebaseMessaging extends FirebaseMessagingService {

    private Application application;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("TELIVER::", "onMessageReceived: PUSH TRY == " +remoteMessage.toString());
        try {
            if (Teliver.isTeliverPush(remoteMessage)) {
                Map<String, String> pushData = remoteMessage.getData();
                final NotificationData data = new GsonBuilder().create().fromJson(pushData.get("description"), NotificationData.class);
                Log.d("TELIVER::", "onMessageReceived: " + "tracking id ==" + data.getTrackingID()
                        + "commnad ==" + data.getCommand() + "message == " + data.getMessage());
                application = (Application) getApplicationContext();
                application.storeStringInPref(Constants.TRACKING_ID, data.getTrackingID());
                Intent intent = new Intent(this, ActivityHome.class);
                JSONObject jsonObject = new JSONObject(data.getPayload());
                String status = jsonObject.getString("status");
                intent.putExtra("msg", status);
                intent.setAction("message");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                if (data.getMessage().equalsIgnoreCase("2"))
                    sendPushNotification(data, getString(R.string.txtOutToDelivery));
                else if (data.getMessage().equalsIgnoreCase("1"))
                    sendPushNotification(data, getString(R.string.txtNearDoorPush));
                else if (data.getMessage().equalsIgnoreCase("3"))
                    sendPushNotification(data, getString(R.string.txtDeliveredPush));
                else sendPushNotification(data,data.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPushNotification(NotificationData data, String pushMessage) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setContentTitle("Teliver");
        notification.setContentText(pushMessage);
        notification.setSmallIcon(R.drawable.ic_scooter);
        notification.setLargeIcon(Utils.getBitmapIcon(this));

        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(pushMessage).setBigContentTitle("Teliver"));
        Intent intent = new Intent(this, ActivityHome.class);
        intent.putExtra("msg", data.getMessage());
        intent.putExtra("tracking_id", data.getTrackingID());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setContentIntent(pendingIntent);
        notification.setAutoCancel(true);
        notification.setPriority(Notification.PRIORITY_MAX);
        notification.setDefaults(Notification.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification.build());
    }
}


