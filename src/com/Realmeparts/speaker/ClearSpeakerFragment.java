/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts.speaker;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import com.realmeparts.R;

import java.io.IOException;

public class ClearSpeakerFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = ClearSpeakerFragment.class.getSimpleName();
    private static final String PREF_CLEAR_SPEAKER = "clear_speaker_pref";
    private static final int PLAY_DURATION = 30000; // Duration to play sound in milliseconds

    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private SwitchPreference mClearSpeakerPref;
    private Handler mHandler;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.clear_speaker_settings);

        mClearSpeakerPref = findPreference(PREF_CLEAR_SPEAKER);
        mClearSpeakerPref.setOnPreferenceChangeListener(this);

        mHandler = new Handler();
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mClearSpeakerPref && newValue instanceof Boolean) {
            boolean value = (Boolean) newValue;
            if (value) {
                if (startPlaying()) {
                    // Schedule the stopPlaying method to be called after PLAY_DURATION milliseconds
                    mHandler.postDelayed(this::stopPlaying, PLAY_DURATION);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        stopPlaying();
    }

    private boolean startPlaying() {
        mAudioManager.setParameters("status_earpiece_clean=on");
        mMediaPlayer = new MediaPlayer();
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        try {
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.clear_speaker_sound);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            } finally {
                file.close();
            }
            mClearSpeakerPref.setEnabled(false);
            mMediaPlayer.setVolume(1.0f, 1.0f);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to play speaker clean sound!", ioe);
            return false;
        }
        return true;
    }

    private void stopPlaying() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mAudioManager.setParameters("status_earpiece_clean=off");
        mClearSpeakerPref.setEnabled(true);
        mClearSpeakerPref.setChecked(false);
    }
}
