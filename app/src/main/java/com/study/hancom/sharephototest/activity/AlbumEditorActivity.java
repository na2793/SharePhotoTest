package com.study.hancom.sharephototest.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.AlbumManager;
import com.study.hancom.sharephototest.model.PageLayout;

public class AlbumEditorActivity extends AppCompatActivity {
    static final String STATE_BUNDLE = "bundle";
    static final String STATE_HORIZONTAL_PAGE_LIST_FRAGMENT_VISIBILITY = "horizontalPageListFragmentVisibility";

    private Bundle mBundle;
    private Album mAlbum;

    private FragmentManager mFragmentManager;
    private AlbumEditorElementGridFragment mAlbumEditorElementGridFragment;
    private AlbumEditorHorizontalPageListFragment mAlbumEditorHorizontalPageListFragment;
    private AlbumEditorPageListFragment mAlbumEditorPageListFragment;

    private LinearLayout mElementGridFragmentContainer;
    private LinearLayout mHorizontalPageListFragmentContainer;
    private LinearLayout mPageListFragmentContainer;

    private Button mHandlerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO: 가로 방향 레이아웃 처리
        /* 레이아웃 처리 */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.album_editor_main);             // 세로 방향에서 사용할 레이아웃
        } else {
            setContentView(R.layout.album_editor_main_horizontal);  // 가로 방향에서 사용할 레이아웃
        }

        mElementGridFragmentContainer = (LinearLayout) findViewById(R.id.element_list_fragment_container);
        mHorizontalPageListFragmentContainer = (LinearLayout) findViewById(R.id.horizontal_page_list_fragment_container);
        mPageListFragmentContainer = (LinearLayout) findViewById(R.id.page_list_fragment_container);

        if (savedInstanceState != null) {
            mBundle = savedInstanceState.getBundle(STATE_BUNDLE);
            if (savedInstanceState.getInt(STATE_HORIZONTAL_PAGE_LIST_FRAGMENT_VISIBILITY) == View.VISIBLE) {
                mHorizontalPageListFragmentContainer.setVisibility(View.VISIBLE);
                mPageListFragmentContainer.setVisibility(View.GONE);
            } else {
                mHorizontalPageListFragmentContainer.setVisibility(View.GONE);
                mPageListFragmentContainer.setVisibility(View.VISIBLE);
            }
        } else {
            mBundle = getIntent().getExtras();
        }

        mAlbum = mBundle.getParcelable("album");

        /* 핸들 처리 */
        mHandlerButton = (Button) findViewById(R.id.page_list_fragment_handle);
        mHandlerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHorizontalPageListFragmentContainer.getVisibility() == View.VISIBLE) {
                    mHorizontalPageListFragmentContainer.setVisibility(View.GONE);
                    mPageListFragmentContainer.setVisibility(View.VISIBLE);
                } else {
                    mHorizontalPageListFragmentContainer.setVisibility(View.VISIBLE);
                    mPageListFragmentContainer.setVisibility(View.GONE);
                }
            }
        });

        /* 프래그먼트 생성 */
        mFragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        mAlbumEditorElementGridFragment = (AlbumEditorElementGridFragment) mFragmentManager.findFragmentByTag("testTag");
        if (mAlbumEditorElementGridFragment == null) {
            mAlbumEditorElementGridFragment = new AlbumEditorElementGridFragment();
            mAlbumEditorElementGridFragment.setArguments(mBundle);
            fragmentTransaction.add(mElementGridFragmentContainer.getId(), mAlbumEditorElementGridFragment, "testTag");
        }
        mAlbumEditorElementGridFragment.setHasOptionsMenu(true);    // 옵션 메뉴 변경

        mAlbumEditorHorizontalPageListFragment = (AlbumEditorHorizontalPageListFragment) mFragmentManager.findFragmentByTag("testTag2");
        if (mAlbumEditorHorizontalPageListFragment == null) {
            mAlbumEditorHorizontalPageListFragment = new AlbumEditorHorizontalPageListFragment();
            mAlbumEditorHorizontalPageListFragment.setArguments(mBundle);
            fragmentTransaction.add(mHorizontalPageListFragmentContainer.getId(), mAlbumEditorHorizontalPageListFragment, "testTag2");
        }

        mAlbumEditorPageListFragment = (AlbumEditorPageListFragment) mFragmentManager.findFragmentByTag("testTag3");
        if (mAlbumEditorPageListFragment == null) {
            mAlbumEditorPageListFragment = new AlbumEditorPageListFragment();
            mAlbumEditorPageListFragment.setArguments(mBundle);
            fragmentTransaction.add(mPageListFragmentContainer.getId(), mAlbumEditorPageListFragment, "testTag3");
        }

        /* 서로 옵저빙 */
        mAlbumEditorElementGridFragment.addObserver("HorizontalPageListFragment", mAlbumEditorHorizontalPageListFragment);
        mAlbumEditorElementGridFragment.addObserver("PageListFragment", mAlbumEditorPageListFragment);

        mAlbumEditorHorizontalPageListFragment.addObserver("ElementGridFragment", mAlbumEditorElementGridFragment);
        mAlbumEditorHorizontalPageListFragment.addObserver("PageListFragment", mAlbumEditorPageListFragment);

        mAlbumEditorPageListFragment.addObserver("ElementGridFragment", mAlbumEditorElementGridFragment);
        mAlbumEditorPageListFragment.addObserver("HorizontalPageListFragment", mAlbumEditorHorizontalPageListFragment);

        fragmentTransaction.commit(); // 완료
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBundle(STATE_BUNDLE, mBundle);
        outState.putInt(STATE_HORIZONTAL_PAGE_LIST_FRAGMENT_VISIBILITY, mHorizontalPageListFragmentContainer.getVisibility());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mPageListFragmentContainer.getVisibility() == View.VISIBLE) {
            mHorizontalPageListFragmentContainer.setVisibility(View.VISIBLE);
            mPageListFragmentContainer.setVisibility(View.GONE);
        } else {
            mAlbumEditorElementGridFragment.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();

            int section = bundle.getInt("currentSection");
            int originPageElementNum = mAlbum.getPage(section).getPictureCount();
            PageLayout pageLayout = bundle.getParcelable("currentPageLayout");
            int changePageElementNum = pageLayout.getElementNum();
            AlbumManager.setLayout(mAlbum, section, pageLayout);
            for (int i = 0; i < changePageElementNum - originPageElementNum ; i++) {
                AlbumManager.addPicture(mAlbum , section, null);
            }
            mAlbumEditorPageListFragment.update(null);
            mAlbumEditorPageListFragment.notifyChangedAll();
        }
    }
}
