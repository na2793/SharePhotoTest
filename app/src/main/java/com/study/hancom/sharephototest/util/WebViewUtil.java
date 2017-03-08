package com.study.hancom.sharephototest.util;

import android.webkit.WebView;

public class WebViewUtil {

    public int getHeightByWidthForA4(int width) {
        return 297 * width / 210;
    }

    public int getWidthByWidthForA4(int height) {
        return 210 * height / 297;
    }

    public void injectStyleByScript(WebView view, String stylePath) {
        view.loadUrl("javascript:(window.onload = function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var link = document.createElement('link');" +
                "link.rel = 'stylesheet';" +
                "link.href = '" + stylePath + "';" +
                "parent.appendChild(link)" +
                "})()");
    }

    public void injectImageByScript(WebView view, String elementId, String picturePath) {
        view.loadUrl("javascript:(window.onload = function() {" +
                "var target = document.getElementById('" + elementId + "');" +
                "target.setAttribute('style', \"background-image:url('" + picturePath + "')\");" +
                "})()");
    }
}
