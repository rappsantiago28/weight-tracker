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
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.AbsListView;

import com.rappsantiago.weighttracker.goal.EditGoalsActivity;
import com.rappsantiago.weighttracker.profile.ProfileFragment;
import com.rappsantiago.weighttracker.profile.setup.ProfileSetupActivity;
import com.rappsantiago.weighttracker.progress.AddEditProgressActivity;
import com.rappsantiago.weighttracker.progress.HistoryFragment;
import com.rappsantiago.weighttracker.progress.StatisticsFragment;
import com.rappsantiago.weighttracker.service.WeightTrackerSaveService;
import com.rappsantiago.weighttracker.settings.SettingsActivity;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

// TODO : Implement correct logic for body fat index in the main page
// TODO : Implement edit goal / targets
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, WeightTrackerSaveService.Listener, FabVisibilityListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PROFILE_SETUP = 0;

    private static final String KEY_CURRENT_PAGE = "MainActivity.KEY_CURRENT_PAGE";

    public static final String CALLBACK_ACTION_INSERT_PROFILE = "MainActivity.CALLBACK_ACTION_INSERT_PROFILE";

    public static final String CALLBACK_ACTION_INSERT_PROGRESS = "MainActivity.CALLBACK_ACTION_INSERT_PROGRESS";

    public static final String CALLBACK_ACTION_REPLACE_PROGRESS = "MainActivity.CALLBACK_ACTION_REPLACE_PROGRESS";

    public static final String CALLBACK_ACTION_UPDATE_PROGRESS = "MainActivity.CALLBACK_ACTION_UPDATE_PROGRESS";

    public static final String CALLBACK_ACTION_DELETE_PROGRESS = "MainActivity.CALLBACK_ACTION_DELETE_PROGRESS";

    public static final String CALLBACK_ACTION_BULK_DELETE_PROGRESS = "MainActivity.CALLBACK_ACTION_BULK_DELETE_PROGRESS";

    public static final String CALLBACK_ACTION_INSERT_GOAL = "MainActivity.CALLBACK_ACTION_INSERT_GOAL";

    public static final String CALLBACK_ACTION_SETUP_PROFILE = "MainActivity.CALLBACK_ACTION_SETUP_PROFILE";

    private static final int FAB_ANIM_DURATION = 150;

    private int mCurrentPage;

    private FloatingActionButton mFab;

    private CountDownTimer mFabTimer = new CountDownTimer(1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            showMainFab();
        }
    };

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
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addProgressIntent = new Intent(MainActivity.this, AddEditProgressActivity.class);
                startActivity(addProgressIntent);
            }
        });
    }

    @Override
    public void showFab() {
        showMainFabWithTimer();
    }

    @Override
    public void hideFab() {
        hideMainFab();
    }

    @Override
    public AbsListView.OnScrollListener getDefaulScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d(TAG, "scrollState = " + scrollState);
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        hideFab();
                        break;

                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        showMainFabWithTimer();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        };
    }

    private void showMainFabWithTimer() {
        if (null != mFabTimer) {
            mFabTimer.cancel();
            mFabTimer.start();
        }
    }

    private void showMainFab() {
        mFab.animate().alpha(1.0F).translationY(0.0F)
                .setDuration(FAB_ANIM_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                mFab.setAlpha(1.0F);
                mFab.setTranslationY(0.0F);
            }
        }).start();
    }

    private void hideMainFab() {
        if (null != mFabTimer) {
            mFabTimer.cancel();
        }

        mFab.animate().alpha(0.0F).translationY(mFab.getMeasuredHeight())
                .setDuration(FAB_ANIM_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                mFab.setAlpha(0.0F);
                mFab.setTranslationY(mFab.getMeasuredHeight());
            }
        }).start();
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

                    Intent setupProfileIntent = WeightTrackerSaveService.createSetupProfileIntent(
                            this,
                            profileData,
                            MainActivity.class,
                            CALLBACK_ACTION_SETUP_PROFILE);

                    startService(setupProfileIntent);
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

        if (R.id.action_edit_goals == id) {
            Intent editGoals = new Intent(this, EditGoalsActivity.class);
            startActivity(editGoals);
            return true;
        } else if (R.id.action_settings == id) {
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
            case CALLBACK_ACTION_INSERT_PROGRESS:
                break;

            case CALLBACK_ACTION_DELETE_PROGRESS:
            case CALLBACK_ACTION_BULK_DELETE_PROGRESS:
                if (null != mHistoryFragment) {
                    mHistoryFragment.refreshList();
                }
                break;

            case CALLBACK_ACTION_SETUP_PROFILE:
                replaceMainContent(mProfileFragment, R.string.profile);
                break;

            default:
                throw new IllegalArgumentException("Unknown action (" + action + ")");
        }
    }
}
