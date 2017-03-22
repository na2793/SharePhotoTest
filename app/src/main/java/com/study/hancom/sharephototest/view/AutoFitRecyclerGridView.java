package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

import com.study.hancom.sharephototest.R;

public class AutoFitRecyclerGridView extends RecyclerView {
    private int mColumnWidth = -1;

    private GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);

    public AutoFitRecyclerGridView(Context context) {
        this(context, null);
    }

    public AutoFitRecyclerGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitRecyclerGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AutoFitRecyclerGridView);
            mColumnWidth = array.getDimensionPixelSize(R.styleable.AutoFitRecyclerGridView_android_columnWidth, -1);

            array.recycle();
        }

        setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        setColumnWidth(mColumnWidth);
    }

    public int getColumnWidth() {
        return mColumnWidth;
    }

    public void setColumnWidth(int columnWidth) {
        mColumnWidth = columnWidth;
        int spanCount = Math.max(1, getMeasuredWidth() / mColumnWidth);
        mLayoutManager.setSpanCount(spanCount);
    }
}