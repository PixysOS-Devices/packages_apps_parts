/**
 * Copyright (C) 2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lineageos.settings.device.thermal;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.R;
import org.lineageos.settings.device.widget.SeekBarPreference;

public class TouchSettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences mSharedPrefs;
    private SeekBarPreference mTouchSensitivity;
    private SeekBarPreference mTouchResponse;
    private SeekBarPreference mTouchResistant;
    private SeekBarPreference mTouchPerfLevel;
    private SwitchPreference mGameMode;

    private String packageName = "";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.touch_settings);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        Bundle bundle = getArguments();
        String appName = "";
        if (bundle != null) {
            appName = bundle.getString("appName", "");
            packageName = bundle.getString("packageName", "");
        }

        getActivity().setTitle(appName);

        mGameMode = (SwitchPreference) findPreference(Constants.KEY_TOUCH_GAME_MODE);
        mTouchPerfLevel = (SeekBarPreference) findPreference(Constants.KEY_TOUCH_PERF_LEVEL);
        mTouchResistant = (SeekBarPreference) findPreference(Constants.KEY_TOUCH_RESISTANT);
        mTouchResponse = (SeekBarPreference) findPreference(Constants.KEY_TOUCH_RESPONSE);
        mTouchSensitivity = (SeekBarPreference) findPreference(Constants.KEY_TOUCH_SENSITIVITY);
        updateDefaults();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
        if (Constants.KEY_TOUCH_GAME_MODE.equals(key)) {
            updateTouchModes(sharedPrefs.getBoolean(key, false) ? 1 : 0,
                    Constants.TOUCH_GAME_MODE);
            mTouchPerfLevel.setEnabled(sharedPrefs.getBoolean(key, false));
            mTouchSensitivity.setEnabled(sharedPrefs.getBoolean(key, false));
            mTouchResponse.setEnabled(sharedPrefs.getBoolean(key, false));
            mTouchResistant.setEnabled(sharedPrefs.getBoolean(key, false));
        } else if (Constants.KEY_TOUCH_PERF_LEVEL.equals(key)) {
            updateTouchModes(sharedPrefs.getInt(key, 0), Constants.TOUCH_PERF_LEVEL);
        } else if (Constants.KEY_TOUCH_RESPONSE.equals(key)) {
            updateTouchModes(sharedPrefs.getInt(key, 0), Constants.TOUCH_RESPONSE);
        } else if (Constants.KEY_TOUCH_SENSITIVITY.equals(key)) {
            updateTouchModes(sharedPrefs.getInt(key, 0), Constants.TOUCH_SENSITIVITY);
        } else if (Constants.KEY_TOUCH_RESISTANT.equals(key)) {
            updateTouchModes(sharedPrefs.getInt(key, 0), Constants.TOUCH_RESISTANT);
        }
    }

    private void updateDefaults() {
        String[] values = getTouchValues().split(",");
        mGameMode.setChecked(Integer.parseInt(values[Constants.TOUCH_GAME_MODE]) == 1);
        mTouchPerfLevel.setProgress(Integer.parseInt(values[Constants.TOUCH_PERF_LEVEL]));
        mTouchResponse.setProgress(Integer.parseInt(values[Constants.TOUCH_RESPONSE]));
        mTouchSensitivity.setProgress(Integer.parseInt(values[Constants.TOUCH_SENSITIVITY]));
        mTouchResistant.setProgress(Integer.parseInt(values[Constants.TOUCH_RESISTANT]));

        mTouchPerfLevel.setEnabled(Integer.parseInt(values[Constants.TOUCH_GAME_MODE]) == 1);
        mTouchResponse.setEnabled(Integer.parseInt(values[Constants.TOUCH_GAME_MODE]) == 1);
        mTouchSensitivity.setEnabled(Integer.parseInt(values[Constants.TOUCH_GAME_MODE]) == 1);
        mTouchResistant.setEnabled(Integer.parseInt(values[Constants.TOUCH_GAME_MODE]) == 1);
    }

    private void writeTouchValues(String modes) {
        mSharedPrefs.edit().putString(packageName, modes).apply();
    }

    public String getTouchValues() {
        String values = mSharedPrefs.getString(packageName, null);
        if (values == null || values.isEmpty()) {
            values = "0,0,0,0,0";
        }
        writeTouchValues(values);
        return values;
    }

    public void updateTouchModes(int value, int mode) {
        String[] values = getTouchValues().split(",");
        values[mode] = String.valueOf(value);
        String finalValues = values[Constants.TOUCH_GAME_MODE] + "," + values[Constants.TOUCH_PERF_LEVEL] + "," + values[Constants.TOUCH_RESPONSE] + ","
                + values[Constants.TOUCH_SENSITIVITY] + "," + values[Constants.TOUCH_RESISTANT];
        writeTouchValues(finalValues);
    }
}

