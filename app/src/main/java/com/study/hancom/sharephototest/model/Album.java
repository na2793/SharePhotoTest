package com.study.hancom.sharephototest.model;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private String mName;
    private List<Page> mPageList;

    public Album() {
        this("tempAlbumName");
    }

    public Album(String name) {
        mName = name;
        mPageList = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public Page getPage(int index) {
        return mPageList.get(index);
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
}
