package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.study.hancom.sharephototest.base.CustomGridView;

import java.util.HashSet;
import java.util.Set;

public class PageElementGridView extends CustomGridView {

    private int mSelectedItemPosition;
    private Set<Integer> mMultipleSelectedItemPositionSet;

    private boolean mIsMultipleSelectItemMode;

    public PageElementGridView(Context context) {
        this(context, null);
    }
    public PageElementGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public PageElementGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    public PageElementGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mIsMultipleSelectItemMode = false;
        mSelectedItemPosition = -1;
    }

    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    public void setSelectedItemPosition(int position) {
        mSelectedItemPosition = position;
    }

    public boolean isMultipleSelectItemMode() {
        return mIsMultipleSelectItemMode;
    }

    public void startMultipleSelectItemMode() {
        mIsMultipleSelectItemMode = true;
        mMultipleSelectedItemPositionSet = new HashSet<>();
    }

    public void stopMultipleSelectItemMode() {
        mIsMultipleSelectItemMode = false;
        clearMultipleSelectedItemPosition();
        mMultipleSelectedItemPositionSet = null;
    }

    public boolean isMultipleSelectedItem(int position) {
        return mMultipleSelectedItemPositionSet.contains(position);
    }

    public void addMultipleSelectedItemPosition(int position) {
        mMultipleSelectedItemPositionSet.add(position);
        Log.v("tag", mMultipleSelectedItemPositionSet.toString());
    }

    public void removeMultipleSelectedItemPosition(int position) {
        mMultipleSelectedItemPositionSet.remove(position);
    }

    public void clearMultipleSelectedItemPosition() {
        mMultipleSelectedItemPositionSet.clear();
    }
}