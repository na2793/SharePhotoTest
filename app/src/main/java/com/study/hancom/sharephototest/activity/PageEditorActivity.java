package com.study.hancom.sharephototest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.PageElementListAdapter;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.MathUtil;
import com.study.hancom.sharephototest.view.PageElementListView;

import java.util.ArrayList;
import java.util.List;

public class PageEditorActivity extends AppCompatActivity {

    private static final int MAX_ELEMENT_OF_PAGE_NUM = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_editor_main);

        /* 데이터 변환 (path -> picture) */
        List<String> picturePathList = getIntentData("selectedImage");
        List<Picture> pictureList = new ArrayList<>();

        for (String eachPicturePath : picturePathList) {
            Picture picture = null;
            /*try {
                picture = picturePathToPicture(eachPicturePath);
            } catch (Exception e) {
                e.printStackTrace();
                wrongPictureNum++;
            }*/
            picture = new Picture(eachPicturePath, 0, 0);
            if (picture != null) {
                pictureList.add(picture);
            }
        }

        /* 앨범 페이지 구성 */
        int pictureNum = pictureList.size();
        Album album = new Album("testAlbumName");
        int temp = 0;

        while (temp < pictureNum) {
            try {
                int elementCount = MathUtil.getRandomMath(MAX_ELEMENT_OF_PAGE_NUM, 1);
                if (pictureNum > temp + elementCount) {
                    album.addPage(new Page(elementCount));
                    temp += elementCount;
                } else {
                    album.addPage(new Page(pictureNum - temp));
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* 페이지에 사진 넣기 */
        int pageNum = album.getPageCount();
        int usedPictureCount = 0;

        for (int i = 0 ; i < pageNum ; i++) {
            Page page = album.getPage(i);
            int elementNum = page.getLayout().getElementNum();

            for (int j = 0 ; j < elementNum ; j++) {
                page.addPicture(pictureList.get(usedPictureCount));
                usedPictureCount++;
            }
        }

        Log.v("tag", album.getPageCount() + "count" + album.getPage(0).getPictureCount());

        /* 뷰 어댑터 처리 */
        final PageElementListView pageElementListView = (PageElementListView) findViewById(R.id.page_list_view);
        final PageElementListAdapter pageElementListAdapter = new PageElementListAdapter(this);
        pageElementListView.setAdapter(pageElementListAdapter);

        /* 어댑터에 데이터 전달 */
        for (int i = 0 ; i < pageNum ; i++) {
            pageElementListAdapter.addItem(album.getPage(i));
        }
    }


    private List<String> getIntentData(String extraName) {
        Intent intent = getIntent();

        return intent.getStringArrayListExtra(extraName);
    }

    /*private Picture picturePathToPicture(final String picturePath) throws Exception {

        Picture picture = null;

        FileInputStream fileInputStream = null;
        try {
            String pathForStream = picturePath.replace("file:/", "");
            fileInputStream = new FileInputStream(pathForStream);
            BufferedInputStream bufferedInputStream = null;
            try {
                bufferedInputStream = new BufferedInputStream(fileInputStream);

                Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                picture = new Picture(bitmap, picturePath, width, height);
            } finally {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            }
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }

        return picture;
    }*/
}
