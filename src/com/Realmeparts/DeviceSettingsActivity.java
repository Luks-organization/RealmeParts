/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.app.Fragment;
import android.os.Bundle;
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;

public class DeviceSettingsActivity extends CollapsingToolbarBaseActivity {

    private DeviceSettings mDeviceSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFragment();
    }

    private void initializeFragment() {
        Fragment existingFragment = getFragmentManager().findFragmentById(com.android.settingslib.collapsingtoolbar.R.id.content_frame);

        if (existingFragment == null) {
            mDeviceSettingsFragment = new DeviceSettings();
            addFragment(mDeviceSettingsFragment);
        } else {
            mDeviceSettingsFragment = (DeviceSettings) existingFragment;
        }
    }

    private void addFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .add(com.android.settingslib.collapsingtoolbar.R.id.content_frame, fragment)
                .commit();
    }
}
