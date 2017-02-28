package com.study.hancom.sharephototest.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.ElementListAdapter;
import com.study.hancom.sharephototest.adapter.PageGridAdapter;
import com.study.hancom.sharephototest.adapter.base.SectionableAdapter;
import com.study.hancom.sharephototest.listener.DataChangedListener;
import com.study.hancom.sharephototest.model.Album;

import java.io.File;

import static com.study.hancom.sharephototest.model.Album.MAX_ELEMENT_OF_PAGE_NUM;

public class PageEditorPageListFragment extends Fragment implements DataChangedListener.OnDataChangeListener {

    GridView mPageGridView;
    PageGridAdapter mPageGridAdapter;

    private Album mAlbum;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Bundle extra = getArguments();
        mAlbum = extra.getParcelable("temp");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.page_editor_page_list, container, false);

        mPageGridView = (GridView) view.findViewById(R.id.page_grid_view);
        mPageGridAdapter = new PageGridAdapter(getActivity(), mAlbum);
        mPageGridView.setAdapter(mPageGridAdapter);

        return view;
    }

    @Override
    public void onDataChanged() {
        mPageGridAdapter.notifyDataSetChanged();
    }
}
