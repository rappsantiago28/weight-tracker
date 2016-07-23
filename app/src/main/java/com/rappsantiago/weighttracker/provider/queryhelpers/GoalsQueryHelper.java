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

package com.rappsantiago.weighttracker.provider.queryhelpers;

import android.content.Context;
import android.database.Cursor;

import com.rappsantiago.weighttracker.model.Goal;
import com.rappsantiago.weighttracker.provider.DbConstants;
import com.rappsantiago.weighttracker.provider.WeightTrackerContract;

/**
 * Created by ARKAS on 23/07/2016.
 */
public class GoalsQueryHelper extends QueryHelper {

    public GoalsQueryHelper(Context context) {
        super(context);
    }

    public Goal getCurrentGoal() {
        try (Cursor cursor = mContext.getContentResolver().query(
                WeightTrackerContract.Goal.CONTENT_URI,
                DbConstants.COLUMNS_GOAL,
                null, null, WeightTrackerContract.Goal._ID + " ASC LIMIT 1")) {

            if (null != cursor && cursor.moveToFirst()) {
                long goalId = cursor.getLong(DbConstants.IDX_GOAL_ID);
                double weight = cursor.getDouble(DbConstants.IDX_GOAL_TARGET_WEIGHT);
                double bodyFatIndex = cursor.getDouble(DbConstants.IDX_GOAL_TARGET_BODY_FAT_INDEX);
                long dueDate = cursor.getLong(DbConstants.IDX_GOAL_DUE_DATE);

                return new Goal.Builder(goalId, weight)
                        .targetBodyFatIndex(bodyFatIndex)
                        .dueDate(dueDate)
                        .build();
            }
        }

        return new Goal.Builder(0L, 0.0)
                .targetBodyFatIndex(0.0)
                .dueDate(0L)
                .build();
    }
}
