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
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by ARKAS on 23/05/2016.
 * Based from ContactSaveService.java
 */
public class WeightTrackerSaveService extends IntentService {

    private static final String TAG = WeightTrackerSaveService.class.getSimpleName();

    private static final String PREFIX = "com.rappsantiago.weighttracker.service.WeightTrackerSaveService";

    // actions
    private static final String ACTION_INSERT_PROGRESS = PREFIX + ".ACTION_INSERT_PROGRESS";

    private static final String ACTION_UPDATE_PROGRESS = PREFIX + ".ACTION_UPDATE_PROGRESS";

    private static final String ACTION_DELETE_PROGRESS = PREFIX + ".ACTION_DELETE_PROGRESS";

    // extras
    private static final String EXTRA_CALLBACK_INTENT = PREFIX + ".EXTRA_CALLBACK_INTENT";

    private static final String EXTRA_PROGRESS_ID = PREFIX + ".EXTRA_PROGRESS_ID";

    private static final String EXTRA_PROGRESS_WEIGHT = PREFIX + ".EXTRA_PROGRESS_WEIGHT";

    private static final String EXTRA_PROGRESS_DATE = PREFIX + ".EXTRA_PROGRESS_DATE";

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
            case ACTION_INSERT_PROGRESS:
                insertProgress(intent);
                break;

            case ACTION_UPDATE_PROGRESS:
                updateProgress(intent);
                break;

            case ACTION_DELETE_PROGRESS:
                deleteProgress(intent);
                break;

            default:
                throw new IllegalArgumentException("Unknown action (" + action + ")");
        }
    }

    /**
     * Action Implementations
     */
    private void insertProgress(Intent intent) {
        double newWeight = intent.getDoubleExtra(EXTRA_PROGRESS_WEIGHT, 0.0);
        long date = intent.getLongExtra(EXTRA_PROGRESS_DATE, 0L);

        ContentValues values = new ContentValues();
        values.put(Progress.COL_NEW_WEIGHT, newWeight);
        values.put(Progress.COL_TIMESTAMP, date);

        Uri result = getContentResolver().insert(Progress.CONTENT_URI, values);

        if (null != result) {
            Intent callbackIntent = intent.getParcelableExtra(EXTRA_CALLBACK_INTENT);
            deliverCallback(callbackIntent);
        }
    }

    private void updateProgress(Intent intent) {
        long progressId = intent.getLongExtra(EXTRA_PROGRESS_ID, 0L);
        double newWeight = intent.getDoubleExtra(EXTRA_PROGRESS_WEIGHT, 0.0);
        long date = intent.getLongExtra(EXTRA_PROGRESS_DATE, 0L);

        Uri updateProgressUri = ContentUris.withAppendedId(
                Progress.CONTENT_URI, progressId);

        ContentValues values = new ContentValues();
        values.put(Progress.COL_NEW_WEIGHT, newWeight);
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


    /**
     * Intent Builders
     */
    public static Intent createInsertProgressIntent(Context context, double newWeight, long dateInMillis, Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent insertProgressIntent = new Intent(context, WeightTrackerSaveService.class);
        insertProgressIntent.setAction(ACTION_INSERT_PROGRESS);
        insertProgressIntent.putExtra(EXTRA_PROGRESS_WEIGHT, newWeight);
        insertProgressIntent.putExtra(EXTRA_PROGRESS_DATE, dateInMillis);

        Intent callbackIntent = new Intent(context, callbackActivity);
        callbackIntent.setAction(callbackAction);
        insertProgressIntent.putExtra(EXTRA_CALLBACK_INTENT, callbackIntent);

        return insertProgressIntent;
    }

    public static Intent createUpdateProgressIntent(Context context, long progressId, double newWeight, long dateInMillis, Class<? extends Activity> callbackActivity, String callbackAction) {
        Intent updateProgressIntent = new Intent(context, WeightTrackerSaveService.class);
        updateProgressIntent.setAction(ACTION_UPDATE_PROGRESS);
        updateProgressIntent.putExtra(EXTRA_PROGRESS_ID, progressId);
        updateProgressIntent.putExtra(EXTRA_PROGRESS_WEIGHT, newWeight);
        updateProgressIntent.putExtra(EXTRA_PROGRESS_DATE, dateInMillis);

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
