package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class PageElementListView extends ListView {

    public static final int SCROLL_MODE_DEFAULT = 0;
    public static final int SCROLL_MODE_ONLY_EDGE = 1;

    private int mScrollMode;
    private int mSelectedItemPosition;

    public PageElementListView(Context context) {
        this(context, null);
    }
    public PageElementListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public PageElementListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        mScrollMode = SCROLL_MODE_DEFAULT;
        mSelectedItemPosition = -1;
    }
    public PageElementListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("tag", "itemClick");
            }
        });
    }

    public void setScrollMode(int scrollModeId) {
        mScrollMode = scrollModeId;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                switch (mScrollMode) {
                    case SCROLL_MODE_ONLY_EDGE :
                        //** 임시
                        int scrollAreaHeight = 150;
                        int scrollAmount = 50;

                        //** 스무스 버벅버벅
                        if (y > getBottom() - scrollAreaHeight) {
                            smoothScrollBy(scrollAmount, 0);
                        } else if (y < getTop()) {
                            smoothScrollBy(-scrollAmount, 0);
                        }
                        break;
                    case SCROLL_MODE_DEFAULT :
                        // pass til default
                    default :
                        break;
                }
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    public void setSelectedItemPosition(int position) {
        mSelectedItemPosition = position;
    }
}
