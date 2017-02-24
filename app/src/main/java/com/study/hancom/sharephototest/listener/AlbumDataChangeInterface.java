package com.study.hancom.sharephototest.listener;

import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;

public interface AlbumDataChangeInterface {
    void onPageAdd(Page page);
    void onPageAdd(int index, Page page);
    void onPageRemove(int index);
    void onPageReorder(int fromIndex, int toIndex);
    void onPictureAdd(int index, Picture picture);
    void onPictureAdd(int index, int position, Picture picture);
    void onPictureRemove(int index, int position, boolean nullable);
    void onPictureReorder(int index, int fromPosition, int toPosition);
}