package com.study.hancom.sharephototest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PageLayout implements Parcelable {
    private final String mPath;
    private final int mElementNum;

    public PageLayout(int elementNum, String stylePath) {
        mElementNum = elementNum;
        mPath = stylePath;
    }

    private PageLayout(Parcel in) {
        mElementNum = in.readInt();
        mPath = in.readString();
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

    public int getElementNum() {
        return mElementNum;
    }

    public String getStylePath() {
        return mPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mElementNum);
        dest.writeString(mPath);
    }
}
