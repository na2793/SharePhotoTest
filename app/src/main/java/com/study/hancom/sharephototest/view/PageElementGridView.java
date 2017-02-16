package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.util.AttributeSet;

import com.study.hancom.sharephototest.base.CustomGridView;

public class PageElementGridView extends CustomGridView {

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
    }
}
