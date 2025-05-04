/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

public class RefreshRateSwitch implements OnPreferenceChangeListener {

    private static final IBinder SF = ServiceManager.getService("SurfaceFlinger");
    public static int setRefreshRate;
    private final Context mContext;

    public RefreshRateSwitch(Context context) {
        mContext = context;
    }

    public static void setForcedRefreshRate(int value) {
        Parcel Info = Parcel.obtain();
        Info.writeInterfaceToken("android.ui.ISurfaceComposer");
        Info.writeInt(value);
        try {
            SF.transact(1035, Info, null, 0);
        } catch (RemoteException e) {
            Log.e("DeviceSettings", e.toString());
        } finally {
            Info.recycle();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        if (preference == DeviceSettings.mRefreshRate90Forced && enabled) {
            setForcedRefreshRate(1);
        } else if (preference == DeviceSettings.mRefreshRate90Forced && !enabled) {
            setForcedRefreshRate(0);
        }
        return true;
    }
}
