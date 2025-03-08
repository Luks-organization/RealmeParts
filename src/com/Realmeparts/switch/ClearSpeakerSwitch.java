/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

public class ClearSpeakerSwitch implements OnPreferenceChangeListener {


    public static String getFile() {
        return null;
    }

    public static boolean isSupported() {
        return true;
    }

    public static boolean isCurrentlyEnabled(Context context) {
        return Utils.getFileValueAsBoolean(getFile(), false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        Utils.writeValue(getFile(), enabled ? "1" : "0");
        return true;
    }
}
