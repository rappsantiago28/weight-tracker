package com.rappsantiago.weighttracker.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rappsantiago.weighttracker.R;

/**
 * Created by rappsantiago28 on 3/13/16.
 */
public class WeightHeightFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight_height, container, false);
        return view;
    }
}
