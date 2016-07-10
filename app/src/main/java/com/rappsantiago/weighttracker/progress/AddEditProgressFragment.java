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

package com.rappsantiago.weighttracker.progress;

import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.rappsantiago.weighttracker.MainActivity;
import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.dialog.ConfirmationDialogFragment;
import com.rappsantiago.weighttracker.dialog.DatePickerDialogFragment;
import com.rappsantiago.weighttracker.provider.DbConstants;
import com.rappsantiago.weighttracker.service.WeightTrackerSaveService;
import com.rappsantiago.weighttracker.util.DisplayUtil;
import com.rappsantiago.weighttracker.util.PreferenceUtil;
import com.rappsantiago.weighttracker.util.Util;

import org.joda.time.LocalDate;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

/**
 * Created by rappsantiago28 on 3/26/16.
 */
public class AddEditProgressFragment extends Fragment implements DatePickerDialog.OnDateSetListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = AddEditProgressFragment.class.getSimpleName();

    private static String KEY_PROGRESS_ID = "com.rappsantiago.weighttracker.progress.AddEditProgressFragment.KEY_PROGRESS_ID";

    private TextInputLayout mTxtNewWeightWrapper;

    private TextInputLayout mTxtNewBodyFatIndexWrapper;

    private TextView mLblDate;

    private Button mBtnDone;

    private long mDateInMillis;

    private long mExistingProgressId = -1L;

    public static AddEditProgressFragment createFragment(long existingProgressId) {
        AddEditProgressFragment addEditProgressFragment = new AddEditProgressFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_PROGRESS_ID, existingProgressId);
        addEditProgressFragment.setArguments(args);

        return addEditProgressFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (null != args) {
            mExistingProgressId = args.getLong(KEY_PROGRESS_ID, -1L);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_progress, container, false);

        mTxtNewWeightWrapper = (TextInputLayout) view.findViewById(R.id.txt_new_weight_wrapper);
        mTxtNewBodyFatIndexWrapper = (TextInputLayout) view.findViewById(R.id.txt_new_bfi_wrapper);
        mLblDate = (TextView) view.findViewById(R.id.lbl_date);
        mBtnDone = (Button) view.findViewById(R.id.btn_done);

        mLblDate.setOnClickListener(mSetDateClickListener);
        mBtnDone.setOnClickListener(mDoneClickListener);

        String weightUnit = PreferenceUtil.getWeightUnit(getActivity());
        String hint;

        switch (weightUnit) {
            case Profile.WEIGHT_UNIT_KILOGRAMS:
                hint = String.format(getString(R.string.weight_with_unit), getString(R.string.kilograms));
                break;

            case Profile.WEIGHT_UNIT_POUNDS:
                hint = String.format(getString(R.string.weight_with_unit), getString(R.string.pounds));
                break;

            default:
                throw new IllegalArgumentException("Invalide weight unit");
        }

        mTxtNewWeightWrapper.setHint(hint);

        if (!isEditMode()) {
            if (0 >= mDateInMillis) {
                mDateInMillis = Util.getCurrentDateInMillis();
                mLblDate.setText(DisplayUtil.getReadableDate(mDateInMillis));
            } else {
                mLblDate.setText(DisplayUtil.getReadableDate(mDateInMillis));
            }
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (isEditMode()) {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    private View.OnClickListener mSetDateClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            DatePickerDialogFragment datePickerDialog = new DatePickerDialogFragment();
            datePickerDialog.setOnDateSetListener(AddEditProgressFragment.this);
            datePickerDialog.show(getFragmentManager(), "");
        }
    };

    private View.OnClickListener mDoneClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String strNewWeight = mTxtNewWeightWrapper.getEditText().getText().toString();
            double newWeight = Util.parseDouble(strNewWeight, 0.0);

            String strNewBodyFatIndex = mTxtNewBodyFatIndexWrapper.getEditText().getText().toString();
            double newBodyFatIndex = Util.parseDouble(strNewBodyFatIndex, 0.0);

            String weightUnit = PreferenceUtil.getWeightUnit(getContext());

            if (0 >= newWeight) {
                mTxtNewWeightWrapper.setError(getString(R.string.invalid_weight));
                return;
            }

            Log.d(TAG, "date = " + mDateInMillis);

            if (isEditMode()) {
                doSaveOnUpdate(mExistingProgressId, newWeight, weightUnit, newBodyFatIndex);
            } else {
                doSaveOnCreate(newWeight, weightUnit, newBodyFatIndex);
            }
        }
    };

    private void doSaveOnCreate(final double newWeight, final String weightUnit, final double newBodyFatIndex) {
        try (Cursor cursor = getActivity().getContentResolver().query(
                Progress.CONTENT_URI,
                DbConstants.COLUMNS_PROGRESS,
                Progress.COL_TIMESTAMP + " = ?",
                new String[]{Long.toString(mDateInMillis)},
                null)) {

            // check if progress for the day is already existing
            if (null != cursor && 0 < cursor.getCount() && cursor.moveToFirst()) {

                final long progressId = cursor.getLong(DbConstants.IDX_PROGRESS_ID);

                ConfirmationDialogFragment confirmationDialog =
                        ConfirmationDialogFragment.createDialog(
                                getString(R.string.warning),
                                getString(R.string.date_already_exists));

                confirmationDialog.setPositiveClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startUpdateProgressService(progressId, newWeight, weightUnit, newBodyFatIndex);
                    }
                });

                confirmationDialog.show(getFragmentManager(), "");
            } else {
                Intent insertProgressIntent = WeightTrackerSaveService.createInsertProgressIntent(
                        getActivity(),
                        newWeight,
                        mDateInMillis,
                        weightUnit,
                        newBodyFatIndex,
                        AddEditProgressActivity.class,
                        MainActivity.CALLBACK_ACTION_INSERT_PROGRESS);

                getActivity().startService(insertProgressIntent);
            }
        }
    }

    private void doSaveOnUpdate(final long progressId, final double newWeight, final String weightUnit, final double newBodyFatIndex) {
        try (Cursor cursor = getActivity().getContentResolver().query(
                Progress.CONTENT_URI,
                DbConstants.COLUMNS_PROGRESS,
                Progress.COL_TIMESTAMP + " = ? AND " + Progress._ID + " != ?", // same day but different entry
                new String[]{Long.toString(mDateInMillis), Long.toString(progressId)},
                null)) {

            // check if progress for the day is already existing
            if (null != cursor && 0 < cursor.getCount() && cursor.moveToFirst()) {

                final long progressIdToDelete = cursor.getLong(DbConstants.IDX_PROGRESS_ID);

                ConfirmationDialogFragment confirmationDialog =
                        ConfirmationDialogFragment.createDialog(
                                getString(R.string.warning),
                                getString(R.string.date_already_exists));

                confirmationDialog.setPositiveClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startReplaceProgressService(progressId, newWeight, weightUnit, newBodyFatIndex, progressIdToDelete);
                    }
                });

                confirmationDialog.show(getFragmentManager(), "");
            } else {
                startUpdateProgressService(progressId, newWeight, weightUnit, newBodyFatIndex);
            }
        }
    }

    private void startUpdateProgressService(long progressId, double newWeight, String weightUnit, double newBodyFatIndex) {
        Intent updateProgressIntent = WeightTrackerSaveService.createUpdateProgressIntent(getActivity(),
                progressId,
                newWeight,
                mDateInMillis,
                weightUnit,
                newBodyFatIndex,
                AddEditProgressActivity.class,
                MainActivity.CALLBACK_ACTION_UPDATE_PROGRESS);

        getActivity().startService(updateProgressIntent);
    }

    private void startReplaceProgressService(long progressId, double newWeight, String weightUnit, double newBodyFatIndex, long progressIdToDelete) {
        Intent replaceProgressIntent = WeightTrackerSaveService.createReplaceProgressIntent(getActivity(),
                progressId,
                newWeight,
                mDateInMillis,
                weightUnit,
                newBodyFatIndex,
                progressIdToDelete,
                AddEditProgressActivity.class,
                MainActivity.CALLBACK_ACTION_REPLACE_PROGRESS);

        getActivity().startService(replaceProgressIntent);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
        mDateInMillis = date.toDate().getTime();
        mLblDate.setText(DisplayUtil.getReadableDate(mDateInMillis));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (isEditMode()) {
            CursorLoader cursorLoader = new CursorLoader(
                    getActivity(),
                    ContentUris.withAppendedId(Progress.CONTENT_URI, mExistingProgressId),
                    DbConstants.COLUMNS_PROGRESS,
                    null, null, null);
            return cursorLoader;
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (null != data && 0 < data.getCount() && data.moveToFirst()) {
            double weight = data.getDouble(DbConstants.IDX_PROGRESS_WEIGHT);
            double bodyFatIndex = data.getDouble(DbConstants.IDX_PROGRESS_BODY_FAT_INDEX);
            long dateInMillis = data.getLong(DbConstants.IDX_PROGRESS_TIMESTAMP);

            mTxtNewWeightWrapper.getEditText().setText(DisplayUtil.getWeightString(getActivity(), weight, null));
            mTxtNewBodyFatIndexWrapper.getEditText().setText(String.format("%.2f", bodyFatIndex));

            mDateInMillis = dateInMillis;
            mLblDate.setText(DisplayUtil.getReadableDate(dateInMillis));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean isEditMode() {
        return (0 < mExistingProgressId);
    }
}
