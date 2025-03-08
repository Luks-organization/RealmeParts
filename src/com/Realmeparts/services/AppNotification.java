/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class AppNotification {
    public static boolean NotificationSent;
    private static NotificationManager mNotificationManager;
    private static NotificationChannel mNotificationChannel;
    private static NotificationCompat.Builder notificationBuilder;
    private static Notification notification;

    public static void Send(Context context, int Notification_Channel_ID, String Notification_Channel_Name, String Notification_Content_Text) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationChannel = new NotificationChannel(Notification_Channel_Name, Notification_Channel_Name, NotificationManager.IMPORTANCE_DEFAULT);
        mNotificationManager.createNotificationChannel(mNotificationChannel);
        notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext())
                .setSmallIcon(R.drawable.ic_homepage_settings)
                .setContentTitle(Notification_Channel_Name)
                .setContentText(Notification_Content_Text)
                .setOngoing(true)
                .setChannelId(Notification_Channel_Name);
        Intent intent = new Intent(context, DeviceSettingsActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(Notification_Channel_ID, notification);
        NotificationSent = true;
    }

    public static void Cancel(Context context, int Notification_Channel_ID) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(Notification_Channel_ID);
        NotificationSent = false;
    }
}
