/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceManager;

public class GameModeSwitch implements OnPreferenceChangeListener {
    public static final int GameMode_Notification_Channel_ID = 0x11011;
    private static final String FILE = "/proc/touchpanel/game_switch_enable";
    private static Context mContext;
    private static NotificationManager mNotificationManager;
    private static int userSelectedDndMode;

    public GameModeSwitch(Context context) {
        mContext = context;
        userSelectedDndMode = mContext.getSystemService(NotificationManager.class).getCurrentInterruptionFilter();
    }

    public static String getFile() {
        if (Utils.fileWritable(FILE)) {
            return FILE;
        }
        return null;
    }

    public static boolean isSupported() {
        return Utils.fileWritable(getFile());
    }

    public static boolean isCurrentlyEnabled(Context context) {
        String fileContent = Utils.getFileValue(getFile(), "0");
        return fileContent.startsWith("1");
    }

    public static boolean checkNotificationPolicy(Context context) {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        return mNotificationManager.isNotificationPolicyAccessGranted();
    }

    public static void GameModeDND() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        if (!checkNotificationPolicy(mContext)) {
            //Launch Do Not Disturb Access settings
            Intent DNDAccess = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            mContext.startActivity(DNDAccess);
        } else if (isCurrentlyEnabled(mContext)) {
            userSelectedDndMode = mContext.getSystemService(NotificationManager.class).getCurrentInterruptionFilter();
            if (sharedPreferences.getBoolean("dnd", false)) activateDND();
            AppNotification.Send(mContext, GameMode_Notification_Channel_ID, mContext.getString(R.string.game_mode_title), mContext.getString(R.string.game_mode_notif_content));
            ShowToast();
        } else if (!isCurrentlyEnabled(mContext)) {
            if (sharedPreferences.getBoolean("dnd", false))
                mNotificationManager.setInterruptionFilter(userSelectedDndMode);
            AppNotification.Cancel(mContext, GameMode_Notification_Channel_ID);
            ShowToast();
        }
    }

    public static void activateDND() {
        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
        mNotificationManager.setNotificationPolicy(
                new NotificationManager.Policy(NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA, 0, 0));
    }

    public static void ShowToast() {
        if (isCurrentlyEnabled(mContext)) {
            Toast.makeText(mContext, "GameMode is activated. ", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(mContext, "GameMode is deactivated. ", Toast.LENGTH_SHORT).show();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        Utils.writeValue(getFile(), enabled ? "1" : "0");
        Utils.writeValue(DeviceSettings.TP_LIMIT_ENABLE, enabled ? "0" : "1");
        SystemProperties.set("persist.perf_profile", enabled ? "2" : "0");
        if (enabled) Utils.startService(mContext, GameModeRotationService.class);
        else Utils.stopService(mContext, GameModeRotationService.class);
        GameModeDND();
        return true;
    }
}
