package com.study.hancom.sharephototest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PageLayout implements Parcelable {
    final private int mElementNum;
    final private String mFramePath;
    final private String mStylePath;

    public PageLayout(int elementNum, String framePath, String stylePath) {
        mElementNum = elementNum;
        mFramePath = framePath;
        mStylePath = stylePath;
    }

    private PageLayout(Parcel in) {
        mElementNum = in.readInt();
        mFramePath = in.readString();
        mStylePath = in.readString();
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

    public String getFramePath() {
        return mFramePath;
    }

    public String getStylePath() {
        return mStylePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mElementNum);
        dest.writeString(mFramePath);
        dest.writeString(mStylePath);
    }
}
