/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

public class SecureSettingListPreference extends ListPreference {

    private boolean mAutoSummary = false;

    public SecureSettingListPreference(Context context) {
        super(context);
        initialize(context);
    }

    public SecureSettingListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SecureSettingListPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context) {
        setPreferenceDataStore(new SecureSettingsStore(context.getContentResolver()));
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        if (mAutoSummary || TextUtils.isEmpty(getSummary())) {
            setSummary(getEntry(), true);
        }
    }

    @Override
    public void setSummary(CharSequence summary) {
        setSummary(summary, false);
    }

    private void setSummary(CharSequence summary, boolean autoSummary) {
        mAutoSummary = autoSummary;
        super.setSummary(summary);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedString((String) defaultValue) : (String) defaultValue);
    }
}
