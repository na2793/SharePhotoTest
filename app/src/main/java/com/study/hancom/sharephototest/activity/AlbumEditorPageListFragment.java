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
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.AlbumAction;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

public class AlbumEditorPageListFragment extends Fragment implements DataChangeObserverActivity.OnDataChangeListener {
    static final String STATE_ALBUM = "album";

    private Album mAlbum;
    private AlbumAction mAlbumAction = new AlbumAction();

    private AutoFitRecyclerGridView mPageListView;
    private PageListAdapter mPageListAdapter;

    private Button mButtonAddPage;

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

        mButtonAddPage = (Button) view.findViewById(R.id.button_add_page);
        mButtonAddPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mAlbumAction.addPage(mAlbum, 1);
                    mAlbumAction.addPicture(mAlbum, mAlbum.getPageCount() - 1, null);
                    mPageListAdapter.notifyDataSetChanged();
                    ((DataChangeObserverActivity) getActivity()).notifyChanged();
                } catch (LayoutNotFoundException e) {
                    //TODO : 토스트메시지 "페이지를 추가하지 못했습니다"
                    e.printStackTrace();
                }
            }
        });

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

