package com.study.hancom.sharephototest.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Album implements Parcelable, Cloneable {

    private String mName;
    private List<Page> mPageList = new ArrayList<>();

    public Album() {
        this("tempAlbumName");
    }

    public Album(String name) {
        mName = name;
    }

    private Album(Parcel in) {
        mName = in.readString();
        int pictureListSize = in.readInt();
        for (int i = 0 ; i < pictureListSize ; i++) {
            mPageList.add((Page) in.readParcelable(Page.class.getClassLoader()));
        }
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public String getName() {
        return mName;
    }

    public Page getPage(int index) {
        return mPageList.get(index);
    }

    public void addPage(Page page) {
        mPageList.add(page);
    }

    public void addPage(int index, Page page) {
        mPageList.add(index, page);
    }

    public Page removePage(int index) {
        return mPageList.remove(index);
    }

    public int getPageCount() {
        return mPageList.size();
    }

    public void reorderPage(int fromIndex, int toIndex) {
        Page tempPage = removePage(fromIndex);
        addPage(toIndex, tempPage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mPageList.size());
        for (Page eachPage : mPageList) {
            dest.writeParcelable(eachPage, 0);
        }
    }

    public Album clone() throws CloneNotSupportedException {
        return (Album) super.clone();
    }
}
