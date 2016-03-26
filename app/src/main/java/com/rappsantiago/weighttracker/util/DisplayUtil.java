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

import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.profile.NameBirthdayGenderFragment;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by rappsantiago28 on 3/26/16.
 */
public final class DisplayUtil {

    public static String getReadableDate(long dateInMillis) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM dd, yyyy");
        return formatter.print(dateInMillis);
    }

    public static String getReadableGender(Context context, String gender) {

        String readableGender = "";

        switch (gender) {
            case Profile.GENDER_MALE:
                readableGender = context.getString(R.string.male);
                break;

            case Profile.GENDER_FEMALE:
                readableGender = context.getString(R.string.female);
                break;

            default:
                throw new IllegalArgumentException();
        }

        return readableGender;
    }

    public static String getFormattedWeight(Context context, double weightInKilograms, String weightUnit) {

        boolean autoConvert = false;

        // if no weight unit is specified, use weight unit in preference and auto-convert values
        if (null == weightUnit || !Util.isStringGood(weightUnit)) {
            weightUnit = PreferenceUtil.getWeightUnit(context);
            autoConvert = true;
        }

        String formattedWeight = "";

        switch (weightUnit) {
            case Profile.WEIGHT_UNIT_KILOGRAMS:
                formattedWeight = String.format(
                        "%.2f %s",
                        weightInKilograms,
                        context.getString(R.string.kilograms));
                break;

            case Profile.WEIGHT_UNIT_POUNDS:
                formattedWeight = String.format(
                        "%.2f %s",
                        autoConvert ? Util.kilogramsToPounds(weightInKilograms) : weightInKilograms,
                        context.getString(R.string.pounds));
                break;

            default:
                throw new IllegalArgumentException();
        }

        return formattedWeight;
    }

    public static String getFormattedHeight(Context context, double height, double heightInches, String heightUnit) {

        // if no height unit is specified, use height unit in preference
        if (null == heightUnit || !Util.isStringGood(heightUnit)) {
            heightUnit = PreferenceUtil.getHeightUnit(context);
        }

        String formattedHeight = "";

        switch (heightUnit) {
            case Profile.HEIGHT_UNIT_CENTIMETERS:
                formattedHeight = String.format("%.2f %s", height, context.getString(R.string.centimeters));
                break;

            case Profile.HEIGHT_UNIT_FOOT_INCHES:
                formattedHeight = String.format("%.2f %s, %.2f %s",
                        height, context.getString(R.string.foot),
                        heightInches, context.getString(R.string.inches));
                break;

            default:
                throw new IllegalArgumentException();
        }

        return formattedHeight;
    }

    public static String getFormattedHeight(Context context, double heightInCentimeters, String heightUnit) {

        // if no height unit is specified, use height unit in preference
        if (null == heightUnit || !Util.isStringGood(heightUnit)) {
            heightUnit = PreferenceUtil.getHeightUnit(context);
        }

        String formattedHeight = "";

        switch (heightUnit) {
            case Profile.HEIGHT_UNIT_CENTIMETERS:
                formattedHeight = String.format(
                        "%.2f %s",
                        heightInCentimeters,
                        context.getString(R.string.centimeters));
                break;

            case Profile.HEIGHT_UNIT_FOOT_INCHES:
                double[] heightData = Util.centimetersToFootInches(heightInCentimeters);
                formattedHeight = String.format("%.2f %s, %.2f %s",
                        heightData[0], context.getString(R.string.foot),
                        heightData[1], context.getString(R.string.inches));
                break;

            default:
                throw new IllegalArgumentException();
        }

        return formattedHeight;
    }

    public static String getReadableUnit(Context context, String unit) {
        String readableUnit = "";

        switch (unit) {
            case Profile.WEIGHT_UNIT_KILOGRAMS:
                readableUnit = context.getString(R.string.kilograms);
                break;

            case Profile.WEIGHT_UNIT_POUNDS:
                readableUnit = context.getString(R.string.pounds);
                break;

            case Profile.HEIGHT_UNIT_CENTIMETERS:
                readableUnit = context.getString(R.string.centimeters);
                break;

            case Profile.HEIGHT_UNIT_FOOT_INCHES:
                readableUnit = context.getString(R.string.foot_inches);
                break;

            default:
                throw new IllegalArgumentException();
        }

        return readableUnit;
    }

    private DisplayUtil() {}
}
