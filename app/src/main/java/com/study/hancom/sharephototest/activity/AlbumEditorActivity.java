package com.study.hancom.sharephototest.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.base.DataChangeObserverActivity;
import com.study.hancom.sharephototest.model.Album;

public class AlbumEditorActivity extends DataChangeObserverActivity {

    private FragmentManager mFragmentManager;

    private AlbumEditorElementListFragment mAlbumEditorElementListFragment;
    private AlbumEditorPageListFragment mAlbumEditorPageListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_editor_main);

        /* 뒤로 가기 버튼 생성 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* 인텐트 처리 */
        Bundle bundle = getIntent().getExtras();

        /* 프래그먼트 생성 */
        mFragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        mAlbumEditorElementListFragment = new AlbumEditorElementListFragment();
        fragmentTransaction.add(R.id.element_list_fragment_container, mAlbumEditorElementListFragment);
        mAlbumEditorElementListFragment.setHasOptionsMenu(true);    // 옵션 메뉴 변경

        mAlbumEditorPageListFragment = new AlbumEditorPageListFragment();
        fragmentTransaction.add(R.id.page_list_fragment_container, mAlbumEditorPageListFragment);

        // 데이터 전달
        mAlbumEditorElementListFragment.setArguments(bundle);
        mAlbumEditorPageListFragment.setArguments(bundle);

        /* 리스너에 등록 */
        addDataChangeListener(mAlbumEditorElementListFragment);
        addDataChangeListener(mAlbumEditorPageListFragment);

        fragmentTransaction.commit(); // 완료

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_editor_main, menu);
        setTitle(R.string.title_album_editor_main);

        return true;
    }
}
