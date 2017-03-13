package com.study.hancom.sharephototest.model;

import android.os.Environment;

import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.util.MathUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class PageLayoutManager {

    private final String LAYOUT_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "SharePhoto" + File.separator + "layout" + File.separator;

    private Map<Integer, List<PageLayout>> mLayoutMap = new HashMap<>();    // <type, pageLayoutList>
                                                                            // type = 스타일 파일들을 분류하는 기준 (element count)
    private MathUtil mMathUtil = new MathUtil();

    private PageLayoutManager() {
        File layoutFolder = new File(LAYOUT_FOLDER_PATH);
        if (layoutFolder.exists()) {
            File[] typeFolderList = layoutFolder.listFiles();
            for (File eachTypeFolder : typeFolderList) {
                try {
                    int type = Integer.parseInt(eachTypeFolder.getName());
                    mLayoutMap.put(type, findPageLayoutList(type));
                } catch (NumberFormatException e) {
                    //**파싱 실패하면 넘어감 (잘못된 파일이라 판단)
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Singleton {
        private static final PageLayoutManager instance = new PageLayoutManager();
    }

    static PageLayoutManager getInstance() {
        return Singleton.instance;
    }

    private List<PageLayout> findPageLayoutList(int type) {
        List<PageLayout> pageLayoutList = new ArrayList<>();

        File typeFolder = new File(LAYOUT_FOLDER_PATH + type);
        File[] fileList = typeFolder.listFiles();
        for (File eachFile : fileList) {
            pageLayoutList.add(new PageLayout(type, eachFile.getAbsolutePath()));
        }

        return pageLayoutList;
    }

    Set<Integer> getAllType() {
        return mLayoutMap.keySet();
    }

    PageLayout getPageLayout(int type) throws LayoutNotFoundException {
        if (!mLayoutMap.containsKey(type)) {
            throw new LayoutNotFoundException();
        }

        List<PageLayout> pageLayoutList = mLayoutMap.get(type);
        return pageLayoutList.get(mMathUtil.getRandomMath(pageLayoutList.size() - 1, 0));
    }
}