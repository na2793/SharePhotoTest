package com.study.hancom.sharephototest.model;

public interface AlbumDataChangeInterface {
    void addPage(Page page);

    void addPage(int index, Page page);

    void removePage(int index);

    void reorderPage(int fromIndex, int toIndex);

    void addPicture(int index, Picture picture);

    void addPicture(int index, int position, Picture picture);

    void removePicture(int index, int position, boolean nullable);

    void reorderPicture(int fromIndex, int fromPosition, int toIndex, int toPosition) throws Exception;
}