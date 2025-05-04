/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class DefaultIndicatorSeekBar extends SeekBar {

    private int mDefaultProgress = -1;

    public DefaultIndicatorSeekBar(Context context) {
        super(context);
    }

    public DefaultIndicatorSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultIndicatorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DefaultIndicatorSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getDefaultProgress() {
        return mDefaultProgress;
    }

    /**
     * N.B. This sets the default *unadjusted* progress, i.e. in the SeekBar's [0 - max] terms.
     */
    public void setDefaultProgress(int defaultProgress) {
        if (mDefaultProgress != defaultProgress) {
            mDefaultProgress = defaultProgress;
            invalidate();
        }
    }
}
