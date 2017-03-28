package com.study.hancom.sharephototest.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.base.DataChangeObserverActivity;

public class AlbumEditorActivity extends DataChangeObserverActivity {
    private FragmentManager mFragmentManager;

    private AlbumEditorElementGridFragment mAlbumEditorElementGridFragment;
    private AlbumEditorHorizontalPageListFragment mAlbumEditorHorizontalPageListFragment;
    private AlbumEditorPageListFragment mAlbumEditorPageListFragment;

    private Button mHandlerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_editor_main);

        // 뒤로 가기 버튼 생성
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 인텐트 처리
        Bundle bundle = getIntent().getExtras();

        // 핸들 처리
        mHandlerButton = (Button) findViewById(R.id.page_list_fragment_handle);
        mHandlerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View albumEditorHorizontalPageListFragment = ((ViewGroup) mAlbumEditorHorizontalPageListFragment.getView().getParent());
                View albumEditorPageListFragmentContainer = ((ViewGroup) mAlbumEditorPageListFragment.getView().getParent());
                if (albumEditorHorizontalPageListFragment.getVisibility() == View.GONE) {
                    albumEditorHorizontalPageListFragment.setVisibility(View.VISIBLE);
                    albumEditorPageListFragmentContainer.setVisibility(View.GONE);
                } else if (albumEditorHorizontalPageListFragment.getVisibility() == View.VISIBLE) {
                    albumEditorHorizontalPageListFragment.setVisibility(View.GONE);
                    albumEditorPageListFragmentContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        // 프래그먼트 생성
        mFragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        mAlbumEditorElementGridFragment = new AlbumEditorElementGridFragment();
        fragmentTransaction.add(R.id.element_list_fragment_container, mAlbumEditorElementGridFragment);
        mAlbumEditorElementGridFragment.setHasOptionsMenu(true);    // 옵션 메뉴 변경

        mAlbumEditorHorizontalPageListFragment = new AlbumEditorHorizontalPageListFragment();
        fragmentTransaction.add(R.id.horizontal_page_list_fragment_container, mAlbumEditorHorizontalPageListFragment);

        mAlbumEditorPageListFragment = new AlbumEditorPageListFragment();
        fragmentTransaction.add(R.id.page_list_fragment_container, mAlbumEditorPageListFragment);

        // 데이터 전달
        mAlbumEditorElementGridFragment.setArguments(bundle);
        mAlbumEditorHorizontalPageListFragment.setArguments(bundle);
        mAlbumEditorPageListFragment.setArguments(bundle);

        // 리스너에 등록
        addDataChangeListener(mAlbumEditorElementGridFragment);
        addDataChangeListener(mAlbumEditorHorizontalPageListFragment);
        addDataChangeListener(mAlbumEditorPageListFragment);

        // 완료
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_editor_main, menu);
        setTitle(R.string.title_album_editor_main);

        return true;
    }
}
