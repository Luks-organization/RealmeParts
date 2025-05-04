/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import androidx.preference.PreferenceManager;

@TargetApi(24)
public class DCModeTileService extends TileService {
    private boolean enabled = false;

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
		
        enabled = DCModeSwitch.isCurrentlyEnabled(this);
        getQsTile().setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().setLabel(getResources().getString(R.string.tile_dc_mode));
			
        getQsTile().updateTile();

    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        enabled = DCModeSwitch.isCurrentlyEnabled(this);
        Utils.writeValue(DCModeSwitch.getFile(), enabled ? "0" : "1");
        sharedPrefs.edit().putBoolean(DeviceSettings.KEY_DC_SWITCH, !enabled).commit();
        //getQsTile().setLabel(enabled ? "DC off" : "DC On");
        getQsTile().setState(enabled ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
        getQsTile().updateTile();
    }
}
