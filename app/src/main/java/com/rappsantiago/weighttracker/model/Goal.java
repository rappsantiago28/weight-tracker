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

package com.rappsantiago.weighttracker.model;

import com.rappsantiago.weighttracker.util.PreferenceUtil;

/**
 * Created by ARKAS on 23/07/2016.
 */
public class Goal {
    private final double targetWeight;
    private final double targetBodyFatIndex;
    private final long dueDate;

    public double getTargetWeight() {
        return targetWeight;
    }

    public double getTargetBodyFatIndex() {
        return targetBodyFatIndex;
    }

    public long getDueDate() {
        return dueDate;
    }

    public static class Builder {
        private final double targetWeight;
        private double targetBodyFatIndex;
        private long dueDate;

        public Builder(double targetWeight) {
            this.targetWeight = targetWeight;
        }

        public Builder targetBodyFatIndex(double targetBodyFatIndex) {
            this.targetBodyFatIndex = targetBodyFatIndex;
            return this;
        }

        public Builder dueDate(long dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Goal build() {
            return new Goal(this);
        }
    }

    private Goal(Builder builder) {
        this.targetWeight = builder.targetWeight;
        this.targetBodyFatIndex = builder.targetBodyFatIndex;
        this.dueDate = builder.dueDate;
    }
}
