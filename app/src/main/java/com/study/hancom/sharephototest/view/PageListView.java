package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class PageListView extends ListView {

    private boolean mIsEditMode = false;
    private int mUpEventItemPosition = -1;

    public PageListView(Context context) {
        this(context, null);
    }
    public PageListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public PageListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    public PageListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getUpEventItemPosition() {
        return mUpEventItemPosition;
    }

    public void startEditMode() {
        mIsEditMode = true;
    }

    public void stopEditMode() {
        mIsEditMode = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mIsEditMode) {
                    //** 임시
                    int scrollAreaHeight = 150;
                    int scrollAmount = 50;

                    //** 스무스 버벅버벅
                    if (y > getBottom() - scrollAreaHeight) {
                        smoothScrollBy(scrollAmount, 0);
                    } else if (y < getTop()) {
                        smoothScrollBy(-scrollAmount, 0);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                mUpEventItemPosition = pointToPosition(x, y);

                break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
