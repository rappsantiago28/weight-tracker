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

package com.rappsantiago.weighttracker.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import com.rappsantiago.weighttracker.profile.setup.NameBirthdayGenderFragment;
import com.rappsantiago.weighttracker.profile.setup.TargetWeightFragment;
import com.rappsantiago.weighttracker.profile.setup.WeightHeightFragment;
import com.rappsantiago.weighttracker.provider.WeightTrackerContract;
import com.rappsantiago.weighttracker.util.PreferenceUtil;
import com.rappsantiago.weighttracker.util.Util;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by ARKAS on 23/05/2016.
 * Based from ContactsSaveService.java
 */
public class WeightTrackerSaveService extends IntentService {

    private static final String TAG = WeightTrackerSaveService.class.getSimpleName();

    private static final String PREFIX = "com.rappsantiago.weighttracker.service.WeightTrackerSaveService";

    // actions
    private static final String ACTION_SETUP_PROFILE = PREFIX + ".ACTION_SETUP_PROFILE";

    private static final String ACTION_INSERT_PROFILE = PREFIX + ".ACTION_INSERT_PROFILE";

    private static final String ACTION_INSERT_PROGRESS = PREFIX + ".ACTION_INSERT_PROGRESS";

    private static final String ACTION_UPDATE_PROGRESS = PREFIX + ".ACTION_UPDATE_PROGRESS";

    private static final String ACTION_DELETE_PROGRESS = PREFIX + ".ACTION_DELETE_PROGRESS";

    private static final String ACTION_BULK_DELETE_PROGRESS = PREFIX + ".ACTION_BULK_DELETE_PROGRESS";

    private static final String ACTION_INSERT_GOAL = PREFIX + ".ACTION_INSERT_GOAL";

    // extras
    private static final String EXTRA_CALLBACK_INTENT = PREFIX + ".EXTRA_CALLBACK_INTENT";

    private static final String EXTRA_PROFILE_NAME = PREFIX + ".EXTRA_PROFILE_NAME";

    private static final String EXTRA_PROFILE_BIRTHDAY = PREFIX + ".EXTRA_PROFILE_BIRTHDAY";

    private static final String EXTRA_PROFILE_GENDER = PREFIX + ".EXTRA_PROFILE_GENDER";

    private static final String EXTRA_PROFILE_HEIGHT = PREFIX + ".EXTRA_PROFILE_HEIGHT";

    private static final String EXTRA_PROFILE_HEIGHT_INCHES = PREFIX + ".EXTRA_PROFILE_HEIGHT_INCHES";

    private static final String EXTRA_PROGRESS_ID = PREFIX + ".EXTRA_PROGRESS_ID";

    private static final String EXTRA_PROGRESS_IDS = PREFIX + ".EXTRA_PROGRESS_IDS";

    private static final String EXTRA_PROGRESS_WEIGHT = PREFIX + ".EXTRA_PROGRESS_WEIGHT";

    private static final String EXTRA_PROGRESS_DATE = PREFIX + ".EXTRA_PROGRESS_DATE";

    private static final String EXTRA_GOAL_DUE_DATE = PREFIX + ".EXTRA_GOAL_DUE_DATE";

    private static final String EXTRA_GOAL_TARGET_WEIGHT = PREFIX + ".EXTRA_GOAL_TARGET_WEIGHT";

    private static final String EXTRA_WEIGHT_UNIT = PREFIX + ".EXTRA_WEIGHT_UNIT";

    private static final String EXTRA_HEIGHT_UNIT = PREFIX + ".EXTRA_HEIGHT_UNIT";

    private static final CopyOnWriteArrayList<Listener> sListeners = new CopyOnWriteArrayList<>();

    private Handler mMainHandler;

    public WeightTrackerSaveService() {
        super(TAG);
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case ACTION_SETUP_PROFILE:
                setupProfile(intent);
                break;

            case ACTION_INSERT_PROFILE:
                insertProfile(intent, true);
                break;

            case ACTION_INSERT_PROGRESS:
                insertProgress(intent, true);
                break;

            case ACTION_UPDATE_PROGRESS:
                updateProgress(intent);
                break;

            case ACTION_DELETE_PROGRESS:
                deleteProgress(intent);
                break;

            case ACTION_BULK_DELETE_PROGRESS:
                bulkDeleteProgress(intent);
                break;

            case ACTION_INSERT_GOAL:
                insertGoal(intent, true);
                break;

            default:
                throw new IllegalArgumentException("Unknown action (" + action + ")");
        }
    }

    /**
     * Action Implementations
     */
    private void setupProfile(Intent intent) {
        insertProfile(intent, false);
        insertProgress(intent, false);
        insertGoal(intent, false);

        // set default weight and height unit of measurement
        String weightUnit = intent.getStringExtra(EXTRA_WEIGHT_UNIT);
        String heightUnit = intent.getStringExtra(EXTRA_HEIGHT_UNIT);
        PreferenceUtil.setWeightUnit(this, weightUnit);
        PreferenceUtil.setHeightUnit(this, heightUnit);

        Intent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
        deliverCallback(callbackIntent);
    }

    private void insertProfile(Intent intent, boolean deliverCallback) {
        String name = intent.getStringExtra(EXTRA_PROFILE_NAME);
        long birthdayInMillis = intent.getLongExtra(EXTRA_PROFILE_BIRTHDAY, 0L);
        String gender = intent.getStringExtra(EXTRA_PROFILE_GENDER);
        double height = intent.getDoubleExtra(EXTRA_PROFILE_HEIGHT, 0.0);
        String heightUnit = intent.getStringExtra(EXTRA_HEIGHT_UNIT);

        ContentValues profileValues = new ContentValues();

        profileValues.put(Profile.COL_NAME, name);
        profileValues.put(Profile.COL_BIRTHDAY, birthdayInMillis);
        profileValues.put(Profile.COL_GENDER, gender);

        switch (heightUnit) {
            case Profile.HEIGHT_UNIT_CENTIMETERS:
                profileValues.put(Profile.COL_HEIGHT, height);
                break;

            case Profile.HEIGHT_UNIT_FOOT_INCHES:
                double inches = intent.getDoubleExtra(EXTRA_PROFILE_HEIGHT_INCHES, 0.0);
                profileValues.put(Profile.COL_HEIGHT, Util.footInchesToCentimeters(height, inches));
                break;

            default:
                throw new IllegalArgumentException("Unknown height unit (" + heightUnit + ")");
        }

        Uri profileUri = getContentResolver().insert(Profile.CONTENT_URI, profileValues);

        if (deliverCallback && null != profileUri) {
            Intent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
            deliverCallback(callbackIntent);
        }
    }

    private void insertProgress(Intent intent, boolean deliverCallback) {
        double newWeight = intent.getDoubleExtra(EXTRA_PROGRESS_WEIGHT, 0.0);
        long date = intent.getLongExtra(EXTRA_PROGRESS_DATE, 0L);
        String weightUnit = intent.getStringExtra(EXTRA_WEIGHT_UNIT);

        ContentValues values = new ContentValues();

        switch (weightUnit) {
            case Profile.WEIGHT_UNIT_KILOGRAMS:
                values.put(Progress.COL_NEW_WEIGHT, newWeight);
                break;

            case Profile.WEIGHT_UNIT_POUNDS:
                values.put(Progress.COL_NEW_WEIGHT, Util.poundsToKilograms(newWeight));
                break;

            default:
                throw new IllegalArgumentException("Unknown weight unit (" + weightUnit + ")");
        }

        values.put(Progress.COL_TIMESTAMP, date);

        Uri result = getContentResolver().insert(Progress.CONTENT_URI, values);

        if (deliverCallback && null != result) {
            Intent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
            deliverCallback(callbackIntent);
        }
    }

    private void updateProgress(Intent intent) {
        long progressId = intent.getLongExtra(EXTRA_PROGRESS_ID, 0L);
        double newWeight = intent.getDoubleExtra(EXTRA_PROGRESS_WEIGHT, 0.0);
        long date = intent.getLongExtra(EXTRA_PROGRESS_DATE, 0L);
        String weightUnit = intent.getStringExtra(EXTRA_WEIGHT_UNIT);

        Uri updateProgressUri = ContentUris.withAppendedId(
                Progress.CONTENT_URI, progressId);

        ContentValues values = new ContentValues();

        switch (weightUnit) {
            case Profile.WEIGHT_UNIT_KILOGRAMS:
                values.put(Progress.COL_NEW_WEIGHT, newWeight);
                break;

            case Profile.WEIGHT_UNIT_POUNDS:
                values.put(Progress.COL_NEW_WEIGHT, Util.poundsToKilograms(newWeight));
                break;

            default:
                throw new IllegalArgumentException("Unknown weight unit (" + weightUnit + ")");
        }

        values.put(Progress.COL_TIMESTAMP, date);

        int result = getContentResolver().update(updateProgressUri, values, null, null);

        if (0 < result) {
            Intent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
            deliverCallback(callbackIntent);
        }
    }

    private void deleteProgress(Intent intent) {
        long progressId = intent.getLongExtra(EXTRA_PROGRESS_ID, 0L);
        Uri deleteProgressUri = ContentUris.withAppendedId(
                Progress.CONTENT_URI, progressId);
        int result = getContentResolver().delete(deleteProgressUri, null, null);

        if (0 < result) {
            Intent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
            deliverCallback(callbackIntent);
        }
    }

    private void bulkDeleteProgress(Intent intent) {
        long[] progressIds = intent.getLongArrayExtra(EXTRA_PROGRESS_IDS);

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        for (long progressId : progressIds) {
            Uri deleteProgressUri = ContentUris.withAppendedId(
                    Progress.CONTENT_URI, progressId);
            ops.add(ContentProviderOperation.newDelete(deleteProgressUri)
                    .build());
        }

        ContentProviderResult[] results = null;
        try {
            results = getContentResolver().applyBatch(WeightTrackerContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        if (0 < results.length) {
            Intent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
            deliverCallback(callbackIntent);
        }
    }

    private void insertGoal(Intent intent, boolean deliverCallback) {
        double targetWeight = intent.getDoubleExtra(EXTRA_GOAL_TARGET_WEIGHT, 0.0);
        long dueDateInMillis = intent.getLongExtra(EXTRA_GOAL_DUE_DATE, 0L);
        String weightUnit = intent.getStringExtra(EXTRA_WEIGHT_UNIT);

        ContentValues goalValues = new ContentValues();

        switch (weightUnit) {
            case Profile.WEIGHT_UNIT_KILOGRAMS:
                goalValues.put(Goal.COL_TARGET_WEIGHT, targetWeight);
                break;

            case Profile.WEIGHT_UNIT_POUNDS:
                goalValues.put(Goal.COL_TARGET_WEIGHT, Util.poundsToKilograms(targetWeight));
                break;

            default:
                throw new IllegalArgumentException("Unknown weight unit (" + weightUnit + ")");
        }

        goalValues.put(Goal.COL_DUE_DATE, dueDateInMillis);

        Uri goalUri = getContentResolver().insert(Goal.CONTENT_URI, goalValues);

        if (deliverCallback && null != goalUri) {
            Intent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
            deliverCallback(callbackIntent);
        }
    }


    /**
     * Intent Builders
     */
    public static Intent createSetupProfileIntent(Context context, Bundle profileData, Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent setupProfileIntent = new Intent(context, WeightTrackerSaveService.class);
        setupProfileIntent.setAction(ACTION_SETUP_PROFILE);

        String name = profileData.getString(NameBirthdayGenderFragment.KEY_NAME);
        long birthdayInMillis = profileData.getLong(NameBirthdayGenderFragment.KEY_BIRTHDAY);
        String gender = profileData.getString(NameBirthdayGenderFragment.KEY_GENDER);
        double weight = profileData.getDouble(WeightHeightFragment.KEY_WEIGHT);
        String weightUnit = profileData.getString(WeightHeightFragment.KEY_WEIGHT_UNIT);
        double height = profileData.getDouble(WeightHeightFragment.KEY_HEIGHT);
        double inches = profileData.getDouble(WeightHeightFragment.KEY_HEIGHT_INCHES);
        String heightUnit = profileData.getString(WeightHeightFragment.KEY_HEIGHT_UNIT);
        double targetWeight = profileData.getDouble(TargetWeightFragment.KEY_TARGET_WEIGHT);
        long dueDateInMillis = profileData.getLong(TargetWeightFragment.KEY_DUE_DATE);

        setupProfileIntent.putExtra(EXTRA_PROFILE_NAME, name);
        setupProfileIntent.putExtra(EXTRA_PROFILE_BIRTHDAY, birthdayInMillis);
        setupProfileIntent.putExtra(EXTRA_PROFILE_GENDER, gender);
        setupProfileIntent.putExtra(EXTRA_PROFILE_HEIGHT, height);
        setupProfileIntent.putExtra(EXTRA_PROFILE_HEIGHT_INCHES, inches);
        setupProfileIntent.putExtra(EXTRA_HEIGHT_UNIT, heightUnit);

        setupProfileIntent.putExtra(EXTRA_PROGRESS_WEIGHT, weight);
        setupProfileIntent.putExtra(EXTRA_PROGRESS_DATE, Util.getCurrentDateInMillis());
        setupProfileIntent.putExtra(EXTRA_WEIGHT_UNIT, weightUnit);

        setupProfileIntent.putExtra(EXTRA_GOAL_TARGET_WEIGHT, targetWeight);
        setupProfileIntent.putExtra(EXTRA_GOAL_DUE_DATE, dueDateInMillis);

        Intent callbackIntent = new Intent(context, callbackActivity);
        callbackIntent.setAction(callbackAction);
        setupProfileIntent.putExtra(EXTRA_CALLBACK_INTENT, callbackIntent);

        return setupProfileIntent;
    }

    public static Intent createInsertProfileIntent(Context context, String name, long birthdayInMillis, String gender, double height, double inches,
            String heightUnit, Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent insertProfileIntent = new Intent(context, WeightTrackerSaveService.class);
        insertProfileIntent.setAction(ACTION_INSERT_PROFILE);
        insertProfileIntent.putExtra(EXTRA_PROFILE_NAME, name);
        insertProfileIntent.putExtra(EXTRA_PROFILE_BIRTHDAY, birthdayInMillis);
        insertProfileIntent.putExtra(EXTRA_PROFILE_GENDER, gender);
        insertProfileIntent.putExtra(EXTRA_PROFILE_HEIGHT, height);
        insertProfileIntent.putExtra(EXTRA_PROFILE_HEIGHT_INCHES, inches);
        insertProfileIntent.putExtra(EXTRA_HEIGHT_UNIT, heightUnit);

        Intent callbackIntent = new Intent(context, callbackActivity);
        callbackIntent.setAction(callbackAction);
        insertProfileIntent.putExtra(EXTRA_CALLBACK_INTENT, callbackIntent);

        return insertProfileIntent;
    }

    public static Intent createInsertProgressIntent(Context context, double newWeight,
            long dateInMillis, String weightUnit, Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent insertProgressIntent = new Intent(context, WeightTrackerSaveService.class);
        insertProgressIntent.setAction(ACTION_INSERT_PROGRESS);
        insertProgressIntent.putExtra(EXTRA_PROGRESS_WEIGHT, newWeight);
        insertProgressIntent.putExtra(EXTRA_PROGRESS_DATE, dateInMillis);
        insertProgressIntent.putExtra(EXTRA_WEIGHT_UNIT, weightUnit);

        Intent callbackIntent = new Intent(context, callbackActivity);
        callbackIntent.setAction(callbackAction);
        insertProgressIntent.putExtra(EXTRA_CALLBACK_INTENT, callbackIntent);

        return insertProgressIntent;
    }

    public static Intent createUpdateProgressIntent(Context context, long progressId,
            double newWeight, long dateInMillis, String weightUnit, Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent updateProgressIntent = new Intent(context, WeightTrackerSaveService.class);
        updateProgressIntent.setAction(ACTION_UPDATE_PROGRESS);
        updateProgressIntent.putExtra(EXTRA_PROGRESS_ID, progressId);
        updateProgressIntent.putExtra(EXTRA_PROGRESS_WEIGHT, newWeight);
        updateProgressIntent.putExtra(EXTRA_PROGRESS_DATE, dateInMillis);
        updateProgressIntent.putExtra(EXTRA_WEIGHT_UNIT, weightUnit);

        Intent callbackIntent = new Intent(context, callbackActivity);
        callbackIntent.setAction(callbackAction);
        updateProgressIntent.putExtra(EXTRA_CALLBACK_INTENT, callbackIntent);

        return updateProgressIntent;
    }

    public static Intent createDeleteProgressIntent(
            Context context, long progressId, Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent deleteProgressIntent = new Intent(context, WeightTrackerSaveService.class);
        deleteProgressIntent.setAction(ACTION_DELETE_PROGRESS);
        deleteProgressIntent.putExtra(EXTRA_PROGRESS_ID, progressId);

        Intent callbackIntent = new Intent(context, callbackActivity);
        callbackIntent.setAction(callbackAction);
        deleteProgressIntent.putExtra(EXTRA_CALLBACK_INTENT, callbackIntent);

        return deleteProgressIntent;
    }

    public static Intent createBulkDeleteProgressIntent(
            Context context, long[] progressId, Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent bulkDeleteProgressIntent = new Intent(context, WeightTrackerSaveService.class);
        bulkDeleteProgressIntent.setAction(ACTION_BULK_DELETE_PROGRESS);
        bulkDeleteProgressIntent.putExtra(EXTRA_PROGRESS_IDS, progressId);

        Intent callbackIntent = new Intent(context, callbackActivity);
        callbackIntent.setAction(callbackAction);
        bulkDeleteProgressIntent.putExtra(EXTRA_CALLBACK_INTENT, callbackIntent);

        return bulkDeleteProgressIntent;
    }

    public static Intent createInsertGoalIntent(Context context, double targetWeight, long dueDateInMillis, String weightUnit, Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent insertGoalIntent = new Intent(context, WeightTrackerSaveService.class);
        insertGoalIntent.setAction(ACTION_INSERT_GOAL);
        insertGoalIntent.putExtra(EXTRA_GOAL_TARGET_WEIGHT, targetWeight);
        insertGoalIntent.putExtra(EXTRA_GOAL_DUE_DATE, dueDateInMillis);
        insertGoalIntent.putExtra(EXTRA_WEIGHT_UNIT, weightUnit);

        Intent callbackIntent = new Intent(context, callbackActivity);
        callbackIntent.setAction(callbackAction);
        insertGoalIntent.putExtra(EXTRA_CALLBACK_INTENT, callbackIntent);

        return insertGoalIntent;
    }

    public interface Listener {
        public void onServiceCompleted(Intent callbackIntent);
    }

    public static void registerListener(Listener listener) {
        if (listener instanceof Activity) {
            sListeners.add(0, listener);
        } else {
            throw new ClassCastException();
        }
    }

    public static void unregisterListener(Listener listener) {
        sListeners.remove(listener);
    }

    private void deliverCallback(final Intent callbackIntent) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                deliverCallbackOnUiThread(callbackIntent);
            }
        });
    }

    private void deliverCallbackOnUiThread(Intent callbackIntent) {
        for (Listener listener : sListeners) {
            if (callbackIntent.getComponent().equals(((Activity) listener).getIntent().getComponent())) {
                listener.onServiceCompleted(callbackIntent);
                return;
            }
        }
    }

}
