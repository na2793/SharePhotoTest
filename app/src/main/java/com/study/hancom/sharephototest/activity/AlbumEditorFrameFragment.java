package com.study.hancom.sharephototest.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.listener.DataChangedListener;
import com.study.hancom.sharephototest.model.Album;

public class AlbumEditorFrameFragment extends Fragment {

    private Album mAlbum;

    private AlbumEditorElementListFragment mAlbumEditorElementListFragment;
    private AlbumEditorPageListFragment mAlbumEditorPageListFragment;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Bundle extra = getArguments();
        mAlbum = extra.getParcelable("album");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.album_editor_frame, container, false);

        /* 프래그먼트 생성 */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        setElementListFragment(fragmentTransaction);
        setPageListFragment(fragmentTransaction);
        fragmentTransaction.commit(); // 완료

        /* 리스너에 등록 */
        DataChangedListener.addDataChangeListener(mAlbumEditorElementListFragment);
        DataChangedListener.addDataChangeListener(mAlbumEditorPageListFragment);

        return view;
    }

    private void setElementListFragment(FragmentTransaction fragmentTransaction) {
        mAlbumEditorElementListFragment = new AlbumEditorElementListFragment();
        fragmentTransaction.add(R.id.element_list_fragment_container, mAlbumEditorElementListFragment);
        // 액션바를 가짐
        mAlbumEditorElementListFragment.setHasOptionsMenu(true);
        // 데이터 전달
        Bundle bundle = new Bundle();
        bundle.putParcelable("album", mAlbum);
        mAlbumEditorElementListFragment.setArguments(bundle);
    }

    private void setPageListFragment(FragmentTransaction fragmentTransaction) {
        mAlbumEditorPageListFragment = new AlbumEditorPageListFragment();
        fragmentTransaction.add(R.id.page_list_fragment_container, mAlbumEditorPageListFragment);
        // 데이터 전달
        Bundle bundle = new Bundle();
        bundle.putParcelable("album", mAlbum);
        mAlbumEditorPageListFragment.setArguments(bundle);
    }
}
