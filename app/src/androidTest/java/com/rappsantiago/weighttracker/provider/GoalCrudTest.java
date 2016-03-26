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

import android.content.ContentValues;
import android.database.Cursor;

import com.rappsantiago.weighttracker.WeightTrackerBaseCrudTest;

import java.util.Date;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.Goal;

/**
 * Created by rappsantiago28 on 3/6/16.
 */
public class GoalCrudTest extends WeightTrackerBaseCrudTest {

    @Override
    protected ContentValues valuesToInsert() {
        final String tDueDate = Long.toString(new Date().getTime());
        final String tTargetWeight = "99";

        ContentValues values = new ContentValues();

        values.put(Goal.COL_TARGET_WEIGHT, tTargetWeight);
        values.put(Goal.COL_DUE_DATE, tDueDate);

        return values;
    }

    @Override
    protected long create(ContentValues valuesToInsert) {
        return mDb.insert(Goal.TABLE_NAME, null, valuesToInsert);
    }

    @Override
    protected Cursor read(long id) {
        Cursor cursor = mDb.query(
                Goal.TABLE_NAME,
                null,
                Goal._ID + " = ?",
                new String[]{Long.toString(id)},
                null, null, null);

        return cursor;
    }

    @Override
    protected String[] columns() {
        return new String[]{
                Goal.COL_TARGET_WEIGHT,
                Goal.COL_DUE_DATE};
    }

    @Override
    protected ContentValues valuesToUpdate() {
        final String tDueDate = Long.toString(new Date().getTime());
        final String tTargetWeight = "99";

        ContentValues values = new ContentValues();

        values.put(Goal.COL_TARGET_WEIGHT, tTargetWeight);
        values.put(Goal.COL_DUE_DATE, tDueDate);

        return values;
    }

    @Override
    protected int update(long id, ContentValues valuesToUpdate) {
        return mDb.update(Goal.TABLE_NAME, valuesToUpdate, Goal._ID + " = ?", new String[]{Long.toString(id)});
    }

    @Override
    protected int delete(long id) {
        return mDb.delete(Goal.TABLE_NAME, Goal._ID + " = ?", new String[]{Long.toString(id)});
    }
}
