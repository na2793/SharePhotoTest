package com.study.hancom.sharephototest.model;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private String mName;
    private List<Page> mPageList;

    public Album() {
        this("test");
    }

    public Album(String name) {
        mName = name;
        mPageList = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public Page getPage(int position) {
        return mPageList.get(position);
    }

    public void addPage(Page page) {
        addPage(mPageList.size(), page);
    }

    public void addPage(int position, Page page) {
        mPageList.add(position, page);
    }

    public Page removePage(int position) {
        return mPageList.remove(position);
    }

    public int getPageCount() {
        return mPageList.size();
    }

    public void reorderPage(int fromPosition, int toPosition) {
        Page tempPage = removePage(fromPosition);
        addPage(toPosition, tempPage);
    }
}
