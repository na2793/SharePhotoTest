package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class PageElementListView extends ListView {

    public PageElementListView(Context context) {
        this(context, null);
    }
    public PageElementListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public PageElementListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    public PageElementListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
