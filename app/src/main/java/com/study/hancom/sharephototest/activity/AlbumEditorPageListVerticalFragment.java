package com.study.hancom.sharephototest.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.base.DataChangeObserverActivity;
import com.study.hancom.sharephototest.adapter.PageListAdapter;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

public class AlbumEditorPageListVerticalFragment extends Fragment implements DataChangeObserverActivity.OnDataChangeListener {

    private Album mAlbum;

    private AutoFitRecyclerGridView mVerticalPageListView;
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
        View view = inflater.inflate(R.layout.album_editor_page_list_vertical, container, false);

        mVerticalPageListView = (AutoFitRecyclerGridView) view.findViewById(R.id.page_grid_view_vertical);
        mPageListAdapter = new PageListAdapter(getActivity(), mAlbum);
        mVerticalPageListView.setAdapter(mPageListAdapter);

        return view;
    }

    @Override
    public void onDataChanged() {
        mPageListAdapter.notifyDataSetChanged();
    }

}
