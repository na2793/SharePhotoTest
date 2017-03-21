package com.study.hancom.sharephototest.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.base.DataChangeObserverActivity;
import com.study.hancom.sharephototest.adapter.PageListAdapter;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

public class AlbumEditorPageListFragment extends Fragment implements DataChangeObserverActivity.OnDataChangeListener {

    private Context mContext;

    private Album mAlbum;

    private RecyclerView mPageListView;
    private PageListAdapter mPageListAdapter;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Bundle extra = getArguments();
        mAlbum = extra.getParcelable("album");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.album_editor_page_list, container, false);

        mPageListView = (RecyclerView) view.findViewById(R.id.page_grid_view);
        mPageListAdapter = new PageListAdapter(mContext, mAlbum);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mPageListView.setLayoutManager(horizontalLayoutManager);
        mPageListView.setAdapter(mPageListAdapter);

        return view;
    }

    @Override
    public void onDataChanged() {
        mPageListAdapter.notifyDataSetChanged();
    }
}

