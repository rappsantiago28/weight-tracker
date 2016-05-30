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

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rappsantiago.weighttracker.MainActivity;
import com.rappsantiago.weighttracker.R;
import com.rappsantiago.weighttracker.provider.DbConstants;
import com.rappsantiago.weighttracker.service.WeightTrackerSaveService;
import com.rappsantiago.weighttracker.util.Util;

import java.util.Set;

import static com.rappsantiago.weighttracker.provider.WeightTrackerContract.*;

/**
 * Created by rappsantiago28 on 3/26/16.
 */
public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = HistoryFragment.class.getSimpleName();

    private ListView mListView;

    private HistoryCursorAdapter mCursorAdapter;

    // TODO : create edit page

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mListView = (ListView) view;

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    mCursorAdapter.addSelectedId(id);
                } else {
                    mCursorAdapter.removeSelectedId(id);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.progress_list_context_menu, menu);
                mCursorAdapter.startMultipleSelection();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                Set<Long> selectedIds = mCursorAdapter.getSelectedIds();

                switch (item.getItemId()) {
                    case R.id.action_delete:
                        long[] arrSelectedIds = Util.convertLongSetToArray(selectedIds);
                        Intent deleteProgressIntent = WeightTrackerSaveService.createBulkDeleteProgressIntent(
                                getActivity(),
                                arrSelectedIds,
                                MainActivity.class,
                                MainActivity.CALLBACK_ACTION_BULK_DELETE_PROGRESS);
                        getActivity().startService(deleteProgressIntent);
                        break;

                    default:
                        return false;
                }

                mode.finish();

                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mCursorAdapter.stopMultipleSelection();
            }
        });

        mCursorAdapter = new HistoryCursorAdapter(getContext());

        mListView.setAdapter(mCursorAdapter);

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
        refreshList();
    }

    public void refreshList() {
        if (null != getActivity() && isAdded()) {
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), Progress.CONTENT_URI,
                DbConstants.COLUMNS_PROGRESS, null, null, Progress.COL_TIMESTAMP + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
