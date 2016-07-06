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

    private TextView mBodyFatIndex;

    private TextView mHeight;

    private TextView mTargetWeight;

    private TextView mTargetBodyFatIndex;

    private TextView mDueDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_setup_summary, container, false);

        mName = (TextView) view.findViewById(R.id.lbl_name);
        mBirthday = (TextView) view.findViewById(R.id.lbl_birthday);
        mGender = (TextView) view.findViewById(R.id.lbl_gender);
        mWeight = (TextView) view.findViewById(R.id.lbl_weight);
        mBodyFatIndex = (TextView) view.findViewById(R.id.lbl_body_fat_index);
        mHeight = (TextView) view.findViewById(R.id.lbl_height);
        mTargetWeight = (TextView) view.findViewById(R.id.lbl_target_weight);
        mTargetBodyFatIndex = (TextView) view.findViewById(R.id.lbl_target_body_fat_index);
        mDueDate = (TextView) view.findViewById(R.id.lbl_due_date);

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

        if (profileData.containsKey(WeightHeightFragment.KEY_BODY_FAT_INDEX)) {
            double bodyFatIndex = profileData.getDouble(WeightHeightFragment.KEY_BODY_FAT_INDEX);
            mBodyFatIndex.setText(bodyFatIndex + "%");
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

        if (profileData.containsKey(TargetWeightFragment.KEY_TARGET_WEIGHT)) {
            String weightUnit = profileData.getString(WeightHeightFragment.KEY_WEIGHT_UNIT);
            double targetWeight = profileData.getDouble(TargetWeightFragment.KEY_TARGET_WEIGHT);
            mTargetWeight.setText(DisplayUtil.getFormattedWeight(getContext(), targetWeight, weightUnit));
        }

        if (profileData.containsKey(TargetWeightFragment.KEY_TARGET_BODY_FAT_INDEX)) {
            double targetBodyFatIndex = profileData.getDouble(TargetWeightFragment.KEY_TARGET_BODY_FAT_INDEX);
            mTargetBodyFatIndex.setText(targetBodyFatIndex  + "%");
        }

        if (profileData.containsKey(TargetWeightFragment.KEY_DUE_DATE)) {
            long dueDateInMillis = profileData.getLong(TargetWeightFragment.KEY_DUE_DATE);

            if (0 < dueDateInMillis) {
                mDueDate.setText(DisplayUtil.getReadableDate(dueDateInMillis));
            } else {
                mDueDate.setText(R.string.not_applicable);
            }
        }
    }
}
