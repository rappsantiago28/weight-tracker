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

package com.rappsantiago.weighttracker.profile;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.content.CursorLoader;

import com.rappsantiago.weighttracker.util.Util;

/**
 * Created by rappsantiago28 on 4/10/16.
 */
public class WeightProgressCursorLoader extends CursorLoader {

    public static String COL_ID = "_id";

    public static String COL_INITIAL_WEIGHT = "initial_weight";

    public static String COL_CURRENT_WEIGHT = "current_weight";

    public static String COL_PERCENT_COMPLETE = "percent_complete";

    public static String COL_REMAINING_WEIGHT = "remaining_weight";

    public static String COL_WEIGHT_LOST = "weight_lost";

    public static String COL_INITIAL_BODY_FAT_INDEX = "initial_body_fat_index";

    public static String COL_CURRENT_BODY_FAT_INDEX = "current_body_fat_index";

    public static String COL_REMAINING_BODY_FAT_INDEX = "remaining_body_fat_index";

    public static String COL_CURRENT_FAT_MASS = "current_fat_mass";

    public static String COL_CURRENT_MUSCLE_MASS = "current_muscle_mass";

    public static int IDX_WEIGHT_PROGRESS_ID = 0;

    public static int IDX_WEIGHT_PROGRESS_INITIAL_WEIGHT = 1;

    public static int IDX_WEIGHT_PROGRESS_CURRENT_WEIGHT = 2;

    public static int IDX_WEIGHT_PROGRESS_PERCENT_COMPLETE = 3;

    public static int IDX_WEIGHT_PROGRESS_REMAINING_WEIGHT = 4;

    public static int IDX_WEIGHT_PROGRESS_LOST = 5;

    public static int IDX_WEIGHT_PROGRESS_INITIAL_BODY_FAT_INDEX = 6;

    public static int IDX_WEIGHT_PROGRESS_CURRENT_BODY_FAT_INDEX = 7;

    public static int IDX_WEIGHT_PROGRESS_REMAINING_BODY_FAT_INDEX = 8;

    public static int IDX_WEIGHT_PROGRESS_CURRENT_FAT_MASS = 9;

    public static int IDX_WEIGHT_PROGRESS_CURRENT_MUSCLE_MASS = 10;

    public static final String[] COLS_WEIGHT_PROGRESS = {
            COL_ID,
            COL_INITIAL_WEIGHT,
            COL_CURRENT_WEIGHT,
            COL_PERCENT_COMPLETE,
            COL_REMAINING_WEIGHT,
            COL_WEIGHT_LOST,
            COL_INITIAL_BODY_FAT_INDEX,
            COL_CURRENT_BODY_FAT_INDEX,
            COL_REMAINING_BODY_FAT_INDEX,
            COL_CURRENT_FAT_MASS,
            COL_CURRENT_MUSCLE_MASS
    };

    public WeightProgressCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected Cursor onLoadInBackground() {

        MatrixCursor matrixCursor = new MatrixCursor(COLS_WEIGHT_PROGRESS);

        double initialWeight = Util.getInitialWeight(getContext());

        double currentWeight = Util.getCurrentWeight(getContext());

        double targetWeight = Util.getTargetWeight(getContext());

        double percentComplete = Util.computePercentComplete(
                initialWeight, currentWeight, targetWeight);

        double remainingWeight = Util.computeRemainingValue(currentWeight, targetWeight);

        double weightLost = Util.computeValueLost(initialWeight, currentWeight);

        double initialBodyFatIndex = Util.getInitialBodyFatIndex(getContext());

        double currentBodyFatIndex = Util.getCurrentBodyFatIndex(getContext());

        double targetBodyFatIndex = Util.getTargetBodyFatIndex(getContext());

        double remainingBodyFatIndex = Util.computeRemainingValue(currentBodyFatIndex, targetBodyFatIndex);

        double currentFatMass = (currentBodyFatIndex / 100) * currentWeight;

        double currentMuscleMass = ((100 - currentBodyFatIndex) / 100) * currentWeight;

        matrixCursor.addRow(new Object[]{
                1,
                initialWeight,
                currentWeight,
                percentComplete,
                remainingWeight,
                weightLost,
                initialBodyFatIndex,
                currentBodyFatIndex,
                remainingBodyFatIndex,
                currentFatMass,
                currentMuscleMass
        });

        return matrixCursor;
    }
}
