package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.WebViewUtil;

public class PageListAdapter extends RecyclerView.Adapter<PageListAdapter.ViewHolder> {

    private Context mContext;
    private Album mAlbum;

    private WebViewUtil mWebViewUtil = new WebViewUtil();

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
        final View view = LayoutInflater.from(mContext).inflate(R.layout.album_editor_page_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // Add a WebViewClient
        holder.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectAll(position, view);
            }
        });

        injectAll(position, holder.webView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        WebView webView;

        ViewHolder(View itemView) {
            super(itemView);
            webView = (WebView) itemView.findViewById(R.id.page_list_item_webview);
            webView.loadDataWithBaseURL("file:///android_asset/", mWebViewUtil.getDefaultHTMLData(), "text/html", "UTF-8", null);
        }
    }

    private void injectAll(int position, WebView view) {
        final Page page = mAlbum.getPage(position);
        int pictureCount = page.getPictureCount();
        mWebViewUtil.injectDivByScript(view, pictureCount);
        // inject data
        for (int i = 0; i < pictureCount; i++) {
            mWebViewUtil.injectStyleByScript(view, page.getLayout().getStylePath());
            Picture eachPicture = page.getPicture(i);
            if (eachPicture != null) {
                mWebViewUtil.injectImageByScript(view, "_" + (i + 1), eachPicture.getPath());
            } else {
                mWebViewUtil.injectImageByScript(view, "_" + (i + 1), "");
            }
        }
    }
}