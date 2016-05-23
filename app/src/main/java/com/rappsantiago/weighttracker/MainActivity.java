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

package com.rappsantiago.weighttracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rappsantiago.weighttracker.profile.ProfileFragment;
import com.rappsantiago.weighttracker.profile.setup.NameBirthdayGenderFragment;
import com.rappsantiago.weighttracker.profile.setup.ProfileSetupActivity;
import com.rappsantiago.weighttracker.profile.setup.TargetWeightFragment;
import com.rappsantiago.weighttracker.profile.setup.WeightHeightFragment;
import com.rappsantiago.weighttracker.progress.AddProgressActivity;
import com.rappsantiago.weighttracker.progress.HistoryFragment;
import com.rappsantiago.weighttracker.progress.StatisticsFragment;
import com.rappsantiago.weighttracker.service.WeightTrackerSaveService;
import com.rappsantiago.weighttracker.settings.SettingsActivity;
import com.rappsantiago.weighttracker.util.PreferenceUtil;
import com.rappsantiago.weighttracker.util.Util;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, WeightTrackerSaveService.Listener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PROFILE_SETUP = 0;

    private static final String KEY_CURRENT_PAGE = "MainActivity.KEY_CURRENT_PAGE";

    public static final String CALLBACK_ACTION_INSERT_PROGRESS = "MainActivity.CALLBACK_ACTION_INSERT_PROGRESS";

    public static final String CALLBACK_ACTION_UPDATE_PROGRESS = "MainActivity.CALLBACK_ACTION_UPDATE_PROGRESS";

    public static final String CALLBACK_ACTION_DELETE_PROGRESS = "MainActivity.CALLBACK_ACTION_DELETE_PROGRESS";

    private int mCurrentPage;

    private ProfileFragment mProfileFragment;

    private HistoryFragment mHistoryFragment;

    private StatisticsFragment mStatisticsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupFab();

        setupNavigationDrawer(toolbar);

        boolean isProfileSetupComplete = false;

        try (Cursor cursor = getContentResolver().query(Profile.CONTENT_URI, null, null, null, null)) {
            isProfileSetupComplete = cursor.getCount() > 0;
        }

        mProfileFragment = new ProfileFragment();
        mHistoryFragment = new HistoryFragment();
        mStatisticsFragment = new StatisticsFragment();

        if (!isProfileSetupComplete) {
            // launch profile setup page
            Intent setupProfileActivity = new Intent(this, ProfileSetupActivity.class);
            startActivityForResult(setupProfileActivity, REQUEST_PROFILE_SETUP);
        } else {

            mCurrentPage = R.id.nav_profile;

            if (null != savedInstanceState) {
                mCurrentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);
            }

            doNavigationAction(mCurrentPage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        WeightTrackerSaveService.registerListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WeightTrackerSaveService.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE, mCurrentPage);
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addProgressIntent = new Intent(MainActivity.this, AddProgressActivity.class);
                startActivity(addProgressIntent);
            }
        });
    }

    private void setupNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PROFILE_SETUP:
                if (Activity.RESULT_OK == resultCode) {

                    Bundle profileData = data.getExtras();

                    // save profile
                    String name = profileData.getString(NameBirthdayGenderFragment.KEY_NAME);
                    long birthdayInMillis = profileData.getLong(NameBirthdayGenderFragment.KEY_BIRTHDAY);
                    String gender = profileData.getString(NameBirthdayGenderFragment.KEY_GENDER);
                    double weight = profileData.getDouble(WeightHeightFragment.KEY_WEIGHT);
                    String weightUnit = profileData.getString(WeightHeightFragment.KEY_WEIGHT_UNIT);
                    double height = profileData.getDouble(WeightHeightFragment.KEY_HEIGHT);
                    String heightUnit = profileData.getString(WeightHeightFragment.KEY_HEIGHT_UNIT);

                    ContentValues profileValues = new ContentValues();

                    profileValues.put(Profile.COL_NAME, name);
                    profileValues.put(Profile.COL_BIRTHDAY, birthdayInMillis);
                    profileValues.put(Profile.COL_GENDER, gender);

                    if (Profile.HEIGHT_UNIT_CENTIMETERS.equals(heightUnit)) {
                        profileValues.put(Profile.COL_HEIGHT, height);
                    } else {
                        double inches = profileData.getDouble(WeightHeightFragment.KEY_HEIGHT_INCHES);
                        profileValues.put(Profile.COL_HEIGHT, Util.footInchesToCentimeters(height, inches));
                    }

                    Uri profileUri = getContentResolver().insert(Profile.CONTENT_URI, profileValues);

                    if (null != profileUri) {
                        // set default weight and height units
                        Log.d(TAG, "weightUnit = " + weightUnit + ", heightUnit = " + heightUnit);
                        PreferenceUtil.setWeightUnit(this, weightUnit);
                        PreferenceUtil.setHeightUnit(this, heightUnit);

                        // make current weight as initial progress
                        ContentValues progressValues = new ContentValues();

                        if (Profile.WEIGHT_UNIT_KILOGRAMS.equals(weightUnit)) {
                            progressValues.put(Progress.COL_NEW_WEIGHT, weight);
                        } else {
                            progressValues.put(Progress.COL_NEW_WEIGHT, Util.poundsToKilograms(weight));
                        }

                        progressValues.put(Progress.COL_TIMESTAMP, Util.getCurrentDateInMillis());

                        getContentResolver().insert(Progress.CONTENT_URI, progressValues);
                    }

                    // save goal
                    double targetWeight = profileData.getDouble(TargetWeightFragment.KEY_TARGET_WEIGHT);
                    long dueDateInMillis = profileData.getLong(TargetWeightFragment.KEY_DUE_DATE);

                    if (0.0 < targetWeight) {
                        ContentValues goalValues = new ContentValues();

                        if (Profile.WEIGHT_UNIT_KILOGRAMS.equals(weightUnit)) {
                            goalValues.put(Goal.COL_TARGET_WEIGHT, targetWeight);
                        } else {
                            goalValues.put(Goal.COL_TARGET_WEIGHT, Util.poundsToKilograms(targetWeight));
                        }

                        goalValues.put(Goal.COL_DUE_DATE, dueDateInMillis);

                        getContentResolver().insert(Goal.CONTENT_URI, goalValues);
                    } else {
                        // user skipped and will set later
                    }

                    replaceMainContent(mProfileFragment, R.string.profile);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown request code : " + requestCode);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        doNavigationAction(itemId);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void doNavigationAction(int itemId) {

        switch (itemId) {
            case R.id.nav_profile:
                replaceMainContent(mProfileFragment, R.string.profile);
                mCurrentPage = R.id.nav_profile;
                break;

            case R.id.nav_progress_history:
                replaceMainContent(mHistoryFragment, R.string.progress_history);
                mCurrentPage = R.id.nav_progress_history;
                break;

            case R.id.nav_statistics:
                replaceMainContent(mStatisticsFragment, R.string.statistics);
                mCurrentPage = R.id.nav_statistics;
                break;

            case R.id.nav_share:
                break;

            case R.id.nav_about:
                break;

            default:
                throw new IllegalArgumentException();
        }
    }

    private void replaceMainContent(Fragment fragment, int resTitle) {
        if (null == fragment) {
            throw new NullPointerException();
        }

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_content);

        if (null == currentFragment || !currentFragment.getClass().equals(fragment.getClass())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commitAllowingStateLoss();
            getSupportActionBar().setTitle(resTitle);
        }
    }

    @Override
    public void onServiceCompleted(Intent callbackIntent) {
        String action = callbackIntent.getAction();

        switch (action) {
            case CALLBACK_ACTION_DELETE_PROGRESS:
                if (null != mHistoryFragment) {
                    mHistoryFragment.refreshList();
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown action (" + action + ")");
        }
    }
}
