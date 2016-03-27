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

package com.rappsantiago.weighttracker.profile.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.util.DisplayUtil;

/**
 * Created by rappsantiago28 on 3/13/16.
 */
public class ProfileSetupSummaryFragment extends Fragment {

    private TextView mName;

    private TextView mBirthday;

    private TextView mGender;

    private TextView mWeight;

    private TextView mHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_setup_summary, container, false);

        mName = (TextView) view.findViewById(R.id.lbl_name);
        mBirthday = (TextView) view.findViewById(R.id.lbl_birthday);
        mGender = (TextView) view.findViewById(R.id.lbl_gender);
        mWeight = (TextView) view.findViewById(R.id.lbl_weight);
        mHeight = (TextView) view.findViewById(R.id.lbl_height);

        return view;
    }

    public void refreshProfileData(Bundle profileData) {
        if (profileData.containsKey(NameBirthdayGenderFragment.KEY_NAME)) {
            mName.setText(profileData.getString(NameBirthdayGenderFragment.KEY_NAME));
        }

        if (profileData.containsKey(NameBirthdayGenderFragment.KEY_BIRTHDAY)) {
            long birthdayInMillis = profileData.getLong(NameBirthdayGenderFragment.KEY_BIRTHDAY);
            mBirthday.setText(DisplayUtil.getReadableDate(birthdayInMillis));
        }

        if (profileData.containsKey(NameBirthdayGenderFragment.KEY_GENDER)) {
            String gender = profileData.getString(NameBirthdayGenderFragment.KEY_GENDER);
            mGender.setText(DisplayUtil.getReadableGender(getContext(), gender));
        }

        if (profileData.containsKey(WeightHeightFragment.KEY_WEIGHT) &&
                profileData.containsKey(WeightHeightFragment.KEY_WEIGHT_UNIT)) {

            String weightUnit = profileData.getString(WeightHeightFragment.KEY_WEIGHT_UNIT);
            double weight = profileData.getDouble(WeightHeightFragment.KEY_WEIGHT);
            mWeight.setText(DisplayUtil.getFormattedWeight(getContext(), weight, weightUnit));
        }

        if (profileData.containsKey(WeightHeightFragment.KEY_HEIGHT) &&
                profileData.containsKey(WeightHeightFragment.KEY_HEIGHT_UNIT)) {

            double height = profileData.getDouble(WeightHeightFragment.KEY_HEIGHT);
            String heightUnit = profileData.getString(WeightHeightFragment.KEY_HEIGHT_UNIT);
            double heightInches = 0.0;

            if (profileData.containsKey(WeightHeightFragment.KEY_HEIGHT_INCHES)) {
                heightInches = profileData.getDouble(WeightHeightFragment.KEY_HEIGHT_INCHES);
            }

            mHeight.setText(DisplayUtil.getFormattedHeight(getContext(), height, heightInches, heightUnit));
        }
    }
}
