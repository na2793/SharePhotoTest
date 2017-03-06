package com.study.hancom.sharephototest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.AlbumGridAdapter;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

import static com.study.hancom.sharephototest.model.Album.MAX_ELEMENT_OF_PAGE_NUM;

public class AlbumOverviewActivity extends AppCompatActivity {

    private Album mAlbum;
    private List<Picture> mPictureList = new ArrayList<>();

    private GridView mAlbumGridView;
    private AlbumGridAdapter mAlbumGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_overview_main);

         /* 데이터 파싱 */
        parseIntentData();

        try {
            setAlbum();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* 뒤로 가기 버튼 생성 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /* 어댑터 붙이기 */
        mAlbumGridView = (GridView) findViewById(R.id.album_overview_grid);
        mAlbumGridAdapter = new AlbumGridAdapter(this, mAlbum);
        mAlbumGridView.setAdapter(mAlbumGridAdapter);
    }

    private void parseIntentData() {
        /* 인텐트 처리 */
        Intent intent = getIntent();
        List<String> picturePathList = intent.getStringArrayListExtra("selectedImage");

        for (String eachPicturePath : picturePathList) {
            Picture picture = new Picture(eachPicturePath, 0, 0);
            mPictureList.add(picture);
        }
    }

    private void setAlbum() throws Exception {
        /* 앨범 구성 */
        mAlbum = new Album();
        int pictureNum = mPictureList.size();
        int temp = 0;
        int errorCount = 0;

        while (temp < pictureNum) {
            try {
                int elementNum = MathUtil.getRandomMath(MAX_ELEMENT_OF_PAGE_NUM, 1);
                if (pictureNum > temp + elementNum) {
                    mAlbum.addPage(new Page(elementNum));
                    temp += elementNum;
                } else {
                    mAlbum.addPage(new Page(pictureNum - temp));
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorCount++;
            }
            if (errorCount > 10) {
                // 레이아웃 시도를 10회 이상 실패함
                throw new Exception();
            }
        }

        /* 페이지에 사진 넣기 */
        int pageNum = mAlbum.getPageCount();
        int usedPictureCount = 0;

        for (int i = 0; i < pageNum; i++) {
            Page page = mAlbum.getPage(i);
            int elementNum = page.getLayout().getElementNum();

            for (int j = 0; j < elementNum; j++) {
                page.addPicture(mPictureList.get(usedPictureCount));
                usedPictureCount++;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.album_overview_main, menu);
        setTitle(R.string.title_album_editor_main);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_album_edit:
                Intent intentEditor = new Intent(this, AlbumEditorActivity.class);
                intentEditor.putExtra("album", mAlbum);
                startActivity(intentEditor);
                return true;

            case R.id.action_album_relayout:
                /* 재구성 */
                return true;

            case R.id.action_album_confirm:  /* 저장 */
                /*Intent intentSave = new Intent(this, AlbumSaveActivity.class);
                intentSave.putExtra("album", mAlbum);
                startActivity(intentSave);*/
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
