package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class AutoFitRecyclerGridView extends RecyclerView {

    private static final int LAYOUT_MODE_VERTICAL = 0;
    private static final int LAYOUT_MODE_HORIZONTAL = 1;

    private Context mContext;

    private int mCurrentLayoutMode;
    private GridLayoutManager mVerticalLayoutManager;
    private LinearLayoutManager mHorizontalLayoutManager;

    private int mColumnWidth = -1;

    public AutoFitRecyclerGridView(Context context, int layoutMode) {
        this(context, null, layoutMode);
    }

    public AutoFitRecyclerGridView(Context context, AttributeSet attrs, int layoutMode) {
        this(context, attrs, 0, layoutMode);
    }

    public AutoFitRecyclerGridView(Context context, AttributeSet attrs, int defStyle, int layoutMode) {
        super(context, attrs, defStyle);

        mContext = context;

        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            mColumnWidth = array.getDimensionPixelSize(0, -1);
            array.recycle();
        }

        mHorizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mVerticalLayoutManager = new GridLayoutManager(mContext, 1);

        switch (layoutMode) {
            case LAYOUT_MODE_HORIZONTAL:
                mCurrentLayoutMode = LAYOUT_MODE_HORIZONTAL;
                setLayoutManager(mHorizontalLayoutManager);
                break;
            case LAYOUT_MODE_VERTICAL:
                // pass ~ default
            default:
                mCurrentLayoutMode = LAYOUT_MODE_VERTICAL;
                setLayoutManager(mVerticalLayoutManager);
                break;
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (mColumnWidth > 0) {
            switch (mCurrentLayoutMode) {
                case LAYOUT_MODE_HORIZONTAL:
                    //mHorizontalLayoutManager.setInitialPrefetchItemCount();
                    break;
                case LAYOUT_MODE_VERTICAL:
                    // pass ~ default
                default:
                    int spanCount = Math.max(1, getMeasuredWidth() / mColumnWidth);
                    mVerticalLayoutManager.setSpanCount(spanCount);
                    break;
            }
        }
    }

    public void setLayoutModeVertical() {
        mCurrentLayoutMode = LAYOUT_MODE_VERTICAL;
        setLayoutManager(mVerticalLayoutManager);
    }

    public void setLayoutModeHorizontal() {
        mCurrentLayoutMode = LAYOUT_MODE_HORIZONTAL;
        setLayoutManager(mHorizontalLayoutManager);
    }

    public LayoutManager getLayoutManager() {
        switch (mCurrentLayoutMode) {
            case LAYOUT_MODE_HORIZONTAL:
                return mHorizontalLayoutManager;
            case LAYOUT_MODE_VERTICAL:
                // pass ~ default
            default:
                return mVerticalLayoutManager;
        }
    }
}