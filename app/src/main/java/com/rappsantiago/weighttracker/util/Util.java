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
import android.database.Cursor;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

import org.joda.time.LocalDate;

import java.util.Set;

/**
 * Created by rappsantiago28 on 3/19/16.
 */
public final class Util {

    // TODO : Refactor -> Move methods with query to 'query helper classes'
    private Util() {
    }

    public static double parseDouble(String str, double defaultVal) {
        return Util.isStringGood(str) ? Double.parseDouble(str) : defaultVal;
    }

    public static boolean isStringGood(String str) {
        return (null != str && !str.trim().isEmpty());
    }

    public static double poundsToKilograms(double pounds) {
        return pounds / 2.2;
    }

    public static double kilogramsToPounds(double kilograms) {
        return kilograms * 2.2;
    }

    public static double footInchesToCentimeters(double foot, double inches) {
        double footInCentimeters = foot * 30.48;
        double inchInCentimeters = inches * 2.54;
        return footInCentimeters + inchInCentimeters;
    }

    public static double[] centimetersToFootInches(double centimeters) {
        double footOnly = centimeters / 30.48;
        double foot = Math.floor(footOnly);
        double inches = (footOnly - foot) * 12;
        return new double[]{foot, inches};
    }

    public static long getCurrentDateInMillis() {
        LocalDate localDate = new LocalDate();
        return localDate.toDate().getTime();
    }

    public static double getInitialWeight(Context context) {

        try (Cursor cursor = context.getContentResolver().query(
                Progress.CONTENT_URI,
                new String[]{Progress.COL_WEIGHT},
                null, null, Progress.COL_TIMESTAMP + " ASC LIMIT 1")) {

            if (cursor.moveToFirst()) {
                double initialWeight = cursor.getDouble(0);
                return initialWeight;
            }
        }

        return 0.0;
    }

    public static double getCurrentWeight(Context context) {

        try (Cursor cursor = context.getContentResolver().query(
                Progress.CONTENT_URI,
                new String[]{Progress.COL_WEIGHT},
                null, null, Progress.COL_TIMESTAMP + " DESC LIMIT 1")) {

            if (cursor.moveToFirst()) {
                double currentWeight = cursor.getDouble(0);
                return currentWeight;
            }
        }

        return 0.0;
    }

    public static double getTargetWeight(Context context) {

        try (Cursor cursor = context.getContentResolver().query(
                Goal.CONTENT_URI,
                new String[]{Goal.COL_TARGET_WEIGHT},
                null, null, Goal._ID + " ASC LIMIT 1")) {

            if (cursor.moveToFirst()) {
                double targetWeight = cursor.getDouble(0);
                return targetWeight;
            }
        }

        return 0.0;
    }

    public static double getTargetBodyFatIndex(Context context) {

        try (Cursor cursor = context.getContentResolver().query(
                Goal.CONTENT_URI,
                new String[]{Goal.COL_TARGET_BODY_FAT_INDEX},
                null, null, Goal._ID + " ASC LIMIT 1")) {

            if (cursor.moveToFirst()) {
                double targetBodyFatIndex = cursor.getDouble(0);
                return targetBodyFatIndex;
            }
        }

        return 0.0;
    }

    public static double computePercentComplete(double initialValue, double currentValue, double targetValue) {

        double totalWeightDiff = Math.abs(initialValue - targetValue);
        double weightLost = computeValueLost(initialValue, currentValue);

        return Math.abs(weightLost / totalWeightDiff);
    }

    public static double computeValueLost(double initialValue, double currentValue) {
        return Math.abs(initialValue - currentValue);
    }

    public static double computeRemainingValue(double currentValue, double targetValue) {
        return Math.abs(currentValue - targetValue);
    }

    public static double getInitialBodyFatIndex(Context context) {

        try (Cursor cursor = context.getContentResolver().query(
                Progress.CONTENT_URI,
                new String[]{Progress.COL_BODY_FAT_INDEX},
                Progress.COL_BODY_FAT_INDEX + " > 0", null,
                Progress.COL_TIMESTAMP + " ASC LIMIT 1")) {

            if (cursor.moveToFirst()) {
                double initialBodyFatIndex = cursor.getDouble(0);
                return initialBodyFatIndex;
            }
        }

        return 0.0;
    }

    public static double getCurrentBodyFatIndex(Context context) {

        try (Cursor cursor = context.getContentResolver().query(
                Progress.CONTENT_URI,
                new String[]{Progress.COL_BODY_FAT_INDEX},
                Progress.COL_BODY_FAT_INDEX + " > 0", null,
                Progress.COL_TIMESTAMP + " DESC LIMIT 1")) {

            if (cursor.moveToFirst()) {
                double currentBodyFatIndex = cursor.getDouble(0);
                return currentBodyFatIndex;
            }
        }

        return 0.0;
    }

    public static long[] convertLongSetToArray(Set<Long> ids) {
        long[] arrIds = new long[ids.size()];
        int idx = 0;
        for (long id : ids) {
            arrIds[idx++] = id;
        }
        return arrIds;
    }

    public static void hideSoftKeyboard(Context context, View view) {
        if (null != view) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
