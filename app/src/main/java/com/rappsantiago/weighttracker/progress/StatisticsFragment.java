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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.provider.DbConstants;
import com.rappsantiago.weighttracker.provider.WeightTrackerContract;
import com.rappsantiago.weighttracker.util.DisplayUtil;
import com.rappsantiago.weighttracker.util.PreferenceUtil;
import com.rappsantiago.weighttracker.util.Util;

import java.util.ArrayList;

/**
 * Created by rappsantiago28 on 3/26/16.
 */
public class StatisticsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private LineChart mLineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        mLineChart = (LineChart) view.findViewById(R.id.line_chart);
        mLineChart.setNoDataTextDescription(getString(R.string.no_data_to_show));
        mLineChart.setDescription("");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), WeightTrackerContract.Progress.CONTENT_URI,
                DbConstants.COLUMNS_PROGRESS, null, null, WeightTrackerContract.Progress.COL_TIMESTAMP + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (null != data && 0 < data.getCount()) {

            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            int dataIdx = 0;

            String weightUnit = PreferenceUtil.getWeightUnit(getActivity());

            while (data.moveToNext()) {

                float weight = data.getFloat(DbConstants.IDX_PROGRESS_WEIGHT);
                long dateInMillis = data.getLong(DbConstants.IDX_PROGRESS_TIMESTAMP);

                switch (weightUnit) {
                    case WeightTrackerContract.Profile.WEIGHT_UNIT_KILOGRAMS:
                        entries.add(new Entry(weight, dataIdx++));
                        break;

                    case WeightTrackerContract.Profile.WEIGHT_UNIT_POUNDS:
                        entries.add(new Entry((float) Util.kilogramsToPounds(weight), dataIdx++));
                        break;

                    default:
                        throw new IllegalArgumentException();
                }

                labels.add(DisplayUtil.getReadableDate(dateInMillis));
            }

            String legend = "";

            switch (weightUnit) {
                case WeightTrackerContract.Profile.WEIGHT_UNIT_KILOGRAMS:
                    legend = getString(R.string.weight_in_kilograms);
                    break;

                case WeightTrackerContract.Profile.WEIGHT_UNIT_POUNDS:
                    legend = getString(R.string.weight_in_pounds);
                    break;

                default:
                    throw new IllegalArgumentException();
            }

            LineDataSet weightDataSet = new LineDataSet(entries, legend);
            weightDataSet.setDrawCubic(true);

            LineData lineData = new LineData(labels, weightDataSet);

            mLineChart.setData(lineData);
            mLineChart.invalidate();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLineChart.setData(null);
        mLineChart.invalidate();
    }
}
