package com.study.hancom.sharephototest.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class EpubMaker {
    private final static String HEAD = "</head>";
    private final static String OEBPS_PACKAGE_MANIFEST = "</manifest>";
    private final static String OEBPS_PACKAGE_METADATA = "</metadata>";
    private final static String OEBPS_PACKAGE_SPINE = "</spine>";
    private final static String OEBPS_TOC_NAVMAP = "</navMap>";
    private final static String OEBPS_TOC_DOCTITLE = "</docTitle>";
    private final static String OEBPS_XHTML_NAV_TITLE = "<ol>";
    private final static String OEBPS_XHTML_NAV_ITEM_LIST = "</ol>";
    private final static String XHTML_HTML_DIV_FOR_IMAGE = "</div>";

    private Album mAlbum;
    private Context mContext;

    public EpubMaker(Album album, Context context) {
        mAlbum = album;
        mContext = context;
    }

    public void createFile(String fileName) {
        File dir;
        File file;

        InputStream inputStream;
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager assetManager = mContext.getAssets();

        int pageCount = mAlbum.getPageCount();

         /* 경로 및 파일 생성*/
        String saveEpubPath = Environment.getExternalStorageDirectory() + mContext.getResources().getString(R.string.epubData_path_SharePhoto);
        String filePath = saveEpubPath + String.format(mContext.getResources().getString(R.string.epubData_path_epub), fileName);
        dir = FileUtil.createDirectory(filePath);

        /* mineType file 생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_mineType));
            file = FileUtil.createFile(dir, (filePath + mContext.getResources().getString(R.string.epubData_fileName_mineType)));
            FileUtil.writeFile(file, FileUtil.fileToString(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* META-INF 경로 및 파일 생성*/
        String metaInfoPath = filePath + mContext.getResources().getString(R.string.epubData_path_metaInfo);
        dir = FileUtil.createDirectory(metaInfoPath);

        /* container.xml 생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_container));
            file = FileUtil.createFile(dir, (metaInfoPath + mContext.getResources().getString(R.string.epubData_fileName_container)));
            FileUtil.writeFile(file, FileUtil.fileToString(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* 고정 레이아웃임을 알 수 있는 파일생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_fixed_layout));
            file = FileUtil.createFile(dir, (metaInfoPath + mContext.getResources().getString(R.string.epubData_fileName_fixed_layout)));
            FileUtil.writeFile(file, FileUtil.fileToString(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

         /* xhtml 폴더 생성 */
        String xhtmlPath = filePath + mContext.getResources().getString(R.string.epubData_path_xhtml);
        dir = FileUtil.createDirectory(xhtmlPath);

        /* nav.xhtml 파일 생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_nav));
            file = FileUtil.createFile(dir, (xhtmlPath + mContext.getResources().getString(R.string.epubData_fileName_nav)));
            stringBuilder.setLength(0);
            stringBuilder.append(FileUtil.fileToString(inputStream));
            stringBuilder.insert(stringBuilder.indexOf(HEAD), String.format(mContext.getResources().getString(R.string.epubData_xhtml_nav_head_title), "Test"));
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_XHTML_NAV_TITLE), String.format(mContext.getResources().getString(R.string.epubData_xhtml_nav_body_title), "Test"));
            for (int i = 0; i < pageCount; i++) {
                stringBuilder.insert(stringBuilder.indexOf(OEBPS_XHTML_NAV_ITEM_LIST), String.format(mContext.getResources().getString(R.string.epubData_xhtml_nav_item), i + 1));
            }
            stringBuilder.append(System.getProperty("line.separator"));
            FileUtil.writeFile(file, stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* image 폴더 생성 */
        String imagePath = filePath + mContext.getResources().getString(R.string.epubData_path_image);

        /* 이미지 저장 */
        int count = 0;
        for (int i = 0; i < pageCount; i++) {
            FileUtil.createDirectory(imagePath + (i + 1)); //복사할 폴더
            for (int j = 0; j < mAlbum.getPage(i).getPictureCount(); j++) {
                count++;

                String originImagePath = mAlbum.getPage(i).getPicture(j).getPath();
                int lastIndex = originImagePath.lastIndexOf("/");
                String realFileName = originImagePath.substring(lastIndex, originImagePath.length());
                String realFilePath = originImagePath.substring(0, lastIndex);
                dir = FileUtil.createDirectory(realFilePath);

                int extensionIndex = realFileName.lastIndexOf(".");
                String ImageName = String.valueOf(count) + realFileName.substring(extensionIndex, realFileName.length());
                file = FileUtil.createFile(dir, (realFilePath + realFileName));
                FileUtil.copyFile(file, (imagePath + "/" + (i + 1) + "/" + ImageName));
            }
            count = 0;
        }

        /* 페이지 별로 html 생성*/
        // TODO :  페이지 스타일이 동일한 경우 createDirectory()를 자주하게 됨. 확인바람
        String cssPath = filePath + mContext.getResources().getString(R.string.epubData_path_css);
        FileUtil.createDirectory(cssPath);

        for (int i = 0; i < pageCount; i++) {
            stringBuilder.setLength(0);

            String layoutPath = mAlbum.getPage(i).getLayout().getPath();
            File originalLayoutFile = FileUtil.createDirectory(layoutPath);

            FileUtil.createFile(originalLayoutFile, originalLayoutFile.getPath());
            FileUtil.copyFile(originalLayoutFile, cssPath + mAlbum.getPage(i).getLayout().getElementNum() + mContext.getResources().getString(R.string.epubData_extension_css));

            try {
                inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_default_html));
                stringBuilder.append(FileUtil.fileToString(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringBuilder.insert(stringBuilder.indexOf(HEAD), String.format(mContext.getResources().getString(R.string.epubData_xhtml_html_css), mAlbum.getPage(i).getLayout().getElementNum()));

            Page page = mAlbum.getPage(i);
            int pictureCount = mAlbum.getPage(i).getPictureCount();

            for (int j = 0; j < pictureCount; j++) {
                String picturePath = page.getPicture(j).getPath();
                int extensionIndex = picturePath.lastIndexOf(".");
                stringBuilder.insert(stringBuilder.lastIndexOf(XHTML_HTML_DIV_FOR_IMAGE),
                        String.format(mContext.getResources().getString(R.string.epubData_xhtml_html_image), j + 1, i + 1, (j + 1) + picturePath.substring(extensionIndex, picturePath.length())));
            }
            file = FileUtil.createFile(dir, xhtmlPath + String.format(mContext.getResources().getString(R.string.epubData_xhtml_html_name), i + 1));
            FileUtil.writeFile(file, stringBuilder.toString());
        }

        /* OEBPS 폴더 생성 */
        String oebpsPath = filePath + mContext.getResources().getString(R.string.epubData_path_oebps);
        dir = FileUtil.createDirectory(oebpsPath);

        /* package.opf 생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_package));
            file = FileUtil.createFile(dir, (oebpsPath + mContext.getResources().getString(R.string.epubData_fileName_package)));
            stringBuilder.setLength(0);
            stringBuilder.append(FileUtil.fileToString(inputStream));
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_MANIFEST), mContext.getResources().getString(R.string.epubData_OEBPS_package_manifest_nav));
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_MANIFEST), mContext.getResources().getString(R.string.epubData_OEBPS_package_manifest_ncx));
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_METADATA), mContext.getResources().getString(R.string.epubData_OEBPS_package_metaData));
            for (int i = 0; i < pageCount; i++) {
                stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_MANIFEST), String.format(mContext.getResources().getString(R.string.epubData_OEBPS_package_manifest_html), i + 1));
                stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_SPINE), String.format(mContext.getResources().getString(R.string.epubData_OEBPS_package_spine), i + 1));
            }
            dir = FileUtil.createDirectory(cssPath);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File eachFile : files) {
                    String cssFileName = eachFile.getName();
                    int index = cssFileName.indexOf(".");
                    stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_MANIFEST), String.format(mContext.getResources().getString(R.string.epubData_OEBPS_package_manifest_css),
                            cssFileName.substring(0, index), cssFileName));
                }
            }
            stringBuilder.append(System.getProperty("line.separator"));
            FileUtil.writeFile(file, stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* toc.ncx 파일 생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_toc));
            file = FileUtil.createFile(dir, (oebpsPath + mContext.getResources().getString(R.string.epubData_fileName_toc)));
            stringBuilder.setLength(0);
            stringBuilder.append(FileUtil.fileToString(inputStream));
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_TOC_DOCTITLE), String.format(mContext.getResources().getString(R.string.epubData_OEPBS_toc_doc_title), "Test"));
            stringBuilder.insert(stringBuilder.indexOf(HEAD), mContext.getResources().getString(R.string.epubData_OEPBS_toc_meta));
            for (int i = 0; i < pageCount; i++) {
                stringBuilder.insert(stringBuilder.indexOf(OEBPS_TOC_NAVMAP), String.format(mContext.getResources().getString(R.string.epubData_OEBPS_toc_navMap), i + 1));
            }
            FileUtil.writeFile(file, stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Epub 문서포맷으로 저장 및 기존 폴더 삭제 */
        try {
            FileUtil.zipFolder(filePath, saveEpubPath +
                    fileName + mContext.getResources().getString(R.string.epubData_extension_epub));
            FileUtil.deleteFolder(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}