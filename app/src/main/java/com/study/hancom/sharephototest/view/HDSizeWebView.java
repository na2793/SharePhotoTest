package com.study.hancom.sharephototest.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.study.hancom.sharephototest.util.WebViewUtil;

@SuppressLint({"SetJavaScriptEnabled"})
public class HDSizeWebView extends WebView {
    WebViewUtil mWebViewUtil = new WebViewUtil();

    public HDSizeWebView(Context context) {
        this(context, null);
    }

    public HDSizeWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HDSizeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);
        setHorizontalScrollBarEnabled(false);
//        getSettings().setBuiltInZoomControls(true);
//        getSettings().setSupportZoom(true);
        setVerticalScrollBarEnabled(false);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setInitialScale(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY && MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int width = mWebViewUtil.getWidthByWidthForHD(height);
            setMeasuredDimension(width, height);
        } else if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY && MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = mWebViewUtil.getHeightByWidthForHD(width);
            setMeasuredDimension(width, height);
        }
    }
}
