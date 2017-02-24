package com.study.hancom.sharephototest.listener;

import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;

import java.util.HashSet;
import java.util.Set;

public class AlbumDataChangedListener implements AlbumDataChangeInterface {

    private Set<AlbumDataChangeInterface> mDataChangedListenerList = new HashSet<>();

    public void addDataChangedListener(AlbumDataChangeInterface context) {
        mDataChangedListenerList.add(context);
    }

    public void removeDataChangedListener(AlbumDataChangeInterface context) {
        mDataChangedListenerList.remove(context);
    }

    @Override
    public void onPageAdd(Page page) {
        for (AlbumDataChangeInterface eachDataChangedListener : mDataChangedListenerList) {
            eachDataChangedListener.onPageAdd(page);
        }
    }

    @Override
    public void onPageAdd(int index, Page page) {
        for (AlbumDataChangeInterface eachDataChangedListener : mDataChangedListenerList) {
            eachDataChangedListener.onPageAdd(index, page);
        }
    }

    @Override
    public void onPageRemove(int index) {
        for (AlbumDataChangeInterface eachDataChangedListener : mDataChangedListenerList) {
            eachDataChangedListener.onPageRemove(index);
        }
    }

    @Override
    public void onPageReorder(int fromIndex, int toIndex) {
        for (AlbumDataChangeInterface eachDataChangedListener : mDataChangedListenerList) {
            eachDataChangedListener.onPageReorder(fromIndex, toIndex);
        }
    }

    @Override
    public void onPictureAdd(int index, Picture picture) {
        for (AlbumDataChangeInterface eachDataChangedListener : mDataChangedListenerList) {
            eachDataChangedListener.onPictureAdd(index, picture);
        }
    }

    @Override
    public void onPictureAdd(int index, int position, Picture picture) {
        for (AlbumDataChangeInterface eachDataChangedListener : mDataChangedListenerList) {
            eachDataChangedListener.onPictureAdd(index, position, picture);
        }
    }

    @Override
    public void onPictureRemove(int index, int position, boolean nullable) {
        for (AlbumDataChangeInterface eachDataChangedListener : mDataChangedListenerList) {
            eachDataChangedListener.onPictureRemove(index, position, nullable);
        }
    }

    @Override
    public void onPictureReorder(int index, int fromPosition, int toPosition) {
        for (AlbumDataChangeInterface eachDataChangedListener : mDataChangedListenerList) {
            eachDataChangedListener.onPictureReorder(index, fromPosition, toPosition);
        }
    }
}
