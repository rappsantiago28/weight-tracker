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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rappsantiago.weighttracker.R;

/**
 * Created by rappsantiago28 on 3/13/16.
 */
public class ProfileSetupActivity extends AppCompatActivity {

    private static final String TAG = ProfileSetupActivity.class.getSimpleName();

    private ViewPager mViewPager;

    private FragmentStatePagerAdapter mPagerAdapter;

    private Button mBtnBack;

    private Button mBtnNext;

    private Bundle mProfileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new ProfileSetupPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnNext = (Button) findViewById(R.id.btn_next);

        mProfileData = new Bundle();
    }

    @Override
    public void onBackPressed() {
        performBackAction();
    }

    public void performBackOrNextAction(View view) {
        final int btnId = view.getId();

        switch (btnId) {
            case R.id.btn_back:
                performBackAction();
                break;

            case R.id.btn_next:
                performNextAction();
                break;

            default:
                throw new IllegalStateException();
        }
    }

    private void performBackAction() {
        int currentPage = mViewPager.getCurrentItem();

        mBtnBack.setEnabled(true);
        mBtnNext.setEnabled(true);

        switch (currentPage) {
            case ProfileSetupPagerAdapter.PAGE_WELCOME:
                // this is the first page so no previous action
                break;

            case ProfileSetupPagerAdapter.PAGE_NAME_BIRTHDAY_GENDER:
                mViewPager.setCurrentItem(ProfileSetupPagerAdapter.PAGE_WELCOME, true);

                // disable back button after going to the first page
                mBtnBack.setEnabled(false);
                break;

            case ProfileSetupPagerAdapter.PAGE_WEIGHT_HEIGHT:
                mViewPager.setCurrentItem(ProfileSetupPagerAdapter.PAGE_NAME_BIRTHDAY_GENDER, true);
                break;

            case ProfileSetupPagerAdapter.PAGE_SUMMARY:
                mViewPager.setCurrentItem(ProfileSetupPagerAdapter.PAGE_WEIGHT_HEIGHT, true);

                // set text back to 'Back'
                mBtnNext.setText(R.string.next);
                break;

            default:
                throw new IllegalArgumentException("Unknown page : " + currentPage);
        }
    }

    private void performNextAction() {
        int currentPage = mViewPager.getCurrentItem();

        // save page data before going to next page
        boolean isDataSaved = savePageData(currentPage);

        if (ProfileSetupPagerAdapter.PAGE_WELCOME != currentPage &&
                ProfileSetupPagerAdapter.PAGE_SUMMARY != currentPage &&
                !isDataSaved) {
            Toast.makeText(this, "Please complete the form", Toast.LENGTH_SHORT).show();
            return;
        }

        mBtnBack.setEnabled(true);
        mBtnNext.setEnabled(true);

        switch (currentPage) {
            case ProfileSetupPagerAdapter.PAGE_WELCOME:
                mViewPager.setCurrentItem(ProfileSetupPagerAdapter.PAGE_NAME_BIRTHDAY_GENDER, true);
                break;

            case ProfileSetupPagerAdapter.PAGE_NAME_BIRTHDAY_GENDER:
                mViewPager.setCurrentItem(ProfileSetupPagerAdapter.PAGE_WEIGHT_HEIGHT, true);
                break;

            case ProfileSetupPagerAdapter.PAGE_WEIGHT_HEIGHT:
                // set summary
                Fragment currentFragment = mPagerAdapter.getItem(ProfileSetupPagerAdapter.PAGE_SUMMARY);
                ProfileSetupSummaryFragment summaryFragment = (ProfileSetupSummaryFragment) currentFragment;
                summaryFragment.refreshProfileData(mProfileData);

                mViewPager.setCurrentItem(ProfileSetupPagerAdapter.PAGE_SUMMARY, true);
                // set text to 'Finish' after going to the last page
                mBtnNext.setText(R.string.finish);
                break;

            case ProfileSetupPagerAdapter.PAGE_SUMMARY:
                // this is the last page so do finish action
                Intent resultIntent = new Intent();
                resultIntent.putExtras(mProfileData);

                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;

            default:
                throw new IllegalArgumentException("Unknown page : " + currentPage);
        }
    }

    private boolean savePageData(int currentPage) {

        Fragment currentFragment = mPagerAdapter.getItem(currentPage);

        if (currentFragment instanceof FragmentWithProfileData) {
            FragmentWithProfileData currentFragmentData = (FragmentWithProfileData) currentFragment;
            Bundle pageData = currentFragmentData.getProfileData();

            if (null == pageData) {
                return false;
            }

            // validate entries
            for (String key : pageData.keySet()) {
                Object obj = pageData.get(key);

                if (null == obj) {
                    return false;
                }

                // inches is allowed to be 0
                if (WeightHeightFragment.KEY_HEIGHT_INCHES == key) {
                    continue;
                }

                if (obj instanceof String) { // name
                    if (((String) obj).trim().isEmpty()) {
                        return false;
                    }
                } else if (obj instanceof Double) { // weight, height
                    if (0 >= ((Double) obj).doubleValue()) {
                        return false;
                    }
                } else if (obj instanceof Long) { // birthday
                    if (0 >= ((Long) obj).longValue()) {
                        return false;
                    }
                }

            }

            Log.d(TAG, "pageData = " + pageData);
            mProfileData.putAll(pageData);

            return true;
        } else {
            return false;
        }
    }
}
