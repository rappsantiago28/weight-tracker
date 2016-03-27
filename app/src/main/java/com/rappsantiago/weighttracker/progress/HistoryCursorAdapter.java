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

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.provider.DbConstants;
import com.rappsantiago.weighttracker.util.DisplayUtil;

/**
 * Created by rappsantiago28 on 3/27/16.
 */
public class HistoryCursorAdapter extends CursorAdapter {


    public HistoryCursorAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setTag(vh);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();

        double newWeight = cursor.getDouble(DbConstants.IDX_PROGRESS_NEW_WEIGHT);
        long date = cursor.getLong(DbConstants.IDX_PROGRESS_TIMESTAMP);

        vh.weight.setText(DisplayUtil.getFormattedWeight(context, newWeight, null));
        vh.date.setText(DisplayUtil.getReadableDate(date));
    }

    private final class ViewHolder {
        private final TextView weight;
        private final TextView date;

        private ViewHolder(View view) {
            weight = (TextView) view.findViewById(R.id.lbl_weight);
            date = (TextView) view.findViewById(R.id.lbl_date);
        }
    }
}
