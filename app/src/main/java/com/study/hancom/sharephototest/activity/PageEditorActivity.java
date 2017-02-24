package com.study.hancom.sharephototest.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class PageEditorActivity extends AppCompatActivity {

    private static final int MAX_ELEMENT_OF_PAGE_NUM = 4;

    private Album mAlbum;

    private PageEditorFrameFragment mPageEditorFrameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_editor_main);

        /* 데이터 파싱 */
        parseIntentData();

        /* 뒤로 가기 버튼 생성 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* 프래그먼트 생성 */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        setFrameFragment(fragmentTransaction);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        setTitle(R.string.title_page_editor_main);
        inflater.inflate(R.menu.page_editor_main, menu);

        return true;
    }

    private void parseIntentData() {
        /* 인텐트 처리 */
        Intent intent = getIntent();
        List<String> picturePathList = intent.getStringArrayListExtra("selectedImage");
        List<Picture> pictureList = new ArrayList<>();

        for (String eachPicturePath : picturePathList) {
            Picture picture = new Picture(eachPicturePath, 0, 0);
            pictureList.add(picture);
        }

        /* 앨범 구성 */
        int pictureNum = pictureList.size();
        mAlbum = new Album("testAlbumName");
        int temp = 0;

        while (temp < pictureNum) {
            try {
                int elementCount = MathUtil.getRandomMath(MAX_ELEMENT_OF_PAGE_NUM, 1);
                if (pictureNum > temp + elementCount) {
                    mAlbum.addPage(new Page(elementCount));
                    temp += elementCount;
                } else {
                    mAlbum.addPage(new Page(pictureNum - temp));
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* 페이지에 사진 넣기 */
        int pageNum = mAlbum.getPageCount();
        int usedPictureCount = 0;

        for (int i = 0; i < pageNum; i++) {
            Page page = mAlbum.getPage(i);
            int elementNum = page.getLayout().getElementNum();

            for (int j = 0; j < elementNum; j++) {
                page.addPicture(pictureList.get(usedPictureCount));
                usedPictureCount++;
            }
        }
    }

    private void setFrameFragment(FragmentTransaction fragmentTransaction) {
        mPageEditorFrameFragment = new PageEditorFrameFragment();
        fragmentTransaction.add(R.id.page_editor_main_frame, mPageEditorFrameFragment);
        // 데이터 전달
        Bundle bundle = new Bundle();
        bundle.putParcelable("temp", mAlbum);
        mPageEditorFrameFragment.setArguments(bundle);
        // 완료
        fragmentTransaction.commit();
    }
}
