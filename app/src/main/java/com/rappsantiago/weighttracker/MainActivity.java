package com.rappsantiago.weighttracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.rappsantiago.weighttracker.profile.ProfileSetupActivity;

public class MainActivity extends AppCompatActivity {

    public static final String PREF_KEY_PROFILE_SETUP = "MainActivity.PREF_KEY_PROFILE_SETUP";

    private SharedPreferences mSharedPrefs;

    private static final int REQUEST_PROFILE_SETUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isProfileSetupComplete = mSharedPrefs.getBoolean(PREF_KEY_PROFILE_SETUP, false);

        if (!isProfileSetupComplete) {
            // launch profile setup page
            Intent setupProfileActivity = new Intent(this, ProfileSetupActivity.class);
            startActivityForResult(setupProfileActivity, REQUEST_PROFILE_SETUP);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PROFILE_SETUP:
                if (Activity.RESULT_OK == resultCode) {
                    // save profile
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown request code : " + requestCode);
        }
    }
}
