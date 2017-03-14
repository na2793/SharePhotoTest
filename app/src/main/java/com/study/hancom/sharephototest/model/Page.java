package com.study.hancom.sharephototest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.study.hancom.sharephototest.exception.LayoutNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Page implements Parcelable {

    private static final PageLayoutManager mPageLayoutManager = PageLayoutManager.getInstance();

    private PageLayout mLayout;
    private List<Picture> mPictureList = new ArrayList<>();

    public Page(int layoutType) throws LayoutNotFoundException {
        mLayout = mPageLayoutManager.getPageLayout(layoutType);
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

    public static Set<Integer> getAllPageLayoutType() {
        return mPageLayoutManager.getAllType();
    }

    public PageLayout getLayout() {
        return mLayout;
    }

    public void setLayout(int layoutType) throws LayoutNotFoundException {
        mLayout = mPageLayoutManager.getPageLayout(layoutType);
    }

    public void setLayout(PageLayout layout) {
        mLayout = layout;
    }

    public Picture getPicture(int position) {
        return mPictureList.get(position);
    }

    public int getPictureCount() {
        return mPictureList.size();
    }

    public void addPicture(Picture picture) {
        mPictureList.add(picture);
    }

    public void addPicture(int position, Picture picture) {
        mPictureList.add(position, picture);
    }

    public Picture removePicture(int position) {
        return mPictureList.remove(position);
    }

    public void reorderPicture(int fromPosition, int toPosition) {
        Picture target = removePicture(fromPosition);
        addPicture(toPosition, target);
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
