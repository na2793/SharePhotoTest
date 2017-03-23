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
    private FileUtil fileUtil = new FileUtil();

    public EpubMaker(Album album, Context context) {
        mAlbum = album;
        mContext = context;
    }

    public void saveEpub(String fileName) {

        File dir;
        File file;

        InputStream inputStream;
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager assetManager = mContext.getAssets();

        int pageCount = mAlbum.getPageCount();

         /* 경로 및 파일 생성*/
        String saveEpubPath = Environment.getExternalStorageDirectory() + mContext.getResources().getString(R.string.epubData_path_SharePhoto);
        String filePath = saveEpubPath + String.format(mContext.getResources().getString(R.string.epubData_path_epub), fileName);
        dir = fileUtil.makeDirectory(filePath);

        /* mineType file 생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_mineType));
            file = fileUtil.createFile(dir, (filePath + mContext.getResources().getString(R.string.epubData_fileName_mineType)));
            fileUtil.writeFile(file, fileUtil.copyAssetFile(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* META-INF 경로 및 파일 생성*/
        String metaInfoPath = filePath + mContext.getResources().getString(R.string.epubData_path_metaInfo);
        dir = fileUtil.makeDirectory(metaInfoPath);

        /* container.xml 생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_container));
            file = fileUtil.createFile(dir, (metaInfoPath + mContext.getResources().getString(R.string.epubData_fileName_container)));
            fileUtil.writeFile(file, fileUtil.copyAssetFile(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* 고정 레이아웃임을 알 수 있는 파일생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_fixed_layout));
            file = fileUtil.createFile(dir, (metaInfoPath + mContext.getResources().getString(R.string.epubData_fileName_fixed_layout)));
            fileUtil.writeFile(file, fileUtil.copyAssetFile(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

         /* xhtml 폴더 생성 */
        String xhtmlPath = filePath + mContext.getResources().getString(R.string.epubData_path_xhtml);
        dir = fileUtil.makeDirectory(xhtmlPath);

        /* nav.xhtml 파일 생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_nav));
            file = fileUtil.createFile(dir, (xhtmlPath + mContext.getResources().getString(R.string.epubData_fileName_nav)));
            stringBuilder.setLength(0);
            stringBuilder.append(fileUtil.copyAssetFile(inputStream));
            stringBuilder.insert(stringBuilder.indexOf(HEAD), String.format(mContext.getResources().getString(R.string.epubData_xhtml_nav_head_title), "Test"));
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_XHTML_NAV_TITLE), String.format(mContext.getResources().getString(R.string.epubData_xhtml_nav_body_title), "Test"));
            for (int i = 0; i < pageCount; i++) {
                stringBuilder.insert(stringBuilder.indexOf(OEBPS_XHTML_NAV_ITEM_LIST), String.format(mContext.getResources().getString(R.string.epubData_xhtml_nav_item), i + 1));
            }
            stringBuilder.append(System.getProperty("line.separator"));
            fileUtil.writeFile(file, stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* image 폴더 생성 */
        String imagePath = filePath + mContext.getResources().getString(R.string.epubData_path_image);

        /* 이미지 저장 */
        int count = 0;
        for (int i = 0; i < pageCount; i++) {
            fileUtil.makeDirectory(imagePath + (i + 1)); //복사할 폴더
            for (int j = 0; j < mAlbum.getPage(i).getPictureCount(); j++) {
                count++;

                String originImagePath = mAlbum.getPage(i).getPicture(j).getPath();
                int lastIndex = originImagePath.lastIndexOf("/");
                String realFileName = originImagePath.substring(lastIndex, originImagePath.length());
                String realFilePath = originImagePath.substring(0, lastIndex);
                dir = fileUtil.makeDirectory(realFilePath);

                int extensionIndex = realFileName.lastIndexOf(".");
                String ImageName = String.valueOf(count) + realFileName.substring(extensionIndex, realFileName.length());
                file = fileUtil.createFile(dir, (realFilePath + realFileName));
                fileUtil.copyFile(file, (imagePath + "/" + (i + 1) + "/" + ImageName));
            }
            count = 0;
        }

        /* 페이지 별로 html 생성*/
        // TODO :  페이지 스타일이 동일한 경우 makeDirectory()를 자주하게 됨. 확인바람
        String cssPath = filePath + mContext.getResources().getString(R.string.epubData_path_css);
        fileUtil.makeDirectory(cssPath);

        for (int i = 0; i < pageCount; i++) {
            stringBuilder.setLength(0);

            String layoutPath = mAlbum.getPage(i).getLayout().getPath();
            File originalLayoutFile = fileUtil.makeDirectory(layoutPath);

            fileUtil.createFile(originalLayoutFile, originalLayoutFile.getPath());
            fileUtil.copyFile(originalLayoutFile, cssPath + mAlbum.getPage(i).getLayout().getElementNum() + mContext.getResources().getString(R.string.epubData_extension_css));

            try {
                inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_default_html));
                stringBuilder.append(fileUtil.copyAssetFile(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringBuilder.insert(stringBuilder.indexOf(HEAD), String.format(mContext.getResources().getString(R.string.epubData_xhtml_html_css), mAlbum.getPage(i).getLayout().getElementNum()));

            Page page = mAlbum.getPage(i);
            int pictureCount = mAlbum.getPage(i).getPictureCount();

            for (int j = 0; j < pictureCount ; j++) {
                String picturePath = page.getPicture(j).getPath();
                int extensionIndex =picturePath.lastIndexOf(".");
                stringBuilder.insert(stringBuilder.lastIndexOf(XHTML_HTML_DIV_FOR_IMAGE),
                        String.format(mContext.getResources().getString(R.string.epubData_xhtml_html_image), j + 1, i + 1, (j + 1) + picturePath.substring(extensionIndex, picturePath.length())));
            }
            file = fileUtil.createFile(dir, xhtmlPath + String.format(mContext.getResources().getString(R.string.epubData_xhtml_html_name), i + 1));
            fileUtil.writeFile(file, stringBuilder.toString());
        }

        /* OEBPS 폴더 생성 */
        String oebpsPath = filePath + mContext.getResources().getString(R.string.epubData_path_oebps);
        dir = fileUtil.makeDirectory(oebpsPath);

        /* package.opf 생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_package));
            file = fileUtil.createFile(dir, (oebpsPath + mContext.getResources().getString(R.string.epubData_fileName_package)));
            stringBuilder.setLength(0);
            stringBuilder.append(fileUtil.copyAssetFile(inputStream));
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_MANIFEST), mContext.getResources().getString(R.string.epubData_OEBPS_package_manifest_nav));
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_MANIFEST), mContext.getResources().getString(R.string.epubData_OEBPS_package_manifest_ncx));
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_METADATA), mContext.getResources().getString(R.string.epubData_OEBPS_package_metaData));
            for (int i = 0; i < pageCount; i++) {
                stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_MANIFEST), String.format(mContext.getResources().getString(R.string.epubData_OEBPS_package_manifest_html), i + 1));
                stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_SPINE), String.format(mContext.getResources().getString(R.string.epubData_OEBPS_package_spine), i + 1));
            }
            dir = fileUtil.makeDirectory(cssPath);
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
            fileUtil.writeFile(file, stringBuilder.toString());
        }catch (IOException e){
            e.printStackTrace();
        }

        /* toc.ncx 파일 생성 */
        try {
            inputStream = assetManager.open(mContext.getResources().getString(R.string.epubData_fileName_toc));
            file = fileUtil.createFile(dir, (oebpsPath + mContext.getResources().getString(R.string.epubData_fileName_toc)));
            stringBuilder.setLength(0);
            stringBuilder.append(fileUtil.copyAssetFile(inputStream));
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_TOC_DOCTITLE), String.format(mContext.getResources().getString(R.string.epubData_OEPBS_toc_doc_title), "Test"));
            stringBuilder.insert(stringBuilder.indexOf(HEAD), mContext.getResources().getString(R.string.epubData_OEPBS_toc_meta));
            for (int i = 0; i < pageCount; i++) {
                stringBuilder.insert(stringBuilder.indexOf(OEBPS_TOC_NAVMAP), String.format(mContext.getResources().getString(R.string.epubData_OEBPS_toc_navMap), i + 1));
            }
            fileUtil.writeFile(file, stringBuilder.toString());
        }catch (IOException e){
            e.printStackTrace();
        }

        /* Epub 문서포맷으로 저장 및 기존 폴더 삭제 */
        try {
            ZipUtil.zipFolder(filePath, saveEpubPath +
                    fileName + mContext.getResources().getString(R.string.epubData_extension_epub));
            fileUtil.deleteFolder(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}