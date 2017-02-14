package com.study.hancom.sharephototest.model;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.study.hancom.sharephototest.util.MathUtil.getRandomMath;

class PageLayoutFactory {

    private Map<Integer, List<PageLayout>> mLayoutMap;

    //** 임시 xml로 뺄 것
    final private String mLayoutFrameFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "SharePhoto" + File.separator + "layout" + File.separator + "frame" + File.separator;
    final private String mLayoutStyleFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "SharePhoto" + File.separator + "layout" + File.separator + "style" + File.separator;

    PageLayoutFactory() {
        mLayoutMap = new HashMap<>();
    }

    PageLayout getPageLayout(int elementNum) throws Exception {
        PageLayout pageLayout = null;
        List<PageLayout> pageLayoutList = mLayoutMap.get(elementNum);

        if (pageLayoutList == null) {
            pageLayoutList = findLayoutFile(elementNum);
            mLayoutMap.put(elementNum, pageLayoutList);
        }

        if (pageLayoutList.size() > 0) {
            pageLayout = pageLayoutList.get(getRandomMath(pageLayoutList.size() - 1, 0));
        }

        return pageLayout;
    }

    private List<PageLayout> findLayoutFile(int elementNum) throws Exception {
        List<PageLayout> pageLayoutList = new ArrayList<>();
        File frameFile = new File(mLayoutFrameFolderPath + elementNum + ".html");

        if (frameFile.exists()) {
            File styleFolder = new File(mLayoutStyleFolderPath + elementNum);
            File[] styleList = styleFolder.listFiles();
            int styleListLength = styleList.length;

            if (styleListLength > 0) {
                for (int i = 0; i < styleListLength; i++) {
                    File styleFile = styleList[i];
                    String pageLayoutData = injectStyleIntoFrame(frameFile, styleFile).toString();
                    pageLayoutList.add(new PageLayout(pageLayoutData, elementNum));
                }
            } else {
                //** exception style not found
                Log.v("tag", "style not found");
                throw new Exception();
            }
        } else {
            //** exception frame not found
            Log.v("tag", "frame not found");
            throw new Exception();
        }

        return pageLayoutList;
    }

    private StringBuffer injectStyleIntoFrame(File frameFile, File styleFile) throws IOException {
        StringBuffer content = null;

        try {
            content = readFile(frameFile);
            content.insert(content.indexOf("</head>"), "<link rel=\"stylesheet\" href=\"" + styleFile.getCanonicalPath() + "\">\n");
        } catch (IOException e) {
            throw e;
        }

        return content;
    }

    private StringBuffer readFile(File file) throws IOException {

        StringBuffer stringBuffer = new StringBuffer();
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            Reader reader = null;
            try {
                reader = new InputStreamReader(fileInputStream);
                int size = fileInputStream.available();
                char[] buffer = new char[size];
                reader.read(buffer);

                stringBuffer.append(buffer);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }

        return stringBuffer;
    }
}