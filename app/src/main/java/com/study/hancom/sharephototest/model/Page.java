package com.study.hancom.sharephototest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.study.hancom.sharephototest.exception.FrameFileNotFoundException;
import com.study.hancom.sharephototest.exception.InvalidElementNumException;
import com.study.hancom.sharephototest.exception.StyleFileNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static com.study.hancom.sharephototest.model.Album.MAX_ELEMENT_OF_PAGE_NUM;

public class Page implements Parcelable {
    static final private PageLayoutFactory pageLayoutFactory = new PageLayoutFactory();

    private PageLayout mLayout;
    private List<Picture> mPictureList = new ArrayList<>();

    public Page(int elementNum) throws InvalidElementNumException, FrameFileNotFoundException, StyleFileNotFoundException {
        if (MAX_ELEMENT_OF_PAGE_NUM < elementNum || elementNum < 1) {
            throw new InvalidElementNumException();
        }

        mLayout = pageLayoutFactory.getPageLayout(elementNum);
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

    public void setLayout(int elementNum) throws FrameFileNotFoundException, StyleFileNotFoundException {
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
        if (position < getPictureCount()) {
            mPictureList.add(position, picture);
        } else {
            mPictureList.add(picture);
        }
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
