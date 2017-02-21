package com.study.hancom.sharephototest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.PageEditorAdapter;
import com.study.hancom.sharephototest.adapter.base.SectionableAdapter;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class PageEditorActivity extends AppCompatActivity {

    private static final int MAX_ELEMENT_OF_PAGE_NUM = 4;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_editor_main);

        /* 데이터 변환 (path -> picture) */
        List<String> picturePathList = getIntentData("selectedImage");
        List<Picture> pictureList = new ArrayList<>();

        for (String eachPicturePath : picturePathList) {
            Picture picture = new Picture(eachPicturePath, 0, 0);
            pictureList.add(picture);
        }

        /* 앨범 구성 */
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
                // e.printStackTrace();
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

        mListView = (ListView) findViewById(R.id.page_list_view);
        PageEditorAdapter adapter = new PageEditorAdapter(this, album,
                R.layout.page_editor_list_row, R.id.row_header,
                R.id.row_itemHolder, SectionableAdapter.MODE_VARY_WIDTHS);
        mListView.setAdapter(adapter);
        mListView.setDividerHeight(0);
    }


    private List<String> getIntentData(String extraName) {
        Intent intent = getIntent();

        return intent.getStringArrayListExtra(extraName);
    }
}
