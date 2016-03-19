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

import org.joda.time.LocalDate;

/**
 * Created by rappsantiago28 on 3/19/16.
 */
public class Profile {
    private final long id;
    private final String name;
    private final LocalDate birthday;
    private final String gender;
    private final double weight;
    private final double height;

    public Profile(long id, String name, LocalDate birthday, String gender, double weight, double height) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }
}
