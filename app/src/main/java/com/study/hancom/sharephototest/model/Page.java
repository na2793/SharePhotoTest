package com.study.hancom.sharephototest.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Page implements Parcelable {
    static final private PageLayoutFactory pageLayoutFactory = new PageLayoutFactory();

    private PageLayout mLayout;
    private List<Picture> mPictureList = new ArrayList<>();

    public Page(int elementNum) throws Exception {
        this(pageLayoutFactory.getPageLayout(elementNum));
    }

    public Page(PageLayout layout) {
        mLayout = layout;
    }

    private Page(Parcel in) {
        mLayout = in.readParcelable(PageLayout.class.getClassLoader());
        mPictureList = in.createTypedArrayList(Picture.CREATOR);
    }

    public static final Creator<Page> CREATOR = new Creator<Page>() {
        @Override
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        @Override
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mLayout, flags);
        dest.writeTypedList(mPictureList);
    }
}
