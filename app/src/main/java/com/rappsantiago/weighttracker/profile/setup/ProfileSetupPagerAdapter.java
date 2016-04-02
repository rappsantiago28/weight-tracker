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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rappsantiago28 on 3/13/16.
 */
public class ProfileSetupPagerAdapter extends FragmentStatePagerAdapter {

    public static final int PAGE_WELCOME = 0;

    public static final int PAGE_NAME_BIRTHDAY_GENDER = 1;

    public static final int PAGE_WEIGHT_HEIGHT = 2;

    public static final int PAGE_TARGET_WEIGHT = 3;

    public static final int PAGE_SUMMARY = 4;

    private final Map<Integer, Fragment> mPageMapping;

    private Fragment mWelcomePageFragment;

    private Fragment mNameBirthdayGenderFragment;

    private Fragment mWeightHeightFragment;

    private Fragment mTargetWeightFragment;

    private Fragment mProfileSetupSummaryFragment;

    public ProfileSetupPagerAdapter(FragmentManager fm) {
        super(fm);

        mPageMapping = new HashMap<>();

        mWelcomePageFragment = new WelcomePageFragment();
        mNameBirthdayGenderFragment = new NameBirthdayGenderFragment();
        mWeightHeightFragment = new WeightHeightFragment();
        mTargetWeightFragment = new TargetWeightFragment();
        mProfileSetupSummaryFragment = new ProfileSetupSummaryFragment();

        mPageMapping.put(PAGE_WELCOME, mWelcomePageFragment);
        mPageMapping.put(PAGE_NAME_BIRTHDAY_GENDER, mNameBirthdayGenderFragment);
        mPageMapping.put(PAGE_WEIGHT_HEIGHT, mWeightHeightFragment);
        mPageMapping.put(PAGE_TARGET_WEIGHT, mTargetWeightFragment);
        mPageMapping.put(PAGE_SUMMARY, mProfileSetupSummaryFragment);
    }

    @Override
    public Fragment getItem(int position) {
        if (mPageMapping.containsKey(position)) {
            return mPageMapping.get(position);
        } else {
            throw new IllegalArgumentException("Unknown position: " + position);
        }
    }

    @Override
    public int getCount() {
        return mPageMapping.size();
    }
}
