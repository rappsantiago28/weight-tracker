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

import com.rappsantiago.weighttracker.profile.NameBirthdayGenderFragment;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by rappsantiago28 on 3/19/16.
 */
public final class Util {

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

    public static String getReadableDate(long dateInMillis) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM dd, yyyy");
        return formatter.print(dateInMillis);
    }

    private Util() {}
}
