package com.study.hancom.sharephototest.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.base.IObservable;
import com.study.hancom.sharephototest.activity.base.IObserver;
import com.study.hancom.sharephototest.adapter.PageListAdapter;
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.AlbumManager;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AlbumEditorPageListFragment extends Fragment implements IObservable, IObserver {
    static final String STATE_ALBUM = "album";

    private Map<String, IObserver> mObserverMap = new HashMap<>();

    private Album mAlbum;

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
                    AlbumManager.addPage(mAlbum, 1);
                    AlbumManager.addPicture(mAlbum, mAlbum.getPageCount() - 1, null);
                    mPageListAdapter.notifyDataSetChanged();
                    notifyChangedAll();
                } catch (LayoutNotFoundException e) {
                    //TODO : 토스트메시지 "페이지를 추가하지 못했습니다"
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_ALBUM, mAlbum);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void addObserver(String tag, IObserver observer) {
        mObserverMap.put(tag, observer);
    }

    @Override
    public IObserver removeObserver(String tag) {
        return mObserverMap.remove(tag);
    }

    @Override
    public IObserver getObserver(String tag) {
        return mObserverMap.get(tag);
    }

    @Override
    public int getObserverCount() {
        return mObserverMap.size();
    }

    @Override
    public void notifyChangedAll() {
        notifyChangedAll(null);
    }

    @Override
    public void notifyChangedAll(Bundle out) {
        Set observerTagSet = mObserverMap.keySet();
        for (Object eachTag : observerTagSet) {
            mObserverMap.get(eachTag).update(out);
        }
    }

    @Override
    public void notifyChanged(String tag) {
        notifyChanged(tag, null);
    }

    @Override
    public void notifyChanged(String tag, Bundle out) {
        mObserverMap.get(tag).update(out);
    }

    @Override
    public void update(Bundle in) {
        mPageListAdapter.notifyDataSetChanged();
    }
}

