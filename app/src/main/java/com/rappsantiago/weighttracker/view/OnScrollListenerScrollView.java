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

package com.rappsantiago.weighttracker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * Created by ARKAS on 17/07/2016.
 *
 * ScrollView that mimics the behaviour of ListView's OnScrollListener
 */
public class OnScrollListenerScrollView extends ScrollView {

    private static final String TAG = OnScrollListenerScrollView.class.getSimpleName();

    private AbsListView.OnScrollListener mOnScrollListener;

    private boolean mIsScrolled;

    private boolean mIsDown;

    private float mPrevY;

    public OnScrollListenerScrollView(Context context) {
        super(context);
    }

    public OnScrollListenerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnScrollListenerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        boolean shouldBroadcastScroll = t != oldt;

        if (shouldBroadcastScroll) {
            if (null != mOnScrollListener && !mIsScrolled && mIsDown) {
                mOnScrollListener.onScrollStateChanged(null, AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                mIsScrolled = true;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsDown = true;
                mPrevY = ev.getRawY();

            case MotionEvent.ACTION_MOVE:
                boolean shouldMove = Math.abs(mPrevY - ev.getRawY()) >= 50;
                if (shouldMove) {
                    if (null != mOnScrollListener && !mIsScrolled && mIsDown) {
                        mOnScrollListener.onScrollStateChanged(null, AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                        mIsScrolled = true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (null != mOnScrollListener) {
                    mOnScrollListener.onScrollStateChanged(null, AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
                }
                mIsDown = false;
                mIsScrolled = false;
                break;
        }

        return super.onTouchEvent(ev);
    }
}
