package com.rappsantiago.weighttracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.rappsantiago.weighttracker.provider.WeightTrackerDbHelper;

/**
 * Created by rappsantiago28 on 3/6/16.
 */
public abstract class WeightTrackerBaseCrudTest extends AndroidTestCase {

    protected WeightTrackerDbHelper mDbHelper;

    protected SQLiteDatabase mDb;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        mDbHelper = new WeightTrackerDbHelper(context);

        assertNotNull(mDbHelper);

        mDb = mDbHelper.getWritableDatabase();
    }

    public void testCrud() {
        ContentValues valuesToInsert = valuesToInsert();

        // CREATE
        long insertedId = create(valuesToInsert);

        assertTrue(-1 != insertedId);

        // READ
        try (Cursor cursor = read(insertedId)) {
            assertTrue(cursor.moveToFirst());

            String[] columns = columns();

            for (String column : columns) {
                String value = cursor.getString(cursor.getColumnIndex(column));
                assertEquals(valuesToInsert.get(column), value);
            }
        }

        // UPDATE
        ContentValues valuesToUpdate = valuesToUpdate();

        int updateResult = update(insertedId, valuesToUpdate);

        assertTrue(1 == updateResult);

        try (Cursor cursor = read(insertedId)) {
            assertTrue(cursor.moveToFirst());

            String[] columns = columns();

            for (String column : columns) {
                String value = cursor.getString(cursor.getColumnIndex(column));
                assertEquals(valuesToUpdate.get(column), value);
            }
        }

        // DELETE
        int deleteResult = delete(insertedId);

        assertTrue(1 == deleteResult);

        try (Cursor cursor = read(insertedId)) {
            assertFalse(cursor.moveToFirst());
        }
    }

    protected abstract ContentValues valuesToInsert();

    protected abstract long create(ContentValues valuesToInsert);

    protected abstract Cursor read(long id);

    protected abstract String[] columns();

    protected abstract ContentValues valuesToUpdate();

    protected abstract int update(long id, ContentValues valuesToUpdate);

    protected abstract int delete(long id);

    @Override
    protected void tearDown() throws Exception {
        mDb.close();
        mDbHelper.close();

        boolean result = mContext.deleteDatabase("test_" + WeightTrackerDbHelper.DB_NAME);

        assertTrue(result);

        super.tearDown();
    }
}
