package com.study.hancom.sharephototest.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.PageListAdapter;
import com.study.hancom.sharephototest.model.Album;

public class AlbumEditorPageListVerticalFragment extends Fragment{

    private Album mAlbum;
    private RecyclerView mVerticalPageListView;
    private PageListAdapter mPageListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.album_editor_page_list_vertical, container, false);

        mVerticalPageListView = (RecyclerView) view.findViewById(R.id.page_grid_view_vertical);
        mVerticalPageListView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mPageListAdapter = new PageListAdapter(getActivity(), mAlbum);
        mVerticalPageListView.setAdapter(mPageListAdapter);

        return view;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Bundle extra = getArguments();
        mAlbum = extra.getParcelable("album");
    }
}
