package com.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import com.example.android.softkeyboard.Home;
import com.example.android.softkeyboard.R;
import com.example.android.softkeyboard.SetUpActivity;


public class NotificationHelper {

    private static final int NOTIFICATION_ID = 1024;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static NotificationChannel notificationChannel;
    private static NotificationManager mNotificationManager;
    /**
     * Create and push the notification
     */
    public static void createNotification(Context mContext,String title, String message) {

        try {

            //activity to redirect when user clicks the notification
            Intent resultIntent = new Intent(mContext, SetUpActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            //add redirect activity as pending intent
            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            //notification builder
            Notification.Builder mBuilder = new Notification.Builder(mContext);
            mBuilder.setSmallIcon(R.drawable.keyboard3);
            mBuilder.setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true);

            //notification manager for showing notification
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            //set up notification channel for android devices above oreo
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                    //create notification channel
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);

                assert mNotificationManager != null;
                //set channel id
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(notificationChannel);

            }
            assert mNotificationManager != null; //show notification on phone
            mNotificationManager.notify(NOTIFICATION_ID /* Request Code */, mBuilder.build());

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * to cancel a on going notification
     * */
    public static void cancelNotification(){
        try
        {
            //check whether notification manager initialized
            if(mNotificationManager!=null){
                //cancel notification with id
                mNotificationManager.cancel(NOTIFICATION_ID);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
