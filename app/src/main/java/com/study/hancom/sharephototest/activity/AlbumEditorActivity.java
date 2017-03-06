package com.study.hancom.sharephototest.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Album;

public class AlbumEditorActivity extends AppCompatActivity {

    private Album mAlbum;

    private AlbumEditorFrameFragment mAlbumEditorFrameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_editor_main);

        /* 데이터 파싱 */
        try {
            parseIntentData();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        setTitle(R.string.title_album_editor_main);
        inflater.inflate(R.menu.album_editor_main, menu);

        return true;
    }

    private void parseIntentData() throws Exception {
        /* 인텐트 처리 */
        Bundle bundle = getIntent().getExtras();
        mAlbum = bundle.getParcelable("album");
    }

    private void setFrameFragment(FragmentTransaction fragmentTransaction) {
        mAlbumEditorFrameFragment = new AlbumEditorFrameFragment();
        fragmentTransaction.add(R.id.page_editor_main_frame, mAlbumEditorFrameFragment);
        // 데이터 전달
        Bundle bundle = new Bundle();
        bundle.putParcelable("album", mAlbum);
        mAlbumEditorFrameFragment.setArguments(bundle);
        // 완료
        fragmentTransaction.commit();
    }
}
