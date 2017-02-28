package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.GalleryFullSizePictureActivity;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.ImageUtil;

import java.io.InputStream;
import java.util.List;

public class PageGridAdapter extends BaseAdapter {

    private Context mContext;
    private Album mAlbum;

    public PageGridAdapter(Context context, Album album) {
        mContext = context;
        mAlbum = album;
    }

    @Override
    public int getCount() {
        return mAlbum.getPageCount();
    }

    @Override
    public Page getItem(int index) {
        return mAlbum.getPage(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.page_editor_page_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.webView = (WebView) convertView.findViewById(R.id.page_list_item_webview);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Page page = mAlbum.getPage(index);

        // Enable Javascript
        viewHolder.webView.getSettings().setJavaScriptEnabled(true);
        viewHolder.webView.getSettings().setDomStorageEnabled(true);
        viewHolder.webView.getSettings().setLoadWithOverviewMode(true);
        viewHolder.webView.getSettings().setUseWideViewPort(true);

        // Add a WebViewClient
        viewHolder.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // css inject
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                for (int i = 0 ; i < page.getPictureCount() ; i++) {
                    injectImageByScript(view, "_" + (i + 1), page.getPicture(i).getPath());
                    Log.v("tag", page.getPicture(i).getPath());
                }

                super.onPageFinished(view, url);
            }
        });

        viewHolder.webView.loadUrl("file://" + page.getLayout().getFramePath());

        //viewHolder.webView.loadDataWithBaseURL("file:///android_asset/", page.getLayout().getData(), "text/html", "UTF-8", null);

        return convertView;
    }

    private void injectImageByScript(WebView view, String elementId, String picturePath) {
        Log.v("tag", elementId + " " + picturePath);
        view.loadUrl("javascript:(function() {" +
                "var target = document.getElementById('" + elementId + "');" +
                "var img = document.createElement('img');" +
                "img.src = '" + picturePath.replace("file://", "file:///") + "';" +
                "target.appendChild(img);" +
                "})()");
    }

    static private class ViewHolder {
        WebView webView;
    }
}