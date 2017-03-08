package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.study.hancom.sharephototest.util.WebViewUtil;

public class A4SizeWebView extends WebView {
    WebViewUtil mWebViewUtil = new WebViewUtil();

    public A4SizeWebView(Context context) {
        this(context, null);
    }

    public A4SizeWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public A4SizeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        getSettings().setBuiltInZoomControls(false);
        getSettings().setSupportZoom(false);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setInitialScale(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY && MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int width = mWebViewUtil.getWidthByWidthForA4(height);
            setMeasuredDimension(width, height);
        } else if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY && MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = mWebViewUtil.getHeightByWidthForA4(width);
            setMeasuredDimension(width, height);
        }
    }
}
