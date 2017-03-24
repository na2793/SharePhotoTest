package com.study.hancom.sharephototest.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.base.DataChangeObserverActivity;
import com.study.hancom.sharephototest.adapter.PageListAdapter;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

public class AlbumEditorPageListFragment extends Fragment implements DataChangeObserverActivity.OnDataChangeListener {
    static final String STATE_ALBUM = "album";

    private Album mAlbum;

    private AutoFitRecyclerGridView mPageListView;
    private PageListAdapter mPageListAdapter;

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

        if (savedInstanceState != null) {
            mAlbum = savedInstanceState.getParcelable(STATE_ALBUM);
        }

        mPageListView = (AutoFitRecyclerGridView) view.findViewById(R.id.page_list_view);
        mPageListAdapter = new PageListAdapter(getActivity(), mAlbum);
        mPageListView.setAdapter(mPageListAdapter);

        return view;
    }

    @Override
    public void onDataChanged() {
        mPageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_ALBUM, mAlbum);
        super.onSaveInstanceState(outState);
    }
}

