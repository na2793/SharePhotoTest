package com.study.hancom.sharephototest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.AlbumGridAdapter;
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.MathUtil;
import com.study.hancom.sharephototest.view.AutoFitRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AlbumOverviewActivity extends AppCompatActivity {

    private Album mAlbum;
    private List<Picture> mPictureList = new ArrayList<>();

    private AutoFitRecyclerView mAlbumGridView;
    private AlbumGridAdapter mAlbumGridAdapter;

    private MathUtil mMathUtil = new MathUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_overview_main);

        /* 뒤로 가기 버튼 생성 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         /* 데이터 파싱 */
        parseIntentData();

           /* 앨범 생성 */
        try {
            createAlbum(mPictureList);
        } catch (LayoutNotFoundException e) {
            e.printStackTrace();
        }

        /* 어댑터 붙이기 */
        mAlbumGridView = (AutoFitRecyclerView) findViewById(R.id.album_overview_grid);
        mAlbumGridAdapter = new AlbumGridAdapter(this, mAlbum);
        mAlbumGridView.setAdapter(mAlbumGridAdapter);
    }

    private void parseIntentData() {
        /* 인텐트 처리 */
        Bundle bundle = getIntent().getExtras();
        List<String> picturePathList = bundle.getStringArrayList("AlbumElementPaths");

        for (String eachPicturePath : picturePathList) {
            Picture picture = new Picture(eachPicturePath);
            mPictureList.add(picture);
        }
    }

    private void createAlbum(List<Picture> pictureList) throws LayoutNotFoundException {
        mAlbum = new Album();

        List<Integer> usableElementNumList = new ArrayList<>(Page.getAllPageLayoutType());
        List<Integer> composedElementNumList = mMathUtil.getRandomNumberList(usableElementNumList, pictureList.size());
        for (int eachElementNum : composedElementNumList) {
            Page newPage = new Page(eachElementNum);
            mAlbum.addPage(newPage);
            Log.v("tag", "페이지 생성 " + eachElementNum);
            for (int i = 0; i < eachElementNum; i++) {
                newPage.addPicture(pictureList.remove(0));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_overview_main, menu);
        setTitle(R.string.title_album_overview_main);
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
                try {
                    mAlbumGridAdapter.relayout();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAlbumGridAdapter.notifyDataSetChanged();
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
