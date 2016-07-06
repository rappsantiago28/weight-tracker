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

        public static final String WEIGHT_UNIT_KILOGRAMS = "Kg";

        public static final String WEIGHT_UNIT_POUNDS = "Lb";

        public static final String HEIGHT_UNIT_CENTIMETERS = "Cm";

        public static final String HEIGHT_UNIT_FOOT_INCHES = "FtIn";

        public static final String COL_NAME = "name";

        public static final String COL_BIRTHDAY = "birthday";

        public static final String COL_GENDER = "gender";

        // height will be stored as centimeters
        public static final String COL_HEIGHT = "height";
    }

    public static abstract class Progress implements BaseColumns {
        public static final String TABLE_NAME = "progress";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd." + AUTHORITY + "." + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd." + AUTHORITY + "." + TABLE_NAME;

        // weight will be stored as kilograms
        public static final String COL_WEIGHT = "weight";

        public static final String COL_BODY_FAT_INDEX = "body_fat_index";

        public static final String COL_TIMESTAMP = "timestamp";
    }

    public static abstract class Goal implements BaseColumns {
        public static final String TABLE_NAME = "goal";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd." + AUTHORITY + "." + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd." + AUTHORITY + "." + TABLE_NAME;

        public static final String COL_TARGET_WEIGHT = "target_weight";

        public static final String COL_TARGET_BODY_FAT_INDEX = "target_body_fat_index";

        public static final String COL_DUE_DATE = "due_date";
    }
}
