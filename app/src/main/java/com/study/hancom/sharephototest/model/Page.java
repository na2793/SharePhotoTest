package com.study.hancom.sharephototest.model;

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

    public void setLayout(int elementNum) throws Exception {
        mLayout = pageLayoutFactory.getPageLayout(elementNum);
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

    public int getPictureCount() {
        return mPictureList.size();
    }

    public void addPicture(Picture picture) {
        addPicture(mPictureList.size(), picture);
    }

    public void addPicture(int position, Picture picture) {
        mPictureList.add(position, picture);
    }

    public Picture removePicture(int position) {
        return mPictureList.remove(position);
    }

    public void reorderPicture(int fromPosition, int toPosition) {
        Picture tempPicture = removePicture(fromPosition);
        addPicture(toPosition, tempPicture);
    }

    public void clear() {
        mLayout = null;
        mPictureList.clear();
    }
}
