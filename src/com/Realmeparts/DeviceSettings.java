/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DecimalFormat;

public class DeviceSettings extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    // Preference Keys
    public static final String KEY_SRGB_SWITCH = "srgb";
    public static final String KEY_DC_SWITCH = "dc";
    public static final String KEY_OTG_SWITCH = "otg";
    public static final String KEY_GAME_SWITCH = "game";
    public static final String KEY_PERF_PROFILE = "perf_profile";
    public static final String KEY_DT2W_SWITCH = "dt2w";
    public static final String KEY_DND_SWITCH = "dnd";
    public static final String KEY_CABC = "cabc";
    public static final String KEY_SETTINGS_PREFIX = "device_setting_";
    public static final String TP_LIMIT_ENABLE = "/proc/touchpanel/oplus_tp_limit_enable";
    public static final String TP_DIRECTION = "/proc/touchpanel/oplus_tp_direction";

    // System Properties
    public static final String PERF_PROFILE_SYSTEM_PROPERTY = "persist.perf_profile";
    public static final String CABC_SYSTEM_PROPERTY = "persist.cabc_profile";

    // Key categories
    private static final String KEY_CATEGORY_GRAPHICS = "graphics";
    private static final String KEY_CATEGORY_REFRESH_RATE = "refresh_rate";
    private static final String KEY_CATEGORY_MTK_ENG = "mtk_engineer";
    
    private static final String ProductName = Utils.ProductName();  // Get product name

    // Preference components
    public TwoStatePreference mDNDSwitch;
    public PreferenceCategory mPreferenceCategory;

    public static TwoStatePreference mRefreshRate90Forced;
    public static DisplayManager mDisplayManager;

    private static NotificationManager mNotificationManager;
    private static TwoStatePreference mDT2WModeSwitch;

    private TwoStatePreference mDCModeSwitch;
    private TwoStatePreference mSRGBModeSwitch;
    private TwoStatePreference mOTGModeSwitch;
    private TwoStatePreference mGameModeSwitch;	
    private SecureSettingListPreference mCABC;
    private SecureSettingListPreference mPerfProfile;
    private Preference mEngineerMode;

    private boolean CABC_DeviceMatched;
    private boolean DC_DeviceMatched;
    private boolean sRGB_DeviceMatched;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Get context and preferences
        Context context = getContext();
        if (context == null) {
            return;  // Exit early if context is null
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("ProductName", ProductName).apply();

        // Load preferences from XML
        addPreferencesFromResource(R.xml.main);

        // Initialize DC Mode Switch
        mDCModeSwitch = findPreference(KEY_DC_SWITCH);
        if (mDCModeSwitch != null) {
            mDCModeSwitch.setEnabled(DCModeSwitch.isSupported());
            mDCModeSwitch.setChecked(DCModeSwitch.isCurrentlyEnabled(context));
            mDCModeSwitch.setOnPreferenceChangeListener(new DCModeSwitch());
        }

        // Initialize SRGB Mode Switch
        mSRGBModeSwitch = findPreference(KEY_SRGB_SWITCH);
        if (mSRGBModeSwitch != null) {
            mSRGBModeSwitch.setEnabled(SRGBModeSwitch.isSupported());
            mSRGBModeSwitch.setChecked(SRGBModeSwitch.isCurrentlyEnabled(context));
            mSRGBModeSwitch.setOnPreferenceChangeListener(new SRGBModeSwitch());
        }

        // Initialize OTG Mode Switch
        mOTGModeSwitch = findPreference(KEY_OTG_SWITCH);
        if (mOTGModeSwitch != null) {
            mOTGModeSwitch.setEnabled(OTGModeSwitch.isSupported());
            mOTGModeSwitch.setChecked(OTGModeSwitch.isCurrentlyEnabled(context));
            mOTGModeSwitch.setOnPreferenceChangeListener(new OTGModeSwitch());
        }

        // Initialize Game Mode Switch
        mGameModeSwitch = findPreference(KEY_GAME_SWITCH);
        if (mGameModeSwitch != null) {
            mGameModeSwitch.setEnabled(GameModeSwitch.isSupported());
            mGameModeSwitch.setChecked(GameModeSwitch.isCurrentlyEnabled(context));
            mGameModeSwitch.setOnPreferenceChangeListener(new GameModeSwitch(context));
        }

        // Initialize DT2W Mode Switch
        mDT2WModeSwitch = findPreference(KEY_DT2W_SWITCH);
        if (mDT2WModeSwitch != null) {
            mDT2WModeSwitch.setEnabled(DT2WModeSwitch.isSupported());
            mDT2WModeSwitch.setChecked(DT2WModeSwitch.isCurrentlyEnabled(context));
            mDT2WModeSwitch.setOnPreferenceChangeListener(new DT2WModeSwitch());
        }

        // Refresh Rate 90Hz Forced Switch
        mRefreshRate90Forced = findPreference("refresh_rate_90Forced");
        if (mRefreshRate90Forced != null) {
            mRefreshRate90Forced.setChecked(prefs.getBoolean("refresh_rate_90Forced", false));
            mRefreshRate90Forced.setOnPreferenceChangeListener(new RefreshRateSwitch(context));
        }

        // CABC (Content Adaptive Backlight Control) Preference
        mCABC = findPreference(KEY_CABC);
        if (mCABC != null) {
            mCABC.setValue(Utils.getStringProp(CABC_SYSTEM_PROPERTY, "0"));
            mCABC.setSummary(mCABC.getEntry());
            mCABC.setOnPreferenceChangeListener(this);
        }

        // Performance Profile Preference
        mPerfProfile = findPreference(KEY_PERF_PROFILE);
        if (mPerfProfile != null) {
            mPerfProfile.setValue(Utils.getStringProp(PERF_PROFILE_SYSTEM_PROPERTY, "0"));
            mPerfProfile.setSummary(mPerfProfile.getEntry());
            mPerfProfile.setOnPreferenceChangeListener(this);
        }

        // Engineer Mode Preference (only available if Developer Options are enabled)
        mEngineerMode = findPreference(KEY_CATEGORY_MTK_ENG);
        boolean isDevOptionsEnabled = Settings.Global.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0;
        if (mEngineerMode != null && !isDevOptionsEnabled) {
            getPreferenceScreen().removePreference(mEngineerMode);
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (newValue == null) {
            return false;  // No change if the newValue is null.
        }

        if (preference == mCABC) {
            String newCABCValue = (String) newValue;
            mCABC.setValue(newCABCValue);
            mCABC.setSummary(mCABC.getEntry());
            Utils.setStringProp(CABC_SYSTEM_PROPERTY, newCABCValue);

        } else if (preference == mPerfProfile) {
            String newPerfProfileValue = (String) newValue;
            mPerfProfile.setValue(newPerfProfileValue);
            mPerfProfile.setSummary(mPerfProfile.getEntry());
            Utils.setStringProp(PERF_PROFILE_SYSTEM_PROPERTY, newPerfProfileValue);
        }

        return true;
    }

    private void ParseJson() throws JSONException {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        mPreferenceCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_GRAPHICS);

        // Read the JSON file as string
        String features_json = Utils.InputStreamToString(getResources().openRawResource(R.raw.realmeparts_features));
        if (features_json == null || features_json.isEmpty()) {
            Log.e("ParseJson", "Failed to load JSON data");
            return;
        }

        // Parse JSON string into JSONObject
        JSONObject jsonOB = new JSONObject(features_json);

        // Get the CABC array from the JSON object
        JSONArray CABC = jsonOB.optJSONArray(KEY_CABC);
        if (CABC == null) {
            Log.e("ParseJson", "CABC array not found in JSON");
            return;
        }

        // Check if ProductName contains any entry in the CABC array
        boolean CABC_DeviceMatched = false;
        String productNameUpper = ProductName.toUpperCase();
        for (int i = 0; i < CABC.length(); i++) {
            if (productNameUpper.contains(CABC.getString(i))) {
                CABC_DeviceMatched = true;
                break;  // Exit the loop early since we've already found a match
            }
        }

        // Remove CABC preference if device is unsupported
        if (!CABC_DeviceMatched) {
            Preference cabcPreference = findPreference(KEY_CABC);
            if (cabcPreference != null) {
                mPreferenceCategory.removePreference(cabcPreference);
            }
            prefs.edit().putBoolean("CABC_DeviceMatched", false).apply();
        } else {
            prefs.edit().putBoolean("CABC_DeviceMatched", true).apply();
        }
    }
}
