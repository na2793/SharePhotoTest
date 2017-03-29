package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.PageLayout;
import com.study.hancom.sharephototest.util.WebViewUtil;
import com.study.hancom.sharephototest.view.HDSizeWebView;

import java.io.IOException;
import java.util.List;

public class LayoutGridAdapter extends BaseAdapter {

    protected Context mContext;
    private int mType;
    private List<PageLayout> mPageLayoutList;

    public LayoutGridAdapter(Context context, int type, List<PageLayout> pageLayoutList) {
        mContext = context;
        mType = type;
        mPageLayoutList = pageLayoutList;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mPageLayoutList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.album_editor_new_layout_item, parent, false);

            viewHolder.hdSizeWebView = (WebView) convertView.findViewById(R.id.new_layout_grid_item_web_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.hdSizeWebView.loadDataWithBaseURL("file:///android_asset/", WebViewUtil.getDefaultHTMLData(mContext), "text/html", "UTF-8", null);
        viewHolder.hdSizeWebView.setClickable(false);
        viewHolder.hdSizeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectAll(position, view);
            }
        });

        return convertView;
    }

    public void setPageLayoutList(List<PageLayout> pageLayoutList, int type) {
        mPageLayoutList = pageLayoutList;
        mType = type;
    }

    private boolean injectAll(int position, WebView view) {
        WebViewUtil.injectDivByScript(view, mType);
        WebViewUtil.injectStyleByScript(view, mPageLayoutList.get(position).getPath());
        // inject data
        for (int i = 0; i < mType; i++) {
            WebViewUtil.injectEmptyImageByScript(view, "_" + (i + 1));
        }

        return true;
    }

    private class ViewHolder {
        WebView hdSizeWebView;
    }
}
