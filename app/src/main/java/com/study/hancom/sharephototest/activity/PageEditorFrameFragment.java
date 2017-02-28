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

public class PageEditorFrameFragment extends Fragment {

    private Album mAlbum;

    private PageEditorElementListFragment mPageEditorElementListFragment;
    private PageEditorPageListFragment mPageEditorPageListFragment;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Bundle extra = getArguments();
        mAlbum = extra.getParcelable("temp");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.page_editor_frame, container, false);

        /* 프래그먼트 생성 */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        setElementListFragment(fragmentTransaction);
        setPageListFragment(fragmentTransaction);
        fragmentTransaction.commit(); // 완료

        /* 리스너에 등록 */
        DataChangedListener.addDataChangeListener(mPageEditorElementListFragment);
        DataChangedListener.addDataChangeListener(mPageEditorPageListFragment);

        return view;
    }

    private void setElementListFragment(FragmentTransaction fragmentTransaction) {
        mPageEditorElementListFragment = new PageEditorElementListFragment();
        fragmentTransaction.add(R.id.element_list_fragment_container, mPageEditorElementListFragment);
        // 액션바를 가짐
        mPageEditorElementListFragment.setHasOptionsMenu(true);
        // 데이터 전달
        Bundle bundle = new Bundle();
        bundle.putParcelable("temp", mAlbum);
        mPageEditorElementListFragment.setArguments(bundle);
    }

    private void setPageListFragment(FragmentTransaction fragmentTransaction) {
        mPageEditorPageListFragment = new PageEditorPageListFragment();
        fragmentTransaction.add(R.id.page_list_fragment_container, mPageEditorPageListFragment);
        // 데이터 전달
        Bundle bundle = new Bundle();
        bundle.putParcelable("temp", mAlbum);
        mPageEditorPageListFragment.setArguments(bundle);
    }
}
