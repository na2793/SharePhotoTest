package com.study.hancom.sharephototest.model;

import android.graphics.Bitmap;

public class Picture {
    final private Bitmap mBitmap;
    final private String mPath;
    final private int mWidth;
    final private int mHeight;

    public Picture(Bitmap bitmap, String path, int width, int height) {
        mBitmap = bitmap;
        mPath = path;
        mWidth = width;
        mHeight = height;
    }

    public String getPath() {
        return mPath;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
}
