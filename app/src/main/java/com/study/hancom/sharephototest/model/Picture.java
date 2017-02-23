package com.study.hancom.sharephototest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Picture implements Parcelable {
    //final private Bitmap mBitmap;
    final private String mPath;
    final private int mWidth;
    final private int mHeight;

    public Picture(String path, int width, int height) {
        mPath = path;
        mWidth = width;
        mHeight = height;
    }

    /*public Picture(Bitmap bitmap, String path, int width, int height) {
        mBitmap = bitmap;
        mPath = path;
        mWidth = width;
        mHeight = height;
    }*/

    private Picture(Parcel in) {
        mPath = in.readString();
        mWidth = in.readInt();
        mHeight = in.readInt();
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

    /*public Bitmap getBitmap() {
        return mBitmap;
    }*/

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
    }
}
