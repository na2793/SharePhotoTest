package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.AlbumFullSizeWebViewActivity;
import com.study.hancom.sharephototest.model.PageLayout;
import com.study.hancom.sharephototest.util.WebViewUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LayoutGridAdapter extends BaseAdapter {

    private Context mContext;

    private List<PageLayout> mPageLayoutList;
    private int mSelectedPosition;
    private OnLayoutSelectListener mOnLayoutSelectListener;

    private Set<View> mWebViewSet = new HashSet<>();

    public LayoutGridAdapter(Context context, List<PageLayout> pageLayoutList) {
        mContext = context;
        mPageLayoutList = pageLayoutList;
        mSelectedPosition = -1;
    }

    @Override
    public PageLayout getItem(int position) {
        return mPageLayoutList.get(position);
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
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(mContext).inflate(R.layout.album_editor_new_layout_item, parent, false);
            viewHolder.webView = (WebView) convertView.findViewById(R.id.new_layout_grid_item_web_view);
            viewHolder.webView.loadDataWithBaseURL("file:///android_asset/", WebViewUtil.getDefaultHTMLData(mContext), "text/html", "UTF-8", null);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mSelectedPosition == position) {
            viewHolder.webView.setAlpha(0.2f);
        } else {
            viewHolder.webView.setAlpha(1.0f);
        }

        final GestureDetector webViewGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                mSelectedPosition = position;
                mOnLayoutSelectListener.onSelect();
                return false;
            }
        });

        viewHolder.webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return webViewGestureDetector.onTouchEvent(event);
            }
        });

        viewHolder.webView.setClickable(false);
        viewHolder.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectAll(position, view);
                mWebViewSet.add(view);
            }
        });

        if (mWebViewSet.contains(viewHolder.webView)) {
            injectAll(position, viewHolder.webView);
        }

        return convertView;
    }

    public void setPageLayoutList(List<PageLayout> pageLayoutList) {
        mPageLayoutList = pageLayoutList;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition  = position;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    private boolean injectAll(int position, WebView view) {
        PageLayout pageLayout = getItem(position);
        int elementNum = pageLayout.getElementNum();

        WebViewUtil.injectDivByScript(view, elementNum);
        WebViewUtil.injectStyleByScript(view, pageLayout.getPath());

        // inject data
        for (int i = 0; i < elementNum; i++) {
            WebViewUtil.injectEmptyImageByScript(view, "_" + (i + 1));
        }

        return true;
    }

    private class ViewHolder {
        WebView webView;
    }

    public void setOnLayoutSelectListener(OnLayoutSelectListener listener) {
        mOnLayoutSelectListener = listener;
    }

    public interface OnLayoutSelectListener {
        void onSelect();
    }
}