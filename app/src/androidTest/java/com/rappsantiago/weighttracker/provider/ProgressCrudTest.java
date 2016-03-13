package com.rappsantiago.weighttracker.provider;

import android.content.ContentValues;
import android.database.Cursor;

import com.rappsantiago.weighttracker.WeightTrackerBaseCrudTest;

import java.util.Date;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.Progress;

/**
 * Created by rappsantiago28 on 3/6/16.
 */
public class ProgressCrudTest extends WeightTrackerBaseCrudTest {

    @Override
    protected ContentValues valuesToInsert() {
        final String tTimestamp = Long.toString(new Date().getTime());
        final String tWeight = "99";

        ContentValues values = new ContentValues();

        values.put(Progress.COL_NEW_WEIGHT, tWeight);
        values.put(Progress.COL_TIMESTAMP, tTimestamp);

        return values;
    }

    @Override
    protected long create(ContentValues valuesToInsert) {
        return mDb.insert(Progress.TABLE_NAME, null, valuesToInsert);
    }

    @Override
    protected Cursor read(long id) {
        Cursor cursor = mDb.query(
                Progress.TABLE_NAME,
                null,
                Progress._ID + " = ?",
                new String[]{Long.toString(id)},
                null, null, null);

        return cursor;
    }

    @Override
    protected String[] columns() {
        return new String[]{
                Progress.COL_NEW_WEIGHT,
                Progress.COL_TIMESTAMP};
    }

    @Override
    protected ContentValues valuesToUpdate() {
        final String tTimestamp = Long.toString(new Date().getTime());
        final String tWeight = "123";

        ContentValues values = new ContentValues();

        values.put(Progress.COL_NEW_WEIGHT, tWeight);
        values.put(Progress.COL_TIMESTAMP, tTimestamp);

        return values;
    }

    @Override
    protected int update(long id, ContentValues valuesToUpdate) {
        return mDb.update(Progress.TABLE_NAME, valuesToUpdate, Progress._ID + " = ?", new String[]{Long.toString(id)});
    }

    @Override
    protected int delete(long id) {
        return mDb.delete(Progress.TABLE_NAME, Progress._ID + " = ?", new String[]{Long.toString(id)});
    }
}
