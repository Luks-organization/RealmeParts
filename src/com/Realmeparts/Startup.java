/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class Startup extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";
    private static final String ONE_TIME_TUNABLE_RESTORE = "hardware_tunable_restored";

    @Override
    public void onReceive(final Context context, final Intent bootIntent) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        
        restoreSwitch(DCModeSwitch.getFile(), sharedPrefs.getBoolean(DeviceSettings.KEY_DC_SWITCH, false));
        restoreSwitch(SRGBModeSwitch.getFile(), sharedPrefs.getBoolean(DeviceSettings.KEY_SRGB_SWITCH, false));
        restoreSwitch(OTGModeSwitch.getFile(), sharedPrefs.getBoolean(DeviceSettings.KEY_OTG_SWITCH, false));
        restoreSwitch(DT2WModeSwitch.getFile(), sharedPrefs.getBoolean(DeviceSettings.KEY_DT2W_SWITCH, false));
        handleRefreshRate(sharedPrefs);
    }

    private void restoreSwitch(String file, boolean enabled) {
        if (file != null) {
            Utils.writeValue(file, enabled ? "1" : "0");
        }
    }

    private void handleRefreshRate(SharedPreferences sharedPrefs) {
        if (sharedPrefs.getBoolean("refresh_rate_90Forced", true)) {
            RefreshRateSwitch.setForcedRefreshRate(1);
        }
    }

    private boolean hasRestoredTunable(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ONE_TIME_TUNABLE_RESTORE, false);
    }

    private void setRestoredTunable(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(ONE_TIME_TUNABLE_RESTORE, true).apply();
    }
}
