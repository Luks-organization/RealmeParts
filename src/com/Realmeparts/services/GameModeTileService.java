/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import androidx.preference.PreferenceManager;

@TargetApi(24)
public class GameModeTileService extends TileService {
    private boolean enabled = false;
    private Context mContext;
    private NotificationManager mNotificationManager;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        enabled = GameModeSwitch.isCurrentlyEnabled(this);
        getQsTile().setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        enabled = GameModeSwitch.isCurrentlyEnabled(this);

        boolean newEnabledState = !enabled;
        Utils.writeValue(GameModeSwitch.getFile(), newEnabledState ? "1" : "0");
        Utils.writeValue(DeviceSettings.TP_LIMIT_ENABLE, newEnabledState ? "0" : "1");
        SystemProperties.set("persist.perf_profile", newEnabledState ? "2" : "0");

        if (newEnabledState) {
            AppNotification.Send(this, GameModeSwitch.GameMode_Notification_Channel_ID, this.getString(R.string.game_mode_title), this.getString(R.string.game_mode_notif_content));
        } else {
            AppNotification.Cancel(this, GameModeSwitch.GameMode_Notification_Channel_ID);
        }
        if (newEnabledState) {
            Utils.startService(this, GameModeRotationService.class);
        } else {
            Utils.stopService(this, GameModeRotationService.class);
        }
        if (sharedPrefs.getBoolean("dnd", false)) {
            GameModeTileDND(newEnabledState);
        }
        sharedPrefs.edit().putBoolean(DeviceSettings.KEY_GAME_SWITCH, newEnabledState).commit();
        getQsTile().setState(newEnabledState ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    private void GameModeTileDND(boolean enabled) {
        if (enabled) {
            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
            mNotificationManager.setNotificationPolicy(
                    new NotificationManager.Policy(NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA, 0, 0));
        } else {
            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        }
    }
}
