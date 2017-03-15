package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class AutoFitRecyclerGridView extends RecyclerView {
    private GridLayoutManager mLayoutManager;
    private int columnWidth = -1;

    public AutoFitRecyclerGridView(Context context) {
        this(context, null);
    }

    public AutoFitRecyclerGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitRecyclerGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            columnWidth = array.getDimensionPixelSize(0, -1);
            array.recycle();
        }

        mLayoutManager = new GridLayoutManager(getContext(), 1);
        setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (columnWidth > 0) {
            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
            mLayoutManager.setSpanCount(spanCount);
        }
    }
    
    public GridLayoutManager getLayoutManager() {
        return mLayoutManager;
    }
}