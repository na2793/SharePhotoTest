package com.study.hancom.sharephototest.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.study.hancom.sharephototest.exception.FrameFileNotFoundException;
import com.study.hancom.sharephototest.exception.InvalidElementNumException;
import com.study.hancom.sharephototest.exception.StyleFileNotFoundException;
import com.study.hancom.sharephototest.exception.TooManyFailedAttemptsException;
import com.study.hancom.sharephototest.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class Album implements Parcelable, Cloneable {

    public static final int MAX_ELEMENT_OF_PAGE_NUM = 4;

    private String mName;
    private List<Page> mPageList = new ArrayList<>();

    public Album() {
        this("tempAlbumName");
    }

    public Album(String name) {
        mName = name;
    }

    public Album(List<Picture> pictureList) throws InvalidElementNumException, TooManyFailedAttemptsException, FrameFileNotFoundException, StyleFileNotFoundException {
        this("tempAlbumName", pictureList);
    }

    public Album(String name, List<Picture> pictureList) throws InvalidElementNumException, TooManyFailedAttemptsException, FrameFileNotFoundException, StyleFileNotFoundException {
        mName = name;
        addPages(pictureList);
    }

    private Album(Parcel in) {
        mName = in.readString();
        mPageList = in.createTypedArrayList(Page.CREATOR);
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

    public void addPages(List<Picture> pictureList) throws TooManyFailedAttemptsException, InvalidElementNumException, FrameFileNotFoundException, StyleFileNotFoundException {
        /* 앨범 구성 */
        int pictureNum = pictureList.size();
        int usedElementCount = 0;
        int errorCount = 0;

        while (usedElementCount < pictureNum) {
            int eachElementNum = MathUtil.getRandomMath(MAX_ELEMENT_OF_PAGE_NUM, 1);
            if (pictureNum > usedElementCount + eachElementNum) {
                Page eachPage = new Page(eachElementNum);
                for (int i = 0; i < eachElementNum; i++) {
                    eachPage.addPicture(pictureList.get(usedElementCount + i));
                }
                addPage(eachPage);
                usedElementCount += eachElementNum;
            } else {
                int remainder = pictureNum - usedElementCount;

                Page eachPage = new Page(remainder);
                for (int i = 0; i < remainder; i++) {
                    eachPage.addPicture(pictureList.get(usedElementCount + i));
                }
                addPage(eachPage);
                break;
            }
            if (errorCount > 10) {
                throw new TooManyFailedAttemptsException();
            }
        }
    }

    public void addPage(Page page) {
        addPage(mPageList.size(), page);
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
        dest.writeTypedList(mPageList);
    }

    public Album clone() throws CloneNotSupportedException {
        return (Album) super.clone();
    }
}
