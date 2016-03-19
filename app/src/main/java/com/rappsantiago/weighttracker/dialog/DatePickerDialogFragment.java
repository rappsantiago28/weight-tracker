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

package com.rappsantiago.weighttracker.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * Created by rappsantiago28 on 3/19/16.
 */
public class DatePickerDialogFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener mOnDateSetListener;

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener l) {
        mOnDateSetListener = l;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalDate now = new LocalDate();

        int year = now.getYear();
        int monthOfYear = now.getMonthOfYear();
        int dayOfMonth = now.getDayOfMonth();

        return new DatePickerDialog(getActivity(), mOnDateSetListener, year, monthOfYear, dayOfMonth);
    }

}
