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

    public static int IDX_WEIGHT_PROGRESS_ID = 0;

    public static int IDX_WEIGHT_PROGRESS_INITIAL_WEIGHT = 1;

    public static int IDX_WEIGHT_PROGRESS_CURRENT_WEIGHT = 2;

    public static int IDX_WEIGHT_PROGRESS_PERCENT_COMPLETE = 3;

    public static int IDX_WEIGHT_PROGRESS_REMAINING = 4;

    public static int IDX_WEIGHT_PROGRESS_LOST = 5;

    public static final String[] COLS_WEIGHT_PROGRESS = {
            COL_ID,
            COL_INITIAL_WEIGHT,
            COL_CURRENT_WEIGHT,
            COL_PERCENT_COMPLETE,
            COL_REMAINING_WEIGHT,
            COL_WEIGHT_LOST
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

        double percentComplete = Util.getPercentComplete(
                initialWeight, currentWeight, targetWeight);

        double remainingWeight = Util.getRemainingWeight(currentWeight, targetWeight);

        double weightLost = Util.getWeightLost(initialWeight, currentWeight);

        matrixCursor.addRow(new Object[]{
                1,
                initialWeight,
                currentWeight,
                percentComplete,
                remainingWeight,
                weightLost
        });

        return matrixCursor;
    }
}
