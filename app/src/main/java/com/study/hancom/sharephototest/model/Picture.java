package com.study.hancom.sharephototest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Picture implements Parcelable {
    final private String mPath;

    public Picture(String path) {
        mPath = path;
    }

    private Picture(Parcel in) {
        mPath = in.readString();
    }

    public static final Creator<Picture> CREATOR = new Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

    public String getPath() {
        return mPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
    }
}
