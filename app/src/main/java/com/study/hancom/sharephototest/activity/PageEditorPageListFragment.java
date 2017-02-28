package com.study.hancom.sharephototest.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.ElementListAdapter;
import com.study.hancom.sharephototest.adapter.base.SectionableAdapter;
import com.study.hancom.sharephototest.listener.DataChangedListener;
import com.study.hancom.sharephototest.model.Album;

import java.io.File;

import static com.study.hancom.sharephototest.model.Album.MAX_ELEMENT_OF_PAGE_NUM;

public class PageEditorPageListFragment extends Fragment implements DataChangedListener.OnDataChangeListener {

    WebView mWebView;

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

        /* 웹뷰 처리 */
        mWebView = (WebView) view.findViewById(R.id.page_list_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        File directory = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/LimHarim/layout/frame/1.html");
        mWebView.loadUrl("file:///" + directory.getAbsolutePath());

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated
        final int val = width / 2;

        /*data = "file:///storage/emulated/0/Pictures/Screenshots/ic_app_manager.png";

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.setLayoutParams(new LinearLayout.LayoutParams(val, 561));

        Log.v("value", mWebView.getUrl() + "        123123string ");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                Log.v("value", view.getUrl() + "string ");
                Log.v("value", url + "        123123string ");
                mWebView.loadUrl("javascript:setMessage('" + data + "')");
                mWebView.invalidate();
            }
        });*/

        return view;
    }

    @Override
    public void onDataChanged() {

    }
}
