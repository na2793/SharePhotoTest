package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.util.WebViewUtil;

public class AlbumGridAdapter extends BaseAdapter {
    private Context mContext;
    private Album mAlbum;

    private WebViewUtil mWebViewUtil = new WebViewUtil();

    private boolean mLoadingFinished = true;
    private boolean mRedirect = false;

    public AlbumGridAdapter(Context context, Album album) {
        this.mContext = context;
        this.mAlbum = album;
    }

    @Override
    public int getCount() {
        return mAlbum.getPageCount();
    }

    @Override
    public Page getItem(int position){
        return mAlbum.getPage(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.album_overview_grid_item, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.page_header_text);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.page_checkbox);
            viewHolder.webView = (WebView) convertView.findViewById(R.id.page_web_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /* 텍스트뷰 처리 */
        String pageNum = getHeaderForSection(position);
        viewHolder.textView.setText(pageNum);

        final Page page = mAlbum.getPage(position);

        // Enable Javascript
        viewHolder.webView.getSettings().setJavaScriptEnabled(true);
        viewHolder.webView.getSettings().setLoadWithOverviewMode(true);
        viewHolder.webView.getSettings().setUseWideViewPort(true);
        viewHolder.webView.setHorizontalScrollBarEnabled(false);
        viewHolder.webView.setVerticalScrollBarEnabled(false);
        viewHolder.webView.getSettings().setBuiltInZoomControls(false);
        viewHolder.webView.getSettings().setSupportZoom(false);
        viewHolder.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        viewHolder.webView.setInitialScale(1);

        // Add a WebViewClient
        viewHolder.webView.setWebViewClient(new WebViewClient() {
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
                if (!mRedirect) {
                    mLoadingFinished = true;
                }

                if (mLoadingFinished && !mRedirect) {
                    // inject data
                    for (int i = 0; i < page.getPictureCount(); i++) {
                        mWebViewUtil.injectStyleByScript(view, page.getLayout().getStylePath());
                        mWebViewUtil.injectImageByScript(view, "_" + (i + 1), page.getPicture(i).getPath());
                    }

                    // setSize
                    mWebViewUtil.setA4SizeByWidth(view, view.getWidth());
                } else {
                    mRedirect = false;
                }
            }
        });

        viewHolder.webView.loadUrl("file://" + page.getLayout().getFramePath());

        return convertView;
    }

    class ViewHolder {
        TextView textView;
        WebView webView;
        CheckBox checkBox;
    }

    public String getHeaderForSection(int position) {
       return mContext.getResources().getString(R.string.album_overview_section_header, position + 1);
    }
}
