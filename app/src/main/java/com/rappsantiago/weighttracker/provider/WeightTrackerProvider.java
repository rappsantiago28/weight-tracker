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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.Goal;
import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.Profile;
import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.Progress;

/**
 * Created by rappsantiago28 on 3/6/16.
 */
public class WeightTrackerProvider extends ContentProvider {

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int PROFILE = 1;
    private static final int PROFILE_ID = 2;
    private static final int PROGRESS = 3;
    private static final int PROGRESS_ID = 4;
    private static final int GOAL = 5;
    private static final int GOAL_ID = 6;

    static {
        MATCHER.addURI(WeightTrackerContract.AUTHORITY, Profile.TABLE_NAME, PROFILE);
        MATCHER.addURI(WeightTrackerContract.AUTHORITY, Profile.TABLE_NAME + "/#", PROFILE_ID);
        MATCHER.addURI(WeightTrackerContract.AUTHORITY, Progress.TABLE_NAME, PROGRESS);
        MATCHER.addURI(WeightTrackerContract.AUTHORITY, Progress.TABLE_NAME + "/#", PROGRESS_ID);
        MATCHER.addURI(WeightTrackerContract.AUTHORITY, Goal.TABLE_NAME, GOAL);
        MATCHER.addURI(WeightTrackerContract.AUTHORITY, Goal.TABLE_NAME + "/#", GOAL_ID);
    }

    private WeightTrackerDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new WeightTrackerDbHelper(getContext());
        return null != mDbHelper;
    }

    @Override
    public String getType(Uri uri) {

        String type = null;

        switch (MATCHER.match(uri)) {
            case PROFILE:
                type = Profile.CONTENT_TYPE;
                break;

            case PROFILE_ID:
                type = Profile.CONTENT_ITEM_TYPE;
                break;

            case PROGRESS:
                type = Progress.CONTENT_TYPE;
                break;

            case PROGRESS_ID:
                type = Progress.CONTENT_ITEM_TYPE;
                break;

            case GOAL:
                type = Goal.CONTENT_TYPE;
                break;

            case GOAL_ID:
                type = Goal.CONTENT_ITEM_TYPE;
                break;

            default:
                throw new IllegalArgumentException("GET_TYPE : Uri '" + uri.toString() + "' not supported.");
        }

        return type;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        switch (MATCHER.match(uri)) {
            case PROFILE:
                cursor = db.query(Profile.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PROGRESS:
                cursor = db.query(Progress.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PROGRESS_ID:
                cursor = db.query(
                        Progress.TABLE_NAME,
                        projection,
                        Progress._ID + " = ?",
                        new String[]{Long.toString(ContentUris.parseId(uri))},
                        null, null, sortOrder);
                break;

            case GOAL:
                cursor = db.query(Goal.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("QUERY : Uri '" + uri.toString() + "' not supported.");
        }

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long insertedId = -1;

        switch (MATCHER.match(uri)) {
            case PROFILE:
                insertedId = db.insert(Profile.TABLE_NAME, null, values);
                break;

            case PROGRESS:
                insertedId = db.insert(Progress.TABLE_NAME, null, values);
                break;

            case GOAL:
                insertedId = db.insert(Goal.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("INSERT : Uri '" + uri.toString() + "' not supported.");
        }

        Uri returnUri = null;

        if (-1 != insertedId) {
            returnUri = ContentUris.withAppendedId(uri, insertedId);
            getContext().getContentResolver().notifyChange(returnUri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int affectedRows = 0;

        switch (MATCHER.match(uri)) {
            case PROFILE_ID:
                affectedRows = deleteById(db, uri, Profile.TABLE_NAME);
                break;

            case PROGRESS_ID:
                affectedRows = deleteById(db, uri, Progress.TABLE_NAME);
                break;

            case GOAL_ID:
                affectedRows = deleteById(db, uri, Goal.TABLE_NAME);
                break;

            default:
                throw new IllegalArgumentException("DELETE : Uri '" + uri.toString() + "' not supported.");
        }

        if (0 < affectedRows) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int affectedRows = 0;

        switch (MATCHER.match(uri)) {
            case PROFILE:
                affectedRows = db.update(Profile.TABLE_NAME, values, selection, selectionArgs);
                break;

            case PROFILE_ID:
                affectedRows = updateById(db, uri, Profile.TABLE_NAME, values);
                break;

            case PROGRESS:
                affectedRows = db.update(Progress.TABLE_NAME, values, selection, selectionArgs);
                break;

            case PROGRESS_ID:
                affectedRows = updateById(db, uri, Progress.TABLE_NAME, values);
                break;

            case GOAL:
                affectedRows = db.update(Goal.TABLE_NAME, values, selection, selectionArgs);
                break;

            case GOAL_ID:
                affectedRows = updateById(db, uri, Goal.TABLE_NAME, values);
                break;

            default:
                throw new IllegalArgumentException("UPDATE : Uri '" + uri.toString() + "' not supported.");
        }

        if (0 < affectedRows) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }

    private int deleteById(SQLiteDatabase db, Uri uri, String tableName) {
        long idToDelete = ContentUris.parseId(uri);

        int affectedRows = db.delete(
                tableName,
                "_id = ?",
                new String[]{Long.toString(idToDelete)});

        return affectedRows;
    }

    private int updateById(SQLiteDatabase db, Uri uri, String tableName, ContentValues values) {
        long idToUpdate = ContentUris.parseId(uri);

        int affectedRows = db.update(
                tableName,
                values,
                "_id = ?",
                new String[]{Long.toString(idToUpdate)});

        return affectedRows;
    }
}
