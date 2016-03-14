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

    public static final int PAGE_SUMMARY = 3;

    private static final Map<Integer, Fragment> PAGE_MAPPING;

    static {
        PAGE_MAPPING = new HashMap<>();
        PAGE_MAPPING.put(PAGE_WELCOME, new WelcomePageFragment());
        PAGE_MAPPING.put(PAGE_NAME_BIRTHDAY_GENDER, new NameBirthdayGenderFragment());
        PAGE_MAPPING.put(PAGE_WEIGHT_HEIGHT, new WeightHeightFragment());
        PAGE_MAPPING.put(PAGE_SUMMARY, new ProfileSetupSummaryFragment());
    }

    public ProfileSetupPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (PAGE_MAPPING.containsKey(position)) {
            return PAGE_MAPPING.get(position);
        } else {
            throw new IllegalArgumentException("Unknown position: " + position);
        }
    }

    @Override
    public int getCount() {
        return PAGE_MAPPING.size();
    }
}
