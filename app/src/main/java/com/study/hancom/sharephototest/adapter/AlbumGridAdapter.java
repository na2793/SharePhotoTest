package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.TextView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.AlbumFullSizeWebViewActivity;
import com.study.hancom.sharephototest.adapter.base.RecyclerClickableItemAdapter;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.FileUtil;
import com.study.hancom.sharephototest.util.WebViewUtil;
import com.study.hancom.sharephototest.view.HDSizeWebView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AlbumGridAdapter extends RecyclerClickableItemAdapter<AlbumGridAdapter.ViewHolder> {

    private Context mContext;
    private Album mAlbum;

    private Set<Integer> mPinnedPositionSet = new HashSet<>();
    private Set<View> mWebViewSet = new HashSet<>();

    public AlbumGridAdapter(Context context, Album album) {
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
        final View view = LayoutInflater.from(mContext).inflate(R.layout.album_overview_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        /* 텍스트뷰 처리 */
        String pageNum = Integer.toString(position + 1) + "페이지";
        holder.textView.setText(pageNum);

        /* 체크박스 처리 */
        if (mPinnedPositionSet.contains(position)) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        final CheckBox checkBox = holder.checkBox;
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPinnedPositionSet.remove(position)) {
                    mPinnedPositionSet.add(position);
                }
            }
        });

       /* 웹뷰 처리 */
        final GestureDetector webViewGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Intent intent = new Intent(mContext, AlbumFullSizeWebViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("album", mAlbum);
                bundle.putInt("pageIndex", position);
                intent.putExtras(bundle);
                mContext.startActivity(intent);

                return false;
            }
        });

        holder.webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return webViewGestureDetector.onTouchEvent(event);
            }
        });

        // Add a WebViewClient
        holder.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectAll(position, view);
                mWebViewSet.add(view);
            }
        });

        if (mWebViewSet.contains(holder.webView)) {
            injectAll(position, holder.webView);
        }
    }

    public ArrayList<Integer> getPinnedPositionAll() {
        return new ArrayList<>(mPinnedPositionSet);
    }

    public void addPinnedPosition(int position) {
        mPinnedPositionSet.add(position);
    }

    public boolean removePinnedPosition(int position) {
        return mPinnedPositionSet.remove(position);
    }

    private void injectAll(int position, WebView view) {
        final Page page = mAlbum.getPage(position);
        int pictureCount = page.getPictureCount();
        WebViewUtil.injectDivByScript(view, pictureCount);
        // inject data
        for (int i = 0; i < pictureCount; i++) {
            WebViewUtil.injectStyleByScript(view, page.getLayout().getPath());
            Picture eachPicture = page.getPicture(i);
            if (eachPicture != null) {
                WebViewUtil.injectImageByScript(view, "_" + (i + 1), eachPicture.getPath());
            } else {
                WebViewUtil.injectImageByScript(view, "_" + (i + 1), "");
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        WebView webView;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.page_header_text);
            checkBox = (CheckBox) itemView.findViewById(R.id.page_checkbox);
            webView = (WebView) itemView.findViewById(R.id.page_web_view);
            webView.loadDataWithBaseURL("file:///android_asset/", WebViewUtil.getDefaultHTMLData(mContext), "text/html", "UTF-8", null);
        }
    }
}
