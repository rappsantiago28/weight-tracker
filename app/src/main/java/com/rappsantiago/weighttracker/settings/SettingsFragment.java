/**
 *  Copyright 2016 Ralph Kristofelle A. Santiago
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 **/

package com.rappsantiago.weighttracker.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.util.DisplayUtil;
import com.rappsantiago.weighttracker.util.PreferenceUtil;

/**
 * Created by rappsantiago28 on 3/25/16.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initializePreferences();
    }

    private void initializePreferences() {
        Preference prefWeightUnit = findPreference(PreferenceUtil.PREF_KEY_WEIGHT_UNIT);
        initializePreference(prefWeightUnit);

        Preference prefHeightUnit = findPreference(PreferenceUtil.PREF_KEY_HEIGHT_UNIT);
        initializePreference(prefHeightUnit);
    }

    private void initializePreference(Preference p) {
        setSummary(p);
        p.setOnPreferenceChangeListener(this);
    }

    private void setSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference lp = (ListPreference) p;
            lp.setSummary(lp.getEntry());
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String prefKey = preference.getKey();

        switch (prefKey) {
            case PreferenceUtil.PREF_KEY_WEIGHT_UNIT:
            case PreferenceUtil.PREF_KEY_HEIGHT_UNIT:
                preference.setSummary(DisplayUtil.getReadableUnit(getActivity(), newValue.toString()));
                break;

            default:
                throw new IllegalArgumentException();
        }

        return true;
    }
}
