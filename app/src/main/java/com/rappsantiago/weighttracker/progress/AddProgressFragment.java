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
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.dialog.DatePickerDialogFragment;
import com.rappsantiago.weighttracker.util.DisplayUtil;
import com.rappsantiago.weighttracker.util.PreferenceUtil;
import com.rappsantiago.weighttracker.util.Util;

import org.joda.time.LocalDate;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

import java.util.Date;

/**
 * Created by rappsantiago28 on 3/26/16.
 */
public class AddProgressFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private TextInputLayout mTxtNewWeightWrapper;

    private TextView mLblDate;

    private Button mBtnDone;

    private long mDateInMillis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_progress, container, false);

        mTxtNewWeightWrapper = (TextInputLayout) view.findViewById(R.id.txt_new_weight_wrapper);
        mLblDate = (TextView) view.findViewById(R.id.lbl_date);
        mBtnDone = (Button) view.findViewById(R.id.btn_done);

        if (0 >= mDateInMillis) {
            mDateInMillis = new Date().getTime();
            mLblDate.setText(DisplayUtil.getReadableDate(mDateInMillis));
        } else {
            mLblDate.setText(DisplayUtil.getReadableDate(mDateInMillis));
        }

        mLblDate.setOnClickListener(mSetDateClickListener);
        mBtnDone.setOnClickListener(mDoneClickListener);

        return view;
    }

    private View.OnClickListener mSetDateClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            DatePickerDialogFragment datePickerDialog = new DatePickerDialogFragment();
            datePickerDialog.setOnDateSetListener(AddProgressFragment.this);
            datePickerDialog.show(getFragmentManager(), "");
        }
    };

    private View.OnClickListener mDoneClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String strNewWeight = mTxtNewWeightWrapper.getEditText().getText().toString();
            double newWeight = Util.parseDouble(strNewWeight, -1);

            String weightUnit = PreferenceUtil.getWeightUnit(getContext());
            
            if (Profile.WEIGHT_UNIT_POUNDS.equals(weightUnit)) {
                newWeight = Util.poundsToKilograms(newWeight);
            }

            if (0 >= newWeight) {
                mTxtNewWeightWrapper.setError(getString(R.string.invalid_weight));
                return;
            }

            ContentValues values = new ContentValues();
            values.put(Progress.COL_NEW_WEIGHT, newWeight);
            values.put(Progress.COL_TIMESTAMP, mDateInMillis);

            Uri progressUri = getActivity().getContentResolver().insert(Progress.CONTENT_URI, values);

            if (null != progressUri) {
                getActivity().finish();
            }
        }
    };

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
        mDateInMillis = date.toDate().getTime();
        mLblDate.setText(DisplayUtil.getReadableDate(mDateInMillis));
    }
}
