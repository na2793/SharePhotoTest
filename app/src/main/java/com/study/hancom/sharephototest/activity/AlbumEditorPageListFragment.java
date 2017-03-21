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
import android.widget.Button;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.base.DataChangeObserverActivity;
import com.study.hancom.sharephototest.adapter.PageListAdapter;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

public class AlbumEditorPageListFragment extends Fragment implements DataChangeObserverActivity.OnDataChangeListener {

    private Context mContext;

    private Album mAlbum;

    private Button mHandleButton;
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
        mContext = getActivity();

        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.album_editor_page_list, container, false);

        mPageListView = (AutoFitRecyclerGridView) view.findViewById(R.id.page_list_view);
        mPageListAdapter = new PageListAdapter(mContext, mAlbum);
        mPageListView.setAdapter(mPageListAdapter);

        /* 핸들 처리 */
        mHandleButton = (Button) view.findViewById(R.id.page_list_fragment_handle);
        mHandleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pageListViewOrientation = mPageListView.getOrientation();
                if (pageListViewOrientation == AutoFitRecyclerGridView.VERTICAL) {
                    mPageListView.setOrientation(AutoFitRecyclerGridView.HORIZONTAL);
                } else {
                    mPageListView.setOrientation(AutoFitRecyclerGridView.VERTICAL);
                }
            }
        });

        return view;
    }

    @Override
    public void onDataChanged() {
        mPageListAdapter.notifyDataSetChanged();
    }
}

