package com.study.hancom.sharephototest.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.listener.DataChangedListener;

public class AlbumEditorFrameFragment extends Fragment {

    private Bundle mBundle;

    private AlbumEditorElementListFragment mAlbumEditorElementListFragment;
    private AlbumEditorPageListFragment mAlbumEditorPageListFragment;
    private AlbumEditorPageListVerticalFragment mAlbumEditorPageListVerticalFragment;

    private FragmentManager mFragmentManager;

    private Button mHandlerButton;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mBundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.album_editor_frame, container, false);

        /* 핸들 처리 */
        mHandlerButton = (Button) view.findViewById(R.id.page_list_fragment_handle);
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
        mAlbumEditorElementListFragment.setArguments(mBundle);
        mAlbumEditorPageListFragment.setArguments(mBundle);
        mAlbumEditorPageListVerticalFragment.setArguments(mBundle);

        /* 리스너에 등록 */
        DataChangedListener.addDataChangeListener(mAlbumEditorElementListFragment);
        DataChangedListener.addDataChangeListener(mAlbumEditorPageListFragment);
        DataChangedListener.addDataChangeListener(mAlbumEditorPageListVerticalFragment);

        fragmentTransaction.commit(); // 완료

        return view;
    }
}
