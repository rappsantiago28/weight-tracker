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

package com.rappsantiago.weighttracker.progress;

import android.support.v4.app.Fragment;

import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.SimpleActivityWithFragment;

/**
 * Created by rappsantiago28 on 3/26/16.
 */
public class AddProgressActivity extends SimpleActivityWithFragment {

    @Override
    protected int getResTitle() {
        return R.string.add_progress;
    }

    @Override
    protected Fragment getContent() {
        return new AddProgressFragment();
    }
}
