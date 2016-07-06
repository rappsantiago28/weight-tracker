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

package com.rappsantiago.weighttracker.provider;

/**
 * Created by rappsantiago28 on 3/26/16.
 */
public final class DbConstants {

    // [ Profile - START
    public static final int IDX_PROFILE_ID = 0;

    public static final int IDX_PROFILE_NAME = 1;

    public static final int IDX_PROFILE_BIRTHDAY = 2;

    public static final int IDX_PROFILE_GENDER = 3;

    public static final int IDX_PROFILE_HEIGHT = 4;

    public static final String[] COLUMNS_PROFILE = {
            WeightTrackerContract.Profile._ID,
            WeightTrackerContract.Profile.COL_NAME,
            WeightTrackerContract.Profile.COL_BIRTHDAY,
            WeightTrackerContract.Profile.COL_GENDER,
            WeightTrackerContract.Profile.COL_HEIGHT
    };
    // Profile - END ]

    // [ Progress - START
    public static final int IDX_PROGRESS_ID = 0;

    public static final int IDX_PROGRESS_NEW_WEIGHT = 1;

    public static final int IDX_PROGRESS_BODY_FAT_INDEX = 2;

    public static final int IDX_PROGRESS_TIMESTAMP = 3;

    public static final String[] COLUMNS_PROGRESS = {
            WeightTrackerContract.Progress._ID,
            WeightTrackerContract.Progress.COL_WEIGHT,
            WeightTrackerContract.Progress.COL_BODY_FAT_INDEX,
            WeightTrackerContract.Progress.COL_TIMESTAMP
    };
    // Progress - END ]

    // [ Goal - START
    public static final int IDX_GOAL_ID = 0;

    public static final int IDX_GOAL_TARGET_WEIGHT = 1;

    public static final int IDX_GOAL_DUE_DATE = 2;

    public static final String[] COLUMNS_GOAL = {
            WeightTrackerContract.Goal._ID,
            WeightTrackerContract.Goal.COL_TARGET_WEIGHT,
            WeightTrackerContract.Goal.COL_DUE_DATE
    };
    // Goal - END ]

    private DbConstants() {}
}
