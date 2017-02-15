package com.study.hancom.sharephototest.model;

public class PageLayout {
    final private String mData;
    final private int mElementNum;

    public PageLayout(String data, int elementNum) {
        mData = data;
        mElementNum = elementNum;
    }

    public String getData() {
        return mData;
    }

    public int getElementNum() {
        return mElementNum;
    }
}
