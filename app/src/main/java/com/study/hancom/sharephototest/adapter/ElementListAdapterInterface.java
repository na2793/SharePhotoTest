package com.study.hancom.sharephototest.adapter;

import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;

interface ElementListAdapterInterface {
    void addPage(Page page);
    void addPage(int index, Page page);
    Page removePage(int index);
    void reorderPage(int fromIndex, int toIndex);
    void addPicture(int index, Picture picture);
    void addPicture(int index, int position, Picture picture);
    Picture removePicture(int index, int position) throws Exception;
    Picture setPictureEmpty(int index, int position);
    void reorderPicture(int index, int fromPosition, int toPosition);
}