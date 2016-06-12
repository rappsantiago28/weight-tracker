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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.rappsantiago.weighttracker.util.Util;

/**
 * Created by ARKAS on 12/06/2016.
 */
public class ConfirmationDialogFragment extends DialogFragment {

    private static final String KEY_TITLE = "com.rappsantiago.weighttracker.dialog.ConfirmationDialogFragment.KEY_TITLE";

    private static final String KEY_MESSAGE = "com.rappsantiago.weighttracker.dialog.ConfirmationDialogFragment.KEY_MESSAGE";

    private DialogInterface.OnClickListener mPositiveClickListener;

    private DialogInterface.OnClickListener mNegativeClickListener;

    public static ConfirmationDialogFragment createDialog(String title, String message) {
        ConfirmationDialogFragment dialog = new ConfirmationDialogFragment();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);

        dialog.setArguments(args);

        return dialog;
    }

    public void setPositiveClickListener(DialogInterface.OnClickListener l) {
        mPositiveClickListener = l;
    }

    public void setNegativeClickListener(DialogInterface.OnClickListener l) {
        mNegativeClickListener = l;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        if (null != args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            String title = args.getString(KEY_TITLE, "");
            String message = args.getString(KEY_MESSAGE, "");

            builder.setTitle(title).setMessage(message);

            if (null != mPositiveClickListener) {
                builder.setPositiveButton(android.R.string.yes, mPositiveClickListener);
            }

            if (null != mNegativeClickListener) {
                builder.setNegativeButton(android.R.string.no, mNegativeClickListener);
            } else {
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
            }

            Util.hideSoftKeyboard(getActivity(), getActivity().getCurrentFocus());

            return builder.create();
        }

        return super.onCreateDialog(savedInstanceState);
    }
}
