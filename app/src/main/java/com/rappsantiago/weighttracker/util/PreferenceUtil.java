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

package com.rappsantiago.weighttracker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rappsantiago28 on 3/26/16.
 */
public final class PreferenceUtil {

    private static final String PREF_KEY_WEIGHT_UNIT = "pref_key_weight_unit";

    private static final String PREF_KEY_HEIGHT_UNIT = "pref_key_height_unit";

    private static final Set<String> VALID_WEIGHT_UNITS = new HashSet<>();

    private static final Set<String> VALID_HEIGHT_UNITS = new HashSet<>();

    static {
        VALID_WEIGHT_UNITS.add(Profile.WEIGHT_UNIT_KILOGRAMS);
        VALID_WEIGHT_UNITS.add(Profile.WEIGHT_UNIT_POUNDS);

        VALID_HEIGHT_UNITS.add(Profile.HEIGHT_UNIT_CENTIMETERS);
        VALID_HEIGHT_UNITS.add(Profile.HEIGHT_UNIT_FOOT_INCHES);
    }

    public static boolean setWeightUnit(Context context, String weightUnit) {
        if (!VALID_WEIGHT_UNITS.contains(weightUnit)) {
            throw new IllegalArgumentException();
        }

        return putString(context, PREF_KEY_WEIGHT_UNIT, weightUnit);
    }

    public static String getWeightUnit(Context context) {
        return getString(context, PREF_KEY_WEIGHT_UNIT);
    }

    public static boolean setHeightUnit(Context context, String heightUnit) {
        if (!VALID_HEIGHT_UNITS.contains(heightUnit)) {
            throw new IllegalArgumentException();
        }

        return putString(context, PREF_KEY_HEIGHT_UNIT, heightUnit);
    }

    public static String getHeightUnit(Context context) {
        return getString(context, PREF_KEY_HEIGHT_UNIT);
    }

    private static boolean putString(Context context, String key, String val) {
        if (!Util.isStringGood(key) || !Util.isStringGood(val)) {
            throw new IllegalArgumentException();
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.edit().putString(key, val).commit();
    }

    private static String getString(Context context, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, null);
    }

    private PreferenceUtil() {}
}
