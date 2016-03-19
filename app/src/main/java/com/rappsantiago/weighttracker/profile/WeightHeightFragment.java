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

package com.rappsantiago.weighttracker.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.util.Util;

/**
 * Created by rappsantiago28 on 3/13/16.
 */
public class WeightHeightFragment extends Fragment implements FragmentWithProfileData, AdapterView.OnItemClickListener {

    private static final String KEY_WEIGHT = "WeightHeightFragment.KEY_WEIGHT";

    private static final String KEY_WEIGHT_UNIT = "WeightHeightFragment.KEY_WEIGHT_UNIT";

    private static final String KEY_HEIGHT = "WeightHeightFragment.KEY_HEIGHT";

    private static final String KEY_HEIGHT_UNIT = "WeightHeightFragment.KEY_HEIGHT_UNIT";

    private EditText mTxtWeight;

    private ArrayAdapter<CharSequence> mWeightUnitAdapter;

    private int mWeightUnitPos;

    private EditText mTxtHeight;

    private ArrayAdapter<CharSequence> mHeightUnitAdapter;

    private int mHeightUnitPos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight_height, container, false);

        mTxtWeight = (EditText) view.findViewById(R.id.txt_weight);
        mTxtHeight = (EditText) view.findViewById(R.id.txt_height);

        Spinner weightUnitDropdown = (Spinner) view.findViewById(R.id.dropdown_weight_unit);
        mWeightUnitAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.arr_weight_unit, android.R.layout.simple_spinner_item);
        weightUnitDropdown.setAdapter(mWeightUnitAdapter);

        Spinner heightUnitDropdown = (Spinner) view.findViewById(R.id.dropdown_height_unit);
        mHeightUnitAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.arr_height_unit, android.R.layout.simple_spinner_item);
        heightUnitDropdown.setAdapter(mHeightUnitAdapter);

        return view;
    }

    @Override
    public Bundle getProfileData() {
        Bundle data = new Bundle();

        String strWeight = mTxtWeight.getText().toString();
        double weight = Util.parseDouble(strWeight, 0.0);
        String weightUnit = mWeightUnitAdapter.getItem(mWeightUnitPos).toString();

        String strHeight = mTxtHeight.getText().toString();
        double height = Util.parseDouble(strHeight, 0.0);
        String heightUnit = mHeightUnitAdapter.getItem(mHeightUnitPos).toString();


        data.putDouble(KEY_WEIGHT, weight);
        data.putString(KEY_WEIGHT_UNIT, weightUnit);
        data.putDouble(KEY_HEIGHT, height);
        data.putString(KEY_HEIGHT_UNIT, heightUnit);

        return data;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int viewId = view.getId();

        switch (viewId) {
            case R.id.dropdown_weight_unit:
                mWeightUnitPos = position;
                break;

            case R.id.dropdown_height_unit:
                mHeightUnitPos = position;
                break;

            default:
                throw new IllegalArgumentException();
        }
    }
}
