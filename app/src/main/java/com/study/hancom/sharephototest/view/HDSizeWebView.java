package com.study.hancom.sharephototest.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.study.hancom.sharephototest.util.WebViewUtil;

@SuppressLint({"SetJavaScriptEnabled"})
public class HDSizeWebView extends WebView {
    private GestureDetector mGestureDetector;

    public HDSizeWebView(Context context) {
        this(context, null);
    }

    public HDSizeWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HDSizeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSettings();
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return performClick();
            }
        });
    }

    public void initSettings() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setInitialScale(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY && MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int width = WebViewUtil.getWidthByWidthForHD(height);
            setMeasuredDimension(width, height);
        } else if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY && MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = WebViewUtil.getHeightByWidthForHD(width);
            setMeasuredDimension(width, height);
        }
    }
}
