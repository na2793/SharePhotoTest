package com.study.hancom.sharephototest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PageLayout implements Parcelable {
    final private String mData;
    final private int mElementNum;

    public PageLayout(String data, int elementNum) {
        mData = data;
        mElementNum = elementNum;
    }

    private PageLayout(Parcel in) {
        mData = in.readString();
        mElementNum = in.readInt();
    }

    public static final Creator<PageLayout> CREATOR = new Creator<PageLayout>() {
        @Override
        public PageLayout createFromParcel(Parcel in) {
            return new PageLayout(in);
        }

        @Override
        public PageLayout[] newArray(int size) {
            return new PageLayout[size];
        }
    };

    public String getData() {
        return mData;
    }

    public int getElementNum() {
        return mElementNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mData);
        dest.writeInt(mElementNum);
    }
}
