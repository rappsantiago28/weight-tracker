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

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.dialog.DatePickerDialogFragment;
import com.rappsantiago.weighttracker.provider.WeightTrackerContract;

import org.joda.time.LocalDate;

/**
 * Created by rappsantiago28 on 3/13/16.
 */
public class NameBirthdayGenderFragment extends Fragment
        implements FragmentWithProfileData, DatePickerDialog.OnDateSetListener {

    public static final String KEY_NAME = "NameBirthdayGenderFragment.KEY_NAME";

    public static final String KEY_BIRTHDAY = "NameBirthdayGenderFragment.KEY_BIRTHDAY";

    public static final String KEY_GENDER = "NameBirthdayGenderFragment.KEY_GENDER";

    private EditText mTxtName;

    private long mBirthday;

    private String mGender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_name_birthday_gender, container, false);

        mTxtName = (EditText) view.findViewById(R.id.txt_name);
        TextView dateText = (TextView) view.findViewById(R.id.txt_birthday);
        RadioButton rdoMale = (RadioButton) view.findViewById(R.id.rdo_male);
        RadioButton rdoFemale = (RadioButton) view.findViewById(R.id.rdo_female);

        dateText.setOnClickListener(mSetDateClickListener);
        rdoMale.setOnClickListener(mRadioButtonsClickListener);
        rdoFemale.setOnClickListener(mRadioButtonsClickListener);

        return view;
    }

    @Override
    public Bundle getProfileData() {
        Bundle bundle = new Bundle();

        bundle.putString(KEY_NAME, mTxtName.getText().toString());
        bundle.putLong(KEY_BIRTHDAY, mBirthday);
        bundle.putString(KEY_GENDER, mGender);

        return bundle;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate date = new LocalDate(year, monthOfYear, dayOfMonth);
        mBirthday = date.toDate().getTime();
    }

    private View.OnClickListener mSetDateClickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            DatePickerDialogFragment datePickerDialog = new DatePickerDialogFragment();
            datePickerDialog.setOnDateSetListener(NameBirthdayGenderFragment.this);
            datePickerDialog.show(getFragmentManager(), "");
        }
    };

    private View.OnClickListener mRadioButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final int id = v.getId();

            switch (id) {
                case R.id.rdo_male:
                    mGender = WeightTrackerContract.Profile.GENDER_MALE;
                    break;

                case R.id.rdo_female:
                    mGender = WeightTrackerContract.Profile.GENDER_FEMALE;
                    break;

                default:
                    throw new IllegalArgumentException();
            }
        }
    };
}
