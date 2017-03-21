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
    private AlbumEditorPageListVerticalFragment mAlbumEditorPageListVerticalFragment;

    private Button mHandlerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_editor_main);

        /* 뒤로 가기 버튼 생성 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* 인텐트 처리 */
        Bundle bundle = getIntent().getExtras();

        /* 핸들 처리 */
        mHandlerButton = (Button) findViewById(R.id.page_list_fragment_handle);
        mHandlerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View albumEditorPageListFragmentContainer = ((ViewGroup) mAlbumEditorPageListFragment.getView().getParent());
                View albumEditorPageListVerticalFragmentContainer = ((ViewGroup) mAlbumEditorPageListVerticalFragment.getView().getParent());
                if (albumEditorPageListFragmentContainer.getVisibility() == View.GONE) {
                    albumEditorPageListFragmentContainer.setVisibility(View.VISIBLE);
                    albumEditorPageListVerticalFragmentContainer.setVisibility(View.GONE);
                } else if (albumEditorPageListFragmentContainer.getVisibility() == View.VISIBLE) {
                    albumEditorPageListFragmentContainer.setVisibility(View.GONE);
                    albumEditorPageListVerticalFragmentContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        /* 프래그먼트 생성 */
        mFragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        mAlbumEditorElementListFragment = new AlbumEditorElementListFragment();
        fragmentTransaction.add(R.id.element_list_fragment_container, mAlbumEditorElementListFragment);
        mAlbumEditorElementListFragment.setHasOptionsMenu(true);    // 옵션 메뉴 변경

        mAlbumEditorPageListFragment = new AlbumEditorPageListFragment();
        fragmentTransaction.add(R.id.page_list_fragment_container, mAlbumEditorPageListFragment);

        mAlbumEditorPageListVerticalFragment = new AlbumEditorPageListVerticalFragment();
        fragmentTransaction.add(R.id.page_list_vertical_fragment_container, mAlbumEditorPageListVerticalFragment);

        // 데이터 전달
        mAlbumEditorElementListFragment.setArguments(bundle);
        mAlbumEditorPageListFragment.setArguments(bundle);
        mAlbumEditorPageListVerticalFragment.setArguments(bundle);

        /* 리스너에 등록 */
        addDataChangeListener(mAlbumEditorElementListFragment);
        addDataChangeListener(mAlbumEditorPageListFragment);
        addDataChangeListener(mAlbumEditorPageListVerticalFragment);

        fragmentTransaction.commit(); // 완료

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_editor_main, menu);
        setTitle(R.string.title_album_editor_main);

        return true;
    }
}
