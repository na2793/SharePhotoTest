package com.study.hancom.sharephototest.model;

import android.util.Log;

import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.util.MathUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlbumAction {

    public Album createAlbum(List<Picture> pictureList) throws LayoutNotFoundException {
        MathUtil mathUtil = new MathUtil();

        List<Integer> usableElementNumList = new ArrayList<>(Page.getAllLayoutType());
        List<Integer> composedElementNumList = mathUtil.getRandomNumberList(usableElementNumList, pictureList.size());

        Album album = new Album();

        for (int eachElementNum : composedElementNumList) {
            Page newPage = new Page(eachElementNum);
            album.addPage(newPage);
            for (int i = 0; i < eachElementNum; i++) {
                newPage.addPicture(pictureList.remove(0));
            }
        }

        return album;
    }

    public void relayoutAlbum(Album album, List<Integer> pinnedPositionList) throws LayoutNotFoundException {
        List<Picture> pictureList = new ArrayList<>();
        List<Page> pinnedPageList = new ArrayList<>();

        /* 고정 페이지 추출 */
        Integer[] sortedPinnedPositionArray = pinnedPositionList.toArray(new Integer[pinnedPositionList.size()]);
        Arrays.sort(sortedPinnedPositionArray);

        int offset = 0;
        for (int eachPinnedPosition : sortedPinnedPositionArray) {
            pinnedPageList.add(album.removePage(eachPinnedPosition - offset));
            offset++;
        }

        /* 모든 사진 추출 및 페이지 삭제 */
        int oldPageCount = album.getPageCount();
        for (int i = 0; i < oldPageCount; i++) {
            Page eachPage = album.removePage(0);
            for (int j = 0; j < eachPage.getPictureCount(); j++) {
                Picture eachPicture = eachPage.getPicture(j);
                if (eachPicture != null) {
                    pictureList.add(eachPicture);
                }
            }
        }

        /* 새롭게 적재 */
        MathUtil mathUtil = new MathUtil();

        List<Integer> usableElementNumList = new ArrayList<>(Page.getAllLayoutType());
        List<Integer> composedElementNumList = mathUtil.getRandomNumberList(usableElementNumList, pictureList.size());

        Collections.shuffle(pictureList);   // 사진도 랜덤으로

        for (int eachElementNum : composedElementNumList) {
            Page newPage = new Page(eachElementNum);
            album.addPage(newPage);
            for (int i = 0; i < eachElementNum; i++) {
                newPage.addPicture(pictureList.remove(0));
            }
        }

        for (int eachPinnedPosition : sortedPinnedPositionArray) {
            int pageCount = album.getPageCount();
            if (eachPinnedPosition < pageCount) {
                album.addPage(eachPinnedPosition, pinnedPageList.remove(0));
            } else {
                pinnedPositionList.remove(eachPinnedPosition);
                pinnedPositionList.add(album.getPageCount());
                album.addPage(pinnedPageList.remove(0));
            }
        }
    }

    public void removePage(Album album, int section) throws LayoutNotFoundException {
        album.removePage(section);
        if (album.getPageCount() < 1) {
            Page page = new Page(1);
            page.addPicture(null);
            album.addPage(page);
        }
    }

    public void setPicture(Album album, int section, int position, Picture picture) {
        Page page = album.getPage(section);
        page.removePicture(position);
        page.addPicture(position, picture);
    }

    public void removePicture(Album album, int section, int position, boolean nullable) throws LayoutNotFoundException {
        if (nullable) {
            album.getPage(section).removePicture(position);
            album.getPage(section).addPicture(position, null);
        } else {
            Page page = album.getPage(section);
            int pictureCount = page.getPictureCount();
            if (pictureCount > 1) {
                page.setLayout(pictureCount - 1);
                page.removePicture(position);
            } else {
                removePage(album, section);
            }
        }
    }

    public void removeMultiplePicture(Album album, int section, List<Integer> positionArray) throws LayoutNotFoundException {
        Page page = album.getPage(section);
        int pictureCount = page.getPictureCount();
        int positionCount = positionArray.size();
        int result = pictureCount - positionCount;
        if (result > 0) {
            page.setLayout(result);
            for (int eachPosition : positionArray) {
                page.removePicture(eachPosition);
            }
        } else {
            removePage(album, section);
        }
    }

    public void reorderPicture(Album album, int fromSection, int fromPosition, int toSection, int toPosition) throws LayoutNotFoundException {
        Page toPage = album.getPage(toSection);
        PageLayout toPageLayoutBackup = toPage.getLayout();
        toPage.setLayout(toPage.getPictureCount() + 1);

        if (fromSection == toSection) {
            if (fromPosition != toPosition) {
                toPage.reorderPicture(fromPosition, toPosition);
            }
        } else {
            Page fromPage = album.getPage(fromSection);
            int fromPagePictureCount = fromPage.getPictureCount();
            if (fromPagePictureCount > 1) {
                try {
                    fromPage.setLayout(fromPagePictureCount - 1);
                } catch (LayoutNotFoundException e) {
                    toPage.setLayout(toPageLayoutBackup);
                    throw e;
                }
            } else {
                fromPage.addPicture(null);
            }

            Picture target = fromPage.removePicture(fromPosition);
            toPage.addPicture(toPosition, target);
        }
    }
}
