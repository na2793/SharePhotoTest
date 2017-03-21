package com.study.hancom.sharephototest.util;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Album;

import java.io.File;
import java.util.UUID;

public class EpubMaker {

    private final static String OEBPS_PACKAGE_MANIFEST = "</manifest>";
    private final static String OEBPS_PACKAGE_SPINE = "</spine>";
    private final static String OEBPS_TOC_NAVMAP = "</navMap>";
    private final static String OEBPS_XHTML_NAV_ITEMLIST = "</ol>";
    private final static String XHTML_HTML_CSS = "</head>";
    private final static String XHTML_HTML_IMAGE = "</div>";

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

        StringBuilder stringBuilder = new StringBuilder();

         /* 경로 및 파일 생성*/
        String saveEpubPath = Environment.getExternalStorageDirectory() + mContext.getResources().getString(R.string.epubData_path_SharePhoto);
        String filePath = saveEpubPath + String.format(mContext.getResources().getString(R.string.epubData_path_epub), fileName);
        dir = fileUtil.makeDirectory(filePath);

        /* mineType file 생성 */
        file = fileUtil.createFile(dir, (filePath + mContext.getResources().getString(R.string.epubData_fileName_mineType)));
        fileUtil.writeFile(file, mContext.getResources().getString(R.string.epubData_mimeType));


        /* META-INF 경로 및 파일 생성*/
        String metaInfoPath = filePath + mContext.getResources().getString(R.string.epubData_path_metaInfo);
        dir = fileUtil.makeDirectory(metaInfoPath);

        /* container.xml 생성 */
        file = fileUtil.createFile(dir, (metaInfoPath + mContext.getResources().getString(R.string.epubData_fileName_container)));
        fileUtil.writeFile(file, mContext.getResources().getString(R.string.epupData_metaInfo_container));

        /* 고정 레이아웃임을 알 수 있는 파일생성 */
        file = fileUtil.createFile(dir, (metaInfoPath + mContext.getResources().getString(R.string.epubData_fileName_fixed_layout)));
        fileUtil.writeFile(file, mContext.getResources().getString(R.string.epubData_metaInfo_fixedLayout));

        /* OEBPS 폴더 생성 */
        String oebpsPath = filePath + mContext.getResources().getString(R.string.epubData_path_oebps);
        dir = fileUtil.makeDirectory(oebpsPath);

        /* package.opf 생성 */
        file = fileUtil.createFile(dir, (oebpsPath + mContext.getResources().getString(R.string.epubData_fileName_package)));
        stringBuilder.setLength(0);
        stringBuilder.append(String.format(mContext.getResources().getString(R.string.epubData_OEBPS_pakage), "임하림", "Test"));
        stringBuilder.append(System.getProperty("line.separator"));
        for (int i = 0; i < mAlbum.getPageCount(); i++) {
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_MANIFEST), String.format(mContext.getResources().getString(R.string.epubData_OEBPS_package_manifest), i + 1));
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_PACKAGE_SPINE), String.format(mContext.getResources().getString(R.string.epubData_OEBPS_package_spine), i + 1));
        }
        stringBuilder.append(System.getProperty("line.separator"));
        fileUtil.writeFile(file, stringBuilder.toString());

        /* toc.ncx 파일 생성 */
        file = fileUtil.createFile(dir, (oebpsPath + mContext.getResources().getString(R.string.epubData_fileName_toc)));
        stringBuilder.setLength(0);
        stringBuilder.append(String.format(mContext.getResources().getString(R.string.epubData_OEBPS_toc), "Test"));
        for (int i = 0; i < mAlbum.getPageCount(); i++) {
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_TOC_NAVMAP), String.format(mContext.getResources().getString(R.string.epubData_OEBPS_toc_navMap), i + 1));
        }
        fileUtil.writeFile(file, stringBuilder.toString());

        /* xhtml 폴더 생성 */
        String xhtmlPath = filePath + mContext.getResources().getString(R.string.epubData_path_xhtml);
        dir = fileUtil.makeDirectory(xhtmlPath);

        /* nav.xhtml 파일 생성 */
        file = fileUtil.createFile(dir, xhtmlPath + mContext.getResources().getString(R.string.epubData_fileName_nav));
        stringBuilder.setLength(0);
        stringBuilder.append(String.format(mContext.getResources().getString(R.string.epubData_xhtml_nav), "Test"));
        for (int i = 0; i < mAlbum.getPageCount(); i++) {
            stringBuilder.insert(stringBuilder.indexOf(OEBPS_XHTML_NAV_ITEMLIST), String.format(mContext.getResources().getString(R.string.epubData_xhtml_nav_item), i + 1));
        }
        stringBuilder.append(System.getProperty("line.separator"));
        fileUtil.writeFile(file, stringBuilder.toString());

         /* 이미지 저장 */
        String imagePath = filePath + mContext.getResources().getString(R.string.epubData_path_image);
        int count = 0;
        for (int i = 0; i < mAlbum.getPageCount(); i++) {
            fileUtil.makeDirectory(imagePath + (i + 1)); //복사할 폴더
            for (int j = 0; j < mAlbum.getPage(i).getPictureCount(); j++) {
                count++;

                String originImagePath = mAlbum.getPage(i).getPicture(j).getPath();
                int lastIndex = originImagePath.lastIndexOf("/");
                String realFileName = originImagePath.substring(lastIndex, originImagePath.length());
                String realFilePath = originImagePath.substring(0, lastIndex);
                dir = fileUtil.makeDirectory(realFilePath);

                String ImageName = String.valueOf(count) + mContext.getResources().getString(R.string.epubData_image_extension);
                file = fileUtil.createFile(dir, (realFilePath + realFileName));
                fileUtil.copyFile(file, (imagePath + "/" + (i + 1) + "/" + ImageName));
            }
            count = 0;
        }

        /* 페이지 별로 html 생성*/
        String cssPath = filePath + mContext.getResources().getString(R.string.epubData_path_css);
        for (int i = 0; i < mAlbum.getPageCount(); i++) {
            stringBuilder.setLength(0);
            String stylePath = mAlbum.getPage(i).getLayout().getPath();
            File originalStyleFile = fileUtil.makeDirectory(stylePath);
            fileUtil.makeDirectory(cssPath);
            fileUtil.createFile(originalStyleFile, originalStyleFile.getPath());
            fileUtil.copyFile(originalStyleFile, cssPath + mAlbum.getPage(i).getLayout().getElementNum() + mContext.getResources().getString(R.string.epubData_css_extension));

            stringBuilder.append(new WebViewUtil().getDefaultHTMLData());
            stringBuilder.insert(stringBuilder.indexOf(XHTML_HTML_CSS), String.format(mContext.getResources().getString(R.string.epubData_xhtml_html_css), mAlbum.getPage(i).getLayout().getElementNum()));
            for (int j = 0; j < mAlbum.getPage(i).getPictureCount(); j++) {
                stringBuilder.insert(stringBuilder.lastIndexOf(XHTML_HTML_IMAGE), String.format(mContext.getResources().getString(R.string.epubData_xhtml_html_image), j + 1, i + 1));
            }
            file = fileUtil.createFile(dir, xhtmlPath + String.format(mContext.getResources().getString(R.string.epubData_xhtml_html_name), i + 1));
            fileUtil.writeFile(file, stringBuilder.toString());

        }

        /* Epub 문서포맷으로 저장 및 기존 폴더 삭제 */
        try {
            ZipUtil.zipFolder(filePath, saveEpubPath +
                    fileName + mContext.getResources().getString(R.string.epubData_epub_extension));
            fileUtil.deleteFolder(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private String GetDevicesUUID(){
//        final TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
//        final String telephonyManagerDevice, telephonyManagerSerial, androidId;
//
//        telephonyManagerDevice = telephonyManager.getDeviceId();
//        telephonyManagerSerial = telephonyManager.getSimSerialNumber();
//        androidId = android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//        Log.v("tag", telephonyManagerDevice);
//        Log.v("tag", telephonyManagerSerial);
//        Log.v("tag", androidId);
//
//        UUID deviceUuid = new UUID(androidId.hashCode(),((long)telephonyManagerDevice.hashCode() << 32) | telephonyManagerSerial.hashCode());
//        String deviceId = deviceUuid.toString();
//
//        return deviceId;
//    }
}