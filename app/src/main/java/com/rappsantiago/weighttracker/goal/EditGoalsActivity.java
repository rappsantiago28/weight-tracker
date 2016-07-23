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

package com.rappsantiago.weighttracker.goal;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.rappsantiago.weighttracker.MainActivity;
import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.SimpleActivityWithFragment;
import com.rappsantiago.weighttracker.model.Goal;
import com.rappsantiago.weighttracker.service.WeightTrackerSaveService;
import com.rappsantiago.weighttracker.util.PreferenceUtil;

/**
 * Created by ARKAS on 23/07/2016.
 */
public class EditGoalsActivity extends SimpleActivityWithFragment
        implements WeightTrackerSaveService.Listener {

    public static final String CALLBACK_ACTION_UPDATE_GOAL = "EditGoalsActivity.CALLBACK_ACTION_UPDATE_GOAL";

    @Override
    protected void setup(Intent intent) {

    }

    @Override
    protected int getResTitle() {
        return R.string.action_edit_goals;
    }

    @Override
    protected Fragment getContent() {
        return EditGoalsFragment.createFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_goals_context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (R.id.action_save == id) {
            EditGoalsFragment editGoalsFragment = (EditGoalsFragment) mContentFragment;
            Goal currentGoal = editGoalsFragment.getCurrentGoal();

            Intent updateGoalIntent = WeightTrackerSaveService.createUpdateGoalIntent(
                    this,
                    currentGoal.getGoalId(),
                    currentGoal.getTargetWeight(),
                    currentGoal.getTargetBodyFatIndex(),
                    currentGoal.getDueDate(),
                    PreferenceUtil.getWeightUnit(this),
                    EditGoalsActivity.class,
                    CALLBACK_ACTION_UPDATE_GOAL);

            startService(updateGoalIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    public void onServiceCompleted(Intent callbackIntent) {
        String action = callbackIntent.getAction();

        switch (action) {
            case CALLBACK_ACTION_UPDATE_GOAL:
                finish();
                break;

            default:
                throw new IllegalArgumentException("Unknown action (" + action + ")");
        }
    }
}
