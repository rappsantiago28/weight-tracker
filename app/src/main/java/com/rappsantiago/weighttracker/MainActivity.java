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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import com.rappsantiago.weighttracker.profile.NameBirthdayGenderFragment;
import com.rappsantiago.weighttracker.profile.ProfileSetupActivity;
import com.rappsantiago.weighttracker.profile.WeightHeightFragment;
import com.rappsantiago.weighttracker.settings.SettingsActivity;
import com.rappsantiago.weighttracker.util.PreferenceUtil;
import com.rappsantiago.weighttracker.util.Util;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences mSharedPrefs;

    private static final int REQUEST_PROFILE_SETUP = 0;

    private ProfileFragment mProfileFragment;

    private HistoryFragment mHistoryFragment;

    private StatisticsFragment mStatisticsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isProfileSetupComplete = false;

        try (Cursor cursor = getContentResolver().query(Profile.CONTENT_URI, null, null, null, null)) {
            isProfileSetupComplete = cursor.getCount() > 0;
            if (cursor.moveToFirst()) {
                for (String col : cursor.getColumnNames()) {
                    Log.d(TAG, col + " : " + cursor.getString(cursor.getColumnIndex(col)));
                }
            }
        }

        mProfileFragment = new ProfileFragment();
        mHistoryFragment = new HistoryFragment();
        mStatisticsFragment = new StatisticsFragment();

        if (!isProfileSetupComplete) {
            // launch profile setup page
            Intent setupProfileActivity = new Intent(this, ProfileSetupActivity.class);
            startActivityForResult(setupProfileActivity, REQUEST_PROFILE_SETUP);
        } else {
            replaceMainContent(mProfileFragment, R.string.profile);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PROFILE_SETUP:
                if (Activity.RESULT_OK == resultCode) {
                    // save profile
                    Bundle profileData = data.getExtras();

                    ContentValues values = new ContentValues();

                    String name = profileData.getString(NameBirthdayGenderFragment.KEY_NAME);
                    long birthdayInMillis = profileData.getLong(NameBirthdayGenderFragment.KEY_BIRTHDAY);
                    String gender = profileData.getString(NameBirthdayGenderFragment.KEY_GENDER);
                    double weight = profileData.getDouble(WeightHeightFragment.KEY_WEIGHT);
                    String weightUnit = profileData.getString(WeightHeightFragment.KEY_WEIGHT_UNIT);
                    double height = profileData.getDouble(WeightHeightFragment.KEY_HEIGHT);
                    String heightUnit = profileData.getString(WeightHeightFragment.KEY_HEIGHT_UNIT);

                    values.put(Profile.COL_NAME, name);
                    values.put(Profile.COL_BIRTHDAY, birthdayInMillis);
                    values.put(Profile.COL_GENDER, gender);

                    if (Profile.WEIGHT_UNIT_KILOGRAMS.equals(weightUnit)) {
                        values.put(Profile.COL_WEIGHT, weight);
                    } else {
                        values.put(Profile.COL_WEIGHT, Util.poundsToKilograms(weight));
                    }

                    if (Profile.HEIGHT_UNIT_CENTIMETERS.equals(heightUnit)) {
                        values.put(Profile.COL_HEIGHT, height);
                    } else {
                        double inches = profileData.getDouble(WeightHeightFragment.KEY_HEIGHT_INCHES);
                        values.put(Profile.COL_HEIGHT, Util.footInchesToCentimeters(height, inches));
                    }

                    Uri profileUri = getContentResolver().insert(Profile.CONTENT_URI, values);

                    if (null != profileUri) {
                        Log.d(TAG, "weightUnit = " + weightUnit + ", heightUnit = " + heightUnit);
                        PreferenceUtil.setWeightUnit(this, weightUnit);
                        PreferenceUtil.setHeightUnit(this, heightUnit);

                        replaceMainContent(mProfileFragment, R.string.profile);
                    }
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

        switch (itemId) {
            case R.id.nav_profile:
                replaceMainContent(mProfileFragment, R.string.profile);
                break;

            case R.id.nav_history:
                replaceMainContent(mHistoryFragment, R.string.history);
                break;

            case R.id.nav_statistics:
                replaceMainContent(mStatisticsFragment, R.string.statistics);
                break;

            case R.id.nav_share:
                break;

            case R.id.nav_about:
                break;

            default:
                throw new IllegalArgumentException();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
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
}
