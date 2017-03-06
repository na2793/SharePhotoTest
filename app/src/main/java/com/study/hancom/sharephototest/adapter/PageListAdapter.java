package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
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

    private boolean mLoadingFinished = true;
    private boolean mRedirect = false;

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
        final Page page = mAlbum.getPage(position);

        // Enable Javascript
        holder.webView.getSettings().setJavaScriptEnabled(true);
        holder.webView.getSettings().setLoadWithOverviewMode(true);
        holder.webView.getSettings().setUseWideViewPort(true);
        holder.webView.setHorizontalScrollBarEnabled(false);
        holder.webView.setVerticalScrollBarEnabled(false);
        holder.webView.getSettings().setBuiltInZoomControls(false);
        holder.webView.getSettings().setSupportZoom(false);
        holder.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        holder.webView.setInitialScale(1);

        // Add a WebViewClient
        holder.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (!mLoadingFinished) {
                    mRedirect = true;
                }
                mLoadingFinished = false;
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mLoadingFinished = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(!mRedirect){
                    mLoadingFinished = true;
                }

                if(mLoadingFinished && !mRedirect){
                    // inject data
                    for (int i = 0 ; i < page.getPictureCount() ; i++) {
                        mWebViewUtil.injectStyleByScript(view, page.getLayout().getStylePath());
                        Picture eachPicture = page.getPicture(i);
                        if (eachPicture != null) {
                            mWebViewUtil.injectImageByScript(view, "_" + (i + 1), eachPicture.getPath());
                        }
                    }

                    mWebViewUtil.setA4SizeByHeight(view, view.getHeight());
                } else{
                    mRedirect = false;
                }
            }
        });

        holder.webView.loadUrl("file://" + page.getLayout().getFramePath());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        WebView webView;

        ViewHolder(View itemView) {
            super(itemView);
            webView = (WebView) itemView.findViewById(R.id.page_list_item_webview);
        }
    }
}