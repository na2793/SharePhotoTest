package com.study.hancom.sharephototest.util;

import android.util.Log;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class WebViewUtil {

    public void setA4SizeByWidth(WebView view, int width) {
        view.setLayoutParams(new LinearLayout.LayoutParams(width, 297 * width / 210));
    }

    public void setA4SizeByHeight(WebView view, int height) {
        view.setLayoutParams(new LinearLayout.LayoutParams(210 * height / 297, height));
    }

    public void injectStyleByScript(WebView view, String stylePath) {
        view.loadUrl("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var link = document.createElement('link');" +
                "link.rel = 'stylesheet';" +
                "link.href = '" + stylePath + "';" +
                "parent.appendChild(link)" +
                "})()");
    }

    public void injectImageByScript(WebView view, String elementId, String picturePath) {
        view.loadUrl("javascript:(function() {" +
                "var target = document.getElementById('" + elementId + "');" +
                "target.setAttribute('style', \"background-image:url('" + picturePath + "')\");" +
                "})()");
    }
}
