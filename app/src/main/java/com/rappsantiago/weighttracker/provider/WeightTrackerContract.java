package com.rappsantiago.weighttracker.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rappsantiago28 on 3/6/16.
 */
public final class WeightTrackerContract {

    public static final String AUTHORITY = "com.rappsantiago.weighttracker.provider";

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private WeightTrackerContract() {
    }

    public static abstract class Profile implements BaseColumns {
        public static final String TABLE_NAME = "profile";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd." + AUTHORITY + "." + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd." + AUTHORITY + "." + TABLE_NAME;

        public static final String GENDER_MALE = "M";

        public static final String GENDER_FEMALE = "F";

        public static final String COL_NAME = "name";

        public static final String COL_BIRTHDAY = "birthday";

        public static final String COL_GENDER = "gender";

        public static final String COL_WEIGHT = "weight";

        public static final String COL_HEIGHT = "height";
    }

    public static abstract class Progress implements BaseColumns {
        public static final String TABLE_NAME = "progress";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd." + AUTHORITY + "." + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd." + AUTHORITY + "." + TABLE_NAME;

        public static final String COL_TIMESTAMP = "timestamp";

        public static final String COL_NEW_WEIGHT = "new_weight";
    }

    public static abstract class Goal implements BaseColumns {
        public static final String TABLE_NAME = "goal";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd." + AUTHORITY + "." + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd." + AUTHORITY + "." + TABLE_NAME;

        public static final String COL_TARGET_WEIGHT = "target_weight";

        public static final String COL_DUE_DATE = "due_date";

        public static final String COL_REMIDER_DURATION = "reminder_duration";

        public static final String COL_REMINDER_DAY = "reminder_day";
    }
}
