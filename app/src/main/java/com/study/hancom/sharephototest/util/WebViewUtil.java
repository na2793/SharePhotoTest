package com.study.hancom.sharephototest.util;

import android.webkit.WebView;

public class WebViewUtil {

    private String mDefaultHTMLData = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\">\n" +
            "<meta name=\"viewport\" content=\"initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"/>\n" +
            "<title>Title of the document</title>\n" +
            "<style>\n" +
            "#container {\n" +
            "    width: 768px;\n" +
            "    height: 1024px;\n" +
            "    background: red;\n" +
            "}\n" +
            "</style>\n" +
            "</head>\n" +
            "\n" +
            "<body style=\"margin : 0px;\">\n" +
            "<div id=\"container\">" +
            "</div>" +
            "</body>\n" +
            "\n" +
            "</html>";

    public String getDefaultHTMLData() {
        return mDefaultHTMLData;
    }

    public int getHeightByWidthForHD(int width) {
        return 1024 * width / 768;
    }

    public int getWidthByWidthForHD(int height) {
        return 768 * height / 1024;
    }

    public void injectDivByScript(WebView view, int divNum) {
        view.loadUrl("javascript:(window.onload = function() {" +
                "var container = document.getElementById('container');" +
                "var oldDivNum = container.children.length;" +
                "var increment = oldDivNum - " + divNum + ";" +
                "if (increment > 0) {" +
                "for (var i = 0 ; i < increment ; i++) {" +
                "var eachDiv = document.getElementById('_' + (oldDivNum - i));" +
                "container.removeChild(eachDiv);" +
                "}" +
                "} else if (0 > increment) {" +
                "for (var i = increment ; i < 0 ; i++) {" +
                "var eachDiv = document.createElement('div');" +
                "eachDiv.setAttribute('id', '_' + (" + divNum + " + i + 1));" +
                "container.appendChild(eachDiv);" +
                "}" +
                "}" +
                "})()");
    }

    public void injectStyleByScript(WebView view, String stylePath) {
        view.loadUrl("javascript:(window.onload = function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var link = document.createElement('link');" +
                "link.rel = 'stylesheet';" +
                "link.href = '" + stylePath + "';" +
                "parent.appendChild(link);" +
                "})()");
    }

    public void injectImageByScript(WebView view, String elementId, String picturePath) {
        view.loadUrl("javascript:(window.onload = function() {" +
                "var target = document.getElementById('" + elementId + "');" +
                "target.setAttribute('style', \"background-image:url('" + picturePath + "')\");" +
                "})()");

    }
}