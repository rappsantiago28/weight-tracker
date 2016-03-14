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

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.Profile;

/**
 * Created by rappsantiago28 on 3/6/16.
 */
public class ProfileCrudTest extends WeightTrackerBaseCrudTest {

    @Override
    protected ContentValues valuesToInsert() {
        final String tName = "Tester";
        final String tBirthday = Long.toString(new Date().getTime());
        final String tGender = "M";
        final String tHeight = "123";
        final String tWeight = "99";

        ContentValues values = new ContentValues();

        values.put(Profile.COL_NAME, tName);
        values.put(Profile.COL_BIRTHDAY, tBirthday);
        values.put(Profile.COL_GENDER, tGender);
        values.put(Profile.COL_HEIGHT, tHeight);
        values.put(Profile.COL_WEIGHT, tWeight);

        return values;
    }

    @Override
    protected long create(ContentValues valuesToInsert) {
        return mDb.insert(Profile.TABLE_NAME, null, valuesToInsert);
    }

    @Override
    protected Cursor read(long id) {
        Cursor cursor = mDb.query(
                Profile.TABLE_NAME,
                null,
                Profile._ID + " = ?",
                new String[]{Long.toString(id)},
                null, null, null);

        return cursor;
    }

    @Override
    protected String[] columns() {
        return new String[]{
                Profile.COL_NAME,
                Profile.COL_BIRTHDAY,
                Profile.COL_GENDER,
                Profile.COL_HEIGHT,
                Profile.COL_WEIGHT};
    }

    @Override
    protected ContentValues valuesToUpdate() {
        final String tNameUpdated = "Updated Tester";
        final String tBirthday = Long.toString(new Date().getTime());
        final String tGender = "M";
        final String tHeight = "123";
        final String tWeight = "99";

        ContentValues values = new ContentValues();

        values.put(Profile.COL_NAME, tNameUpdated);
        values.put(Profile.COL_BIRTHDAY, tBirthday);
        values.put(Profile.COL_GENDER, tGender);
        values.put(Profile.COL_HEIGHT, tHeight);
        values.put(Profile.COL_WEIGHT, tWeight);

        return values;
    }

    @Override
    protected int update(long id, ContentValues valuesToUpdate) {
        return mDb.update(Profile.TABLE_NAME, valuesToUpdate, Profile._ID + " = ?", new String[]{Long.toString(id)});
    }

    @Override
    protected int delete(long id) {
        return mDb.delete(Profile.TABLE_NAME, Profile._ID + " = ?", new String[]{Long.toString(id)});
    }
}
