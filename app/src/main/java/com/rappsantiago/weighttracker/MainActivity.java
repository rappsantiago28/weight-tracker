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

package com.rappsantiago.weighttracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rappsantiago.weighttracker.profile.NameBirthdayGenderFragment;
import com.rappsantiago.weighttracker.profile.ProfileSetupActivity;
import com.rappsantiago.weighttracker.profile.WeightHeightFragment;
import com.rappsantiago.weighttracker.util.Util;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String PREF_KEY_PROFILE_SETUP = "MainActivity.PREF_KEY_PROFILE_SETUP";

    private SharedPreferences mSharedPrefs;

    private static final int REQUEST_PROFILE_SETUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isProfileSetupComplete = false;

        try (Cursor cursor = getContentResolver().query(Profile.CONTENT_URI, null, null, null, null)) {
            isProfileSetupComplete = cursor.getCount() > 0;
            if (cursor.moveToFirst()) {
                for (String col : cursor.getColumnNames()) {
                    Log.d(TAG, col + " : " + cursor.getString(cursor.getColumnIndex(col)));
                }
            }
        }

        if (!isProfileSetupComplete) {
            // launch profile setup page
            Intent setupProfileActivity = new Intent(this, ProfileSetupActivity.class);
            startActivityForResult(setupProfileActivity, REQUEST_PROFILE_SETUP);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PROFILE_SETUP:
                if (Activity.RESULT_OK == resultCode) {
                    // save profile
                    Bundle profileData = data.getExtras();

                    ContentValues values = new ContentValues();

                    String name = profileData.getString(NameBirthdayGenderFragment.KEY_NAME);
                    long birthdayInMillis = profileData.getLong(NameBirthdayGenderFragment.KEY_BIRTHDAY);
                    String gender = profileData.getString(NameBirthdayGenderFragment.KEY_GENDER);
                    double weight = profileData.getDouble(WeightHeightFragment.KEY_WEIGHT);
                    String weightUnit = profileData.getString(WeightHeightFragment.KEY_WEIGHT_UNIT);
                    double height = profileData.getDouble(WeightHeightFragment.KEY_HEIGHT);
                    String heightUnit = profileData.getString(WeightHeightFragment.KEY_HEIGHT_UNIT);

                    values.put(Profile.COL_NAME, name);
                    values.put(Profile.COL_BIRTHDAY, birthdayInMillis);
                    values.put(Profile.COL_GENDER, gender);

                    if (Profile.WEIGHT_UNIT_KILOGRAMS.equals(weightUnit)) {
                        values.put(Profile.COL_WEIGHT, weight);
                    } else {
                        values.put(Profile.COL_WEIGHT, Util.poundsToKilograms(weight));
                    }

                    if (Profile.HEIGHT_UNIT_CENTIMETERS.equals(heightUnit)) {
                        values.put(Profile.COL_HEIGHT, height);
                    } else {
                        double inches = profileData.getDouble(WeightHeightFragment.KEY_HEIGHT_INCHES);
                        values.put(Profile.COL_HEIGHT, Util.footInchesToCentimeters(height, inches));
                    }

                    getContentResolver().insert(Profile.CONTENT_URI, values);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown request code : " + requestCode);
        }
    }
}
