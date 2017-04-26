package com.study.hancom.sharephototest.model;

import android.util.Log;

import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.util.MathUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AlbumManager {
    private AlbumManager() {
    }

    public static Album createAlbum(List<Picture> pictureList) throws LayoutNotFoundException {
        List<Integer> usableElementNumList = new ArrayList<>(Page.getAllLayoutType());
        List<Integer> composedElementNumList = MathUtil.getRandomNumberList(usableElementNumList, pictureList.size());

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

    public static List<Integer> relayoutAlbum(Album album, List<Integer> pinnedPositionList) throws LayoutNotFoundException {
        List<Picture> pictureList = new ArrayList<>();
        List<Page> pinnedPageList = null;
        List<Integer> sortedPinnedPositionList = null;

        /* 고정 페이지 추출 */
        if (pinnedPositionList != null) {
            pinnedPageList = new ArrayList<>();
            sortedPinnedPositionList = new ArrayList<>(pinnedPositionList);
            Collections.sort(sortedPinnedPositionList);   // sort
            int offset = 0;
            for (int eachPinnedPosition : sortedPinnedPositionList) {
                pinnedPageList.add(album.removePage(eachPinnedPosition - offset));
                offset++;
            }
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
        List<Integer> usableElementNumList = new ArrayList<>(Page.getAllLayoutType());
        List<Integer> composedElementNumList = MathUtil.getRandomNumberList(usableElementNumList, pictureList.size());

        Collections.shuffle(pictureList);   // 사진도 랜덤으로

        for (int eachElementNum : composedElementNumList) {
            Page newPage = new Page(eachElementNum);
            album.addPage(newPage);
            for (int i = 0; i < eachElementNum; i++) {
                newPage.addPicture(pictureList.remove(0));
            }
        }

        if (sortedPinnedPositionList != null) {
            int pinnedPositionCount = sortedPinnedPositionList.size();
            for (int i = 0; i < pinnedPositionCount; i++) {
                Integer eachPosition = sortedPinnedPositionList.get(i);
                int pageCount = album.getPageCount();
                if (eachPosition > pageCount) {
                    sortedPinnedPositionList.set(i, pageCount);
                    album.addPage(pinnedPageList.remove(0));
                } else {
                    album.addPage(eachPosition, pinnedPageList.remove(0));
                }
            }
        }

        return sortedPinnedPositionList;
    }

    public static void addPage(Album album, int elementNum) throws LayoutNotFoundException {
        album.addPage(new Page(elementNum));
    }

    public static void addPage(Album album, int index, int elementNum) throws LayoutNotFoundException {
        album.addPage(index, new Page(elementNum));
    }

    public static void removePage(Album album, int section) throws LayoutNotFoundException {
        album.removePage(section);
        if (album.getPageCount() < 1) {
            Page page = new Page(1);
            page.addPicture(null);
            album.addPage(page);
        }
    }

    public static void setPicture(Album album, int section, int position, Picture picture) {
        Page page = album.getPage(section);
        page.removePicture(position);
        page.addPicture(position, picture);
    }

    public static void addPicture(Album album, int section, Picture picture) {
        Page page = album.getPage(section);
        page.addPicture(picture);
    }

    public static void addPicture(Album album, int section, int position, Picture picture) {
        Page page = album.getPage(section);
        page.addPicture(position, picture);
    }

    public static Picture removePicture(Album album, int section, int position, boolean nullable) throws LayoutNotFoundException {
        Picture oldPicture = null;

        if (nullable) {
            oldPicture = album.getPage(section).removePicture(position);
            album.getPage(section).addPicture(position, null);
        } else {
            Page page = album.getPage(section);
            int pictureCount = page.getPictureCount();
            oldPicture = page.removePicture(position);
            if (pictureCount > 1) {
                page.setLayout(pictureCount - 1);
            } else {
                removePage(album, section);
            }
        }

        return oldPicture;
    }

    public static void removeMultiplePicture(Album album, Map<Integer, List<Integer>> positionMap, boolean nullable) throws LayoutNotFoundException {
        List<Integer> sectionList = new ArrayList<>(positionMap.keySet());

        if (nullable) {
            for (int eachSection : sectionList) {
                List<Integer> eachPositionList = positionMap.get(eachSection);
                for (int eachPosition : eachPositionList) {
                    removePicture(album, eachSection, eachPosition, true);
                }
            }
        } else {
            List<PageLayout> pageLayoutBackupList = new ArrayList<>();
            try {
                for (int eachSection : sectionList) {
                    Page eachPage = album.getPage(eachSection);
                    pageLayoutBackupList.add(eachPage.getLayout());
                    int eachPictureCount = eachPage.getPictureCount();
                    List<Integer> eachPositionList = positionMap.get(eachSection);
                    int resultCount = eachPictureCount - eachPositionList.size();
                    if (resultCount > 0) {
                        eachPage.setLayout(resultCount);
                    } else {
                        eachPage.setLayout(1);
                    }
                }
            } catch (LayoutNotFoundException e) {
                int i = 0;
                for (PageLayout eachPageLayout : pageLayoutBackupList) {
                    album.getPage(sectionList.get(i++)).setLayout(eachPageLayout);
                }
                throw e;
            }

            for (int eachSection : sectionList) {
                Page eachPage = album.getPage(eachSection);
                List<Integer> eachPositionList = positionMap.get(eachSection);
                Collections.sort(eachPositionList);
                int eachPositionCount = eachPositionList.size();
                for (int i = eachPositionCount - 1; i >= 0; i--) {
                    int eachPosition = eachPositionList.get(i);
                    eachPage.removePicture(eachPosition);
                }
                if (eachPage.getPictureCount() < 1) {
                    eachPage.addPicture(null);
                }
            }
        }
    }

    public static void reorderPicture(Album album, int fromSection, int fromPosition, int toSection, int toPosition) throws LayoutNotFoundException {
        boolean isAdded = false;
        Page toPage = album.getPage(toSection);
        if (fromSection != toSection) {
            PageLayout toPageLayoutBackup = toPage.getLayout();
            toPage.setLayout(toPage.getPictureCount() + 1);

            Page fromPage = album.getPage(fromSection);
            int fromPictureCount = fromPage.getPictureCount();
            Picture target;
            try {
                if (fromPictureCount > 1) {
                    fromPage.setLayout(fromPictureCount - 1);
                    target = fromPage.removePicture(fromPosition);
                } else {
                    fromPage.setLayout(1);
                    target = fromPage.removePicture(fromPosition);
                    fromPage.addPicture(null);
                }
            } catch (LayoutNotFoundException e) {
                toPage.setLayout(toPageLayoutBackup);
                throw e;
            }

            toPage.addPicture(toPosition, target);
        } else {
            toPage.reorderPicture(fromPosition, toPosition);
        }
    }

    public static void reorderMultiplePicture(Album album, Map<Integer, List<Integer>> fromPositionMap, int toSection) throws LayoutNotFoundException {
        List<Integer> fromSectionList = new ArrayList<>(fromPositionMap.keySet());
        Collections.sort(fromSectionList);

        int targetCount = 0;
        for (int eachSection : fromSectionList) {
            if (eachSection != toSection) {
                int eachPositionCount = fromPositionMap.get(eachSection).size();
                targetCount += eachPositionCount;
            }
        }

        Page toPage = album.getPage(toSection);
        PageLayout toPageLayoutBackup = toPage.getLayout();
        toPage.setLayout(toPage.getPictureCount() + targetCount);

        List<PageLayout> fromPageLayoutBackupList = new ArrayList<>();
        try {
            for (int eachSection : fromSectionList) {
                Page eachFromPage = album.getPage(eachSection);
                fromPageLayoutBackupList.add(eachFromPage.getLayout());
                if (eachSection != toSection) {
                    int eachPictureCount = eachFromPage.getPictureCount();
                    List<Integer> eachPositionList = fromPositionMap.get(eachSection);
                    int resultCount = eachPictureCount - eachPositionList.size();
                    if (resultCount > 0) {
                        eachFromPage.setLayout(resultCount);
                    } else {
                        eachFromPage.setLayout(1);
                    }
                }
            }
        } catch (LayoutNotFoundException e) {
            toPage.setLayout(toPageLayoutBackup);
            int i = 0;
            for (PageLayout eachPageLayout : fromPageLayoutBackupList) {
                album.getPage(fromSectionList.get(i++)).setLayout(eachPageLayout);
            }
            throw e;
        }

        List<Picture> targetList = new ArrayList<>();
        for (int eachSection : fromSectionList) {
            Page eachFromPage = album.getPage(eachSection);
            List<Integer> eachPositionList = fromPositionMap.get(eachSection);
            Collections.sort(eachPositionList);
            int eachPositionCount = eachPositionList.size();
            for (int i = eachPositionCount - 1; i >= 0; i--) {
                int eachPosition = eachPositionList.get(i);
                targetList.add(eachFromPage.removePicture(eachPosition));
            }
            if (eachSection != toSection) {
                if (eachFromPage.getPictureCount() < 1) {
                    eachFromPage.addPicture(null);
                }
            }
        }

        for (Picture eachTarget : targetList) {
            toPage.addPicture(eachTarget);
        }
    }

    public static void setLayout(Album album, int section, PageLayout pageLayout) {
        Page page = album.getPage(section);
        page.setLayout(pageLayout);
    }
}