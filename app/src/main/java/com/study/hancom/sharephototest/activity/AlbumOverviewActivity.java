package com.study.hancom.sharephototest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.AlbumGridAdapter;
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.MathUtil;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

import java.util.ArrayList;
import java.util.List;

public class AlbumOverviewActivity extends AppCompatActivity {

    private Album mAlbum;
    private List<Picture> mPictureList = new ArrayList<>();

    private AutoFitRecyclerGridView mAlbumGridView;
    private AlbumGridAdapter mAlbumGridAdapter;

    private MathUtil mMathUtil = new MathUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_overview_main);

        /* 뒤로 가기 버튼 생성 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         /* 인텐트 처리 */
        Bundle bundle = getIntent().getExtras();
        List<String> picturePathList = bundle.getStringArrayList("selectedPicturePathList");

        for (String eachPicturePath : picturePathList) {
            Picture picture = new Picture(eachPicturePath);
            mPictureList.add(picture);
        }

        try {
            /* 앨범 생성 */
            createAlbum(mPictureList);

            /* 어댑터 붙이기 */
            mAlbumGridView = (AutoFitRecyclerGridView) findViewById(R.id.album_overview_grid);
            mAlbumGridAdapter = new AlbumGridAdapter(this, mAlbum);
            mAlbumGridView.setAdapter(mAlbumGridAdapter);
        } catch (LayoutNotFoundException e) {
            //** String 임시
            Toast.makeText(this, "ERROR : 페이지를 구성하는데 필요한 필수 파일을 삭제했냐? (../SharePhoto/layout)", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    private void createAlbum(List<Picture> pictureList) throws LayoutNotFoundException {
        List<Integer> usableElementNumList = new ArrayList<>(Page.getAllPageLayoutType());
        List<Integer> composedElementNumList = mMathUtil.getRandomNumberList(usableElementNumList, pictureList.size());

        mAlbum = new Album();

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
                    //** String 임시
                    Toast.makeText(this, "ERROR : 앨범 재구성을 실패했습니다.", Toast.LENGTH_LONG).show();
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
