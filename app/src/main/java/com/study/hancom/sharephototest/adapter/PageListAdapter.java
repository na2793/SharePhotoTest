package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;

public class PageListAdapter extends RecyclerView.Adapter<PageListAdapter.ViewHolder> {

    private Context mContext;
    private Album mAlbum;

    private int parentHeight;

    public PageListAdapter(Context context, Album album){
        mContext = context;
        mAlbum = album;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mAlbum.getPageCount();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.page_editor_page_list_item, parent, false);

        parentHeight = parent.getHeight();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Page page = mAlbum.getPage(position);

        // Enable Javascript
        holder.webView.getSettings().setJavaScriptEnabled(true);
        holder.webView.getSettings().setLoadWithOverviewMode(true);
        holder.webView.getSettings().setUseWideViewPort(true);
        holder.webView.setHorizontalScrollBarEnabled(false);
        holder.webView.setVerticalScrollBarEnabled(false);
        holder.webView.getSettings().setBuiltInZoomControls(false);
        holder.webView.getSettings().setSupportZoom(false);

        // Add a WebViewClient
        holder.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                double realRate = (double)view.getContentHeight() / (double)view.getHeight();
                double realHeight = (double)view.getHeight() * realRate;
                double realWidth = (double)view.getWidth() * realRate;

                Log.v("tag", view.getContentHeight() + " " + view.getHeight() + " " + view.getWidth());

                //view.setLayoutParams(new LinearLayout.LayoutParams((int)realWidth, (int)realHeight));

                for (int i = 0 ; i < page.getPictureCount() ; i++) {
                    injectStyleByScript(view, page.getLayout().getStylePath());
                    injectImageByScript(view, "_" + (i + 1), page.getPicture(i).getPath());
                }
            }
        });

        holder.webView.loadUrl("file://" + page.getLayout().getFramePath());
    }

    private void injectStyleByScript(WebView view, String stylePath) {
        view.loadUrl("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var link = document.createElement('link');" +
                "link.rel = 'stylesheet';" +
                "link.href = '" + stylePath + "';" +
                "parent.appendChild(link)" +
                "})()");
    }

    private void injectImageByScript(WebView view, String elementId, String picturePath) {
        view.loadUrl("javascript:(function() {" +
                "var target = document.getElementById('" + elementId + "');" +
                "var img = document.createElement('img');" +
                "img.src = '" + picturePath + "';" +
                "target.appendChild(img);" +
                "})()");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        WebView webView;

        ViewHolder(View itemView) {
            super(itemView);
            webView = (WebView) itemView.findViewById(R.id.page_list_item_webview);
        }
    }
}