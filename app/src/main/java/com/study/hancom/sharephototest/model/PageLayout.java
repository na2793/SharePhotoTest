package com.study.hancom.sharephototest.model;

public class PageLayout {
    final private String mData;
    final private int mElementCount;

    public PageLayout(String data, int elementCount) {
        mData = data;
        mElementCount = elementCount;
    }

    public String getData() {
        return mData;
    }

    public int getElementCount() {
        return mElementCount;
    }
}
