package com.study.hancom.sharephototest.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Page {
    private PageLayout mLayout;
    private List<Picture> mPictureList;
    static final private PageLayoutFactory pageLayoutFactory = new PageLayoutFactory();

    public Page(int elementNum) throws Exception {
        this(pageLayoutFactory.getPageLayout(elementNum));
    }

    public Page(PageLayout layout) {
        mLayout = layout;
        mPictureList = new ArrayList<>();
    }

    public void setLayout(PageLayout layout) {
        mLayout = layout;
    }

    public PageLayout getLayout() {
        return mLayout;
    }

    public Picture getPicture(int position) {
        return mPictureList.get(position);
    }

    public void addPicture(Picture picture) throws Exception {
        addPicture(mPictureList.size(), picture);
    }

    public void addPicture(int position, Picture picture) throws Exception {
        if (mLayout.getElementCount() > mPictureList.size()) {
            mPictureList.add(position, picture);
        } else {
            //** exception
            Log.v("tag", "더이상 사진을 넣을 수 없습니다.");
            throw new Exception();
        }
    }

    public Picture removePicture(int position) {
        return mPictureList.remove(position);
    }

    public void reorderPicture(int fromPosition, int toPosition) throws Exception {
        Picture tempPicture = removePicture(fromPosition);
        addPicture(toPosition, tempPicture);
    }

    public void clear() {
        mLayout = null;
        mPictureList.clear();
    }
}
