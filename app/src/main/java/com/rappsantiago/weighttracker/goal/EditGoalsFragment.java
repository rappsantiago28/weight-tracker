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

package com.rappsantiago.weighttracker.goal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;

import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.dialog.DatePickerDialogFragment;
import com.rappsantiago.weighttracker.model.Goal;
import com.rappsantiago.weighttracker.provider.WeightTrackerContract;
import com.rappsantiago.weighttracker.provider.queryhelpers.GoalsQueryHelper;
import com.rappsantiago.weighttracker.util.DisplayUtil;
import com.rappsantiago.weighttracker.util.PreferenceUtil;
import com.rappsantiago.weighttracker.util.Util;

import org.joda.time.LocalDate;

/**
 * Created by ARKAS on 23/07/2016.
 */
public class EditGoalsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private TextInputLayout mTxtTargetWeightWrapper;

    private TextInputLayout mTxtTargetBodyFatIndexWrapper;

    private CheckBox mChkDueDate;

    private TextView mLblDueDate;

    private long mDueDateInMillis;

    private long mGoalId;

    public static EditGoalsFragment createFragment() {
        return new EditGoalsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_goals, container, false);

        mTxtTargetWeightWrapper = (TextInputLayout) view.findViewById(R.id.txt_target_weight_wrapper);
        mTxtTargetBodyFatIndexWrapper = (TextInputLayout) view.findViewById(R.id.txt_target_bfi_wrapper);
        mChkDueDate = (CheckBox) view.findViewById(R.id.chk_due_date);
        mLblDueDate = (TextView) view.findViewById(R.id.lbl_due_date);

        setCurrentValues();

        mChkDueDate.setOnCheckedChangeListener(mCheckedChangedListener);
        mLblDueDate.setOnClickListener(mSetDateClickListener);

        return view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
        mDueDateInMillis = date.toDate().getTime();
        mLblDueDate.setText(DisplayUtil.getReadableDate(mDueDateInMillis));
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangedListener =
            new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mLblDueDate.setEnabled(isChecked);
                }
            };

    private View.OnClickListener mSetDateClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            DatePickerDialogFragment datePickerDialog = new DatePickerDialogFragment();
            datePickerDialog.setOnDateSetListener(EditGoalsFragment.this);
            datePickerDialog.show(getFragmentManager(), "");
        }
    };

    private void setCurrentValues() {
        String weightUnit = PreferenceUtil.getWeightUnit(getActivity());

        int resString = -1;

        switch (weightUnit) {
            case WeightTrackerContract.Profile.WEIGHT_UNIT_KILOGRAMS:
                resString = R.string.kilograms;
                break;

            case WeightTrackerContract.Profile.WEIGHT_UNIT_POUNDS:
                resString = R.string.pounds;
                break;

            default:
                throw new IllegalArgumentException("Invalid weight unit");
        }

        String hint = String.format(getString(R.string.target_weight_with_unit), getString(resString));
        mTxtTargetWeightWrapper.setHint(hint);

        GoalsQueryHelper goalsQueryHelper = new GoalsQueryHelper(getActivity());
        Goal currentGoal = goalsQueryHelper.getCurrentGoal();

        mGoalId = currentGoal.getGoalId();

        mTxtTargetWeightWrapper.getEditText().setText(
                DisplayUtil.getWeightString(getActivity(),
                        currentGoal.getTargetWeight(), null));

        mTxtTargetBodyFatIndexWrapper.getEditText().setText(
                Double.toString(currentGoal.getTargetBodyFatIndex()));

        if (0 >= currentGoal.getDueDate()) {
            mDueDateInMillis = Util.getCurrentDateInMillis();
            mLblDueDate.setText(DisplayUtil.getReadableDate(mDueDateInMillis));
            mChkDueDate.setChecked(false);
        } else {
            mDueDateInMillis = currentGoal.getDueDate();
            mLblDueDate.setText(DisplayUtil.getReadableDate(currentGoal.getDueDate()));
            mChkDueDate.setChecked(true);
        }
    }

    public Goal getCurrentGoal() {
        String strTargetWeight = mTxtTargetWeightWrapper.getEditText().getText().toString();
        double targetWeight = Util.parseDouble(strTargetWeight, 0.0);

        String strTargetBodyFatIndex = mTxtTargetBodyFatIndexWrapper.getEditText().getText().toString();
        double targetBodyFatIndex = Util.parseDouble(strTargetBodyFatIndex, 0.0);

        return new Goal.Builder(mGoalId, targetWeight)
                .targetBodyFatIndex(targetBodyFatIndex)
                .dueDate(mDueDateInMillis)
                .build();
    }
}
