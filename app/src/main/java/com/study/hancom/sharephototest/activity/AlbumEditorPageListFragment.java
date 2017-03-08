package com.study.hancom.sharephototest.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.PageListAdapter;
import com.study.hancom.sharephototest.listener.DataChangedListener;
import com.study.hancom.sharephototest.model.Album;

public class AlbumEditorPageListFragment extends Fragment implements DataChangedListener.OnDataChangeListener {

    HorizontalGridView mPageListView;
    PageListAdapter mPageListAdapter;

    private Album mAlbum;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Bundle extra = getArguments();
        mAlbum = extra.getParcelable("album");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.album_editor_page_list, container, false);

        mPageListView = (HorizontalGridView) view.findViewById(R.id.page_grid_view);
        mPageListAdapter = new PageListAdapter(getActivity(), mAlbum);
        mPageListView.setAdapter(mPageListAdapter);

        return view;
    }

    @Override
    public void onDataChanged() {
        mPageListAdapter.notifyDataSetChanged();
    }
}
