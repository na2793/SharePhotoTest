package com.study.hancom.sharephototest.model;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.study.hancom.sharephototest.util.MathUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PageLayoutFactory implements Parcelable {

    //** 임시 xml로 뺄 것
    final static private String mLayoutFrameFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "SharePhoto" + File.separator + "layout" + File.separator + "frame" + File.separator;
    final static private String mLayoutStyleFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "SharePhoto" + File.separator + "layout" + File.separator + "style" + File.separator;

    private Map<Integer, List<PageLayout>> mLayoutMap = new HashMap<>();

    PageLayoutFactory() {
    }

    private PageLayoutFactory(Parcel in) {
        int keyCount = in.readInt();
        for (int i = 0; i < keyCount; i++) {
            List<PageLayout> eachList = new ArrayList<>();
            in.readList(eachList, PageLayout.class.getClassLoader());
            mLayoutMap.put(i, eachList);
        }
    }

    public static final Creator<PageLayoutFactory> CREATOR = new Creator<PageLayoutFactory>() {
        @Override
        public PageLayoutFactory createFromParcel(Parcel in) {
            return new PageLayoutFactory(in);
        }

        @Override
        public PageLayoutFactory[] newArray(int size) {
            return new PageLayoutFactory[size];
        }
    };

    PageLayout getPageLayout(int elementNum) throws Exception {
        PageLayout pageLayout = null;
        List<PageLayout> pageLayoutList = mLayoutMap.get(elementNum);

        if (pageLayoutList == null) {
            pageLayoutList = findLayoutFile(elementNum);
            mLayoutMap.put(elementNum, pageLayoutList);
        }

        if (pageLayoutList.size() > 0) {
            pageLayout = pageLayoutList.get(MathUtil.getRandomMath(pageLayoutList.size() - 1, 0));
        }

        return pageLayout;
    }

    private List<PageLayout> findLayoutFile(int elementNum) throws Exception {
        List<PageLayout> pageLayoutList = new ArrayList<>();
        File frameFile = new File(mLayoutFrameFolderPath + elementNum + ".html");

        if (frameFile.exists()) {   //** 이렇게 하지 말 것 (동기화 X)
            File styleFolder = new File(mLayoutStyleFolderPath + elementNum);
            File[] styleList = styleFolder.listFiles();
            int styleListLength = styleList.length;

            if (styleListLength > 0) {
                for (File eachStyleFile : styleList) {
                    pageLayoutList.add(new PageLayout(elementNum, frameFile.getAbsolutePath(), eachStyleFile.getAbsolutePath()));
                }
            } else {
                //** exception style not found
                Log.v("tag", "style not found");
                throw new Exception();
            }
        } else {
            //** exception frame not found
            Log.v("tag", "frame not found");
            throw new Exception();
        }

        return pageLayoutList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mLayoutMap.size());
        for (int eachKey : mLayoutMap.keySet()) {
            dest.writeTypedList(mLayoutMap.get(eachKey));
        }
    }
}