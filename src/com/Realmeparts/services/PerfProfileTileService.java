/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class PerfProfileTileService extends TileService {

    @Override
    public void onStartListening() {
        int currentState = Utils.getintProp(DeviceSettings.PERF_PROFILE_SYSTEM_PROPERTY, 0);

        Tile tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        tile.setLabel(getResources().getStringArray(R.array.perf_profiles)[currentState]);

        tile.updateTile();
        super.onStartListening();
    }

    @Override
    public void onClick() {
        int currentState = Utils.getintProp(DeviceSettings.PERF_PROFILE_SYSTEM_PROPERTY, 0);

        int nextState;
        if (currentState == 2) {
            nextState = 0;
        } else {
            nextState = currentState + 1;
        }

        Tile tile = getQsTile();
        Utils.setintProp(DeviceSettings.PERF_PROFILE_SYSTEM_PROPERTY, nextState);
        tile.setLabel(getResources().getStringArray(R.array.perf_profiles)[nextState]);

        tile.updateTile();
        super.onClick();
    }
}
