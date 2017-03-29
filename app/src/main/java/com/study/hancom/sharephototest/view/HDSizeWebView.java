package com.study.hancom.sharephototest.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.study.hancom.sharephototest.util.WebViewUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressLint({"SetJavaScriptEnabled"})
public class HDSizeWebView extends WebView {
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
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
//        getSettings().setBuiltInZoomControls(true);
//        getSettings().setSupportZoom(true);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setInitialScale(16);
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
