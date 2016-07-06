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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;


/**
 * Created by rappsantiago28 on 3/6/16.
 */
public class WeightTrackerDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "weight_tracker.db";
    public static final int DB_VERSION = 1;

    private static final String SQL_CREATE_TABLE_PROFILE =
            "CREATE TABLE " + Profile.TABLE_NAME + " (" +
                    Profile._ID + " INTEGER PRIMARY KEY," +
                    Profile.COL_NAME + " TEXT," +
                    Profile.COL_BIRTHDAY + " INTEGER," +
                    Profile.COL_GENDER + " TEXT," +
                    Profile.COL_HEIGHT + " REAL);";

    private static final String SQL_CREATE_TABLE_PROGRESS =
            "CREATE TABLE " + Progress.TABLE_NAME + " (" +
                    Progress._ID + " INTEGER PRIMARY KEY," +
                    Progress.COL_WEIGHT + " REAL," +
                    Progress.COL_BODY_FAT_INDEX + " REAL," +
                    Progress.COL_TIMESTAMP + " INTEGER);";

    private static final String SQL_CREATE_TABLE_GOAL =
            "CREATE TABLE " + Goal.TABLE_NAME + " (" +
                    Goal._ID + " INTEGER PRIMARY KEY," +
                    Goal.COL_TARGET_WEIGHT + " REAL," +
                    Goal.COL_TARGET_BODY_FAT_INDEX + " REAL," +
                    Goal.COL_DUE_DATE + " INTEGER)";

    private static final String SQL_DROP_TABLE_PROFILE = "DROP TABLE IF EXISTS " + Profile.TABLE_NAME;

    private static final String SQL_DROP_TABLE_PROGRESS = "DROP TABLE IF EXISTS " + Progress.TABLE_NAME;

    private static final String SQL_DROP_TABLE_GOAL = "DROP TABLE IF EXISTS " + Goal.TABLE_NAME;

    public WeightTrackerDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_PROFILE);
        db.execSQL(SQL_CREATE_TABLE_PROGRESS);
        db.execSQL(SQL_CREATE_TABLE_GOAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE_PROFILE);
        db.execSQL(SQL_DROP_TABLE_PROGRESS);
        db.execSQL(SQL_DROP_TABLE_GOAL);
        onCreate(db);
    }
}
