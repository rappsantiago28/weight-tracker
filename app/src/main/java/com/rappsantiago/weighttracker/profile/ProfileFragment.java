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

package com.rappsantiago.weighttracker.profile;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.provider.DbConstants;
import com.rappsantiago.weighttracker.util.DisplayUtil;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

/**
 * Created by rappsantiago28 on 3/26/16.
 */
public class ProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private static final int LOAD_PROFILE = 0;

    private static final int LOAD_GOAL = 1;

    private static final int LOAD_WEIGHT_PROGRESS = 2;

    private TextView mLblName;

    private TextView mLblBirthday;

    private TextView mLblGender;

    private TextView mLblInitialWeight;

    private TextView mLblCurrentWeight;

    private TextView mLblHeight;

    private TextView mLblTargetWeight;

    private TextView mLblDueDate;

    private TextView mLblStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mLblName = (TextView) view.findViewById(R.id.lbl_name);
        mLblBirthday = (TextView) view.findViewById(R.id.lbl_birthday);
        mLblGender = (TextView) view.findViewById(R.id.lbl_gender);
        mLblInitialWeight = (TextView) view.findViewById(R.id.lbl_initial_weight);
        mLblCurrentWeight = (TextView) view.findViewById(R.id.lbl_current_weight);
        mLblHeight = (TextView) view.findViewById(R.id.lbl_height);
        mLblTargetWeight = (TextView) view.findViewById(R.id.lbl_target_weight);
        mLblDueDate = (TextView) view.findViewById(R.id.lbl_due_date);
        mLblStatus = (TextView) view.findViewById(R.id.lbl_status);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOAD_PROFILE, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshPage();
    }

    public void refreshPage() {
        getLoaderManager().restartLoader(LOAD_PROFILE, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case LOAD_PROFILE:
                return new CursorLoader(getActivity(),
                        Profile.CONTENT_URI, DbConstants.COLUMNS_PROFILE, null, null, null);

            case LOAD_GOAL:
                return new CursorLoader(getActivity(),
                        Goal.CONTENT_URI, DbConstants.COLUMNS_GOAL, null, null, null);

            case LOAD_WEIGHT_PROGRESS:
                return new WeightProgressCursorLoader(getActivity());

            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int id = loader.getId();

        switch (id) {
            case LOAD_PROFILE:
                if (null != data && (0 < data.getCount()) && data.moveToFirst()) {
                    String name = data.getString(DbConstants.IDX_PROFILE_NAME);
                    long birthdayInMillis = data.getLong(DbConstants.IDX_PROFILE_BIRTHDAY);
                    String gender = data.getString(DbConstants.IDX_PROFILE_GENDER);
                    double height = data.getDouble(DbConstants.IDX_PROFILE_HEIGHT);

                    mLblName.setText(name);
                    mLblBirthday.setText(DisplayUtil.getReadableDate(birthdayInMillis));
                    mLblGender.setText(DisplayUtil.getReadableGender(getContext(), gender));
                    mLblHeight.setText(DisplayUtil.getFormattedHeight(getContext(), height, null));

                    getLoaderManager().restartLoader(LOAD_GOAL, null, this);
                }
                break;

            case LOAD_GOAL:
                if (null != data && (0 < data.getCount()) && data.moveToFirst()) {
                    double targetWeight = data.getDouble(DbConstants.IDX_GOAL_TARGET_WEIGHT);
                    long dueDateInMillis = data.getLong(DbConstants.IDX_GOAL_DUE_DATE);

                    mLblTargetWeight.setText(DisplayUtil.getFormattedWeight(getContext(), targetWeight, null));

                    if (0 < dueDateInMillis) {
                        mLblDueDate.setText(DisplayUtil.getReadableDate(dueDateInMillis));
                    } else {
                        mLblDueDate.setText(R.string.not_applicable);
                    }

                    getLoaderManager().restartLoader(LOAD_WEIGHT_PROGRESS, null, this);
                }
                break;

            case LOAD_WEIGHT_PROGRESS:
                if (null != data && (0 < data.getCount()) && data.moveToFirst()) {
                    double intialWeight = data.getDouble(WeightProgressCursorLoader.IDX_WEIGHT_PROGRESS_INITIAL_WEIGHT);
                    double currentWeight = data.getDouble(WeightProgressCursorLoader.IDX_WEIGHT_PROGRESS_CURRENT_WEIGHT);
                    double percentComplete = data.getDouble(WeightProgressCursorLoader.IDX_WEIGHT_PROGRESS_PERCENT_COMPLETE);

                    mLblInitialWeight.setText(DisplayUtil.getFormattedWeight(getActivity(), intialWeight, null));
                    mLblCurrentWeight.setText(DisplayUtil.getFormattedWeight(getActivity(), currentWeight, null));
                    mLblStatus.setText(getString(R.string.percent_complete_format, percentComplete * 100.0));
                }
                break;

            default:
                throw new IllegalArgumentException();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLblName.setText("");
        mLblBirthday.setText("");
        mLblGender.setText("");
        mLblCurrentWeight.setText("");
        mLblHeight.setText("");
    }
}
