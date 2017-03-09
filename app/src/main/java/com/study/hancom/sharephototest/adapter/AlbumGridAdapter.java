package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.AlbumEditorPageFullSizeWebViewActivity;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.WebViewUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlbumGridAdapter extends BaseAdapter {
    private Context mContext;
    private Album mAlbum;

    private WebViewUtil mWebViewUtil = new WebViewUtil();

    private boolean mLoadingFinished = true;
    private boolean mRedirect = false;

    private Set<Integer> mPinnedPositionSet = new HashSet<>();

    public AlbumGridAdapter(Context context, Album album) {
        this.mContext = context;
        this.mAlbum = album;
    }

    @Override
    public int getCount() {
        return mAlbum.getPageCount();
    }

    @Override
    public Page getItem(int position) {
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

        /* 체크박스 처리 */
        if (mPinnedPositionSet.contains(position)) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }

        final CheckBox checkBox = viewHolder.checkBox;
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPinnedPositionSet.remove(position)) {
                    mPinnedPositionSet.add(position);
                }
            }
        });

        /* 웹뷰 처리 */
        // webview에 onClickListener가 적용되지 않아 이런 식으로 처리
        final GestureDetector webViewGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Intent intent = new Intent(mContext, AlbumEditorPageFullSizeWebViewActivity.class);

                Bundle bundle = new Bundle();
                bundle.putParcelable("album", mAlbum);
                bundle.putInt("pageIndex", position);
                intent.putExtras(bundle);

                mContext.startActivity(intent);

                return false;
            }
        });

        final Page page = mAlbum.getPage(position);

        viewHolder.webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return webViewGestureDetector.onTouchEvent(event);
            }
        });

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
                } else {
                    mRedirect = false;
                }
            }
        });

        final String layoutFramePath = "file://" + page.getLayout().getFramePath();
        viewHolder.webView.loadUrl(layoutFramePath);

        return convertView;
    }

    public String getHeaderForSection(int position) {
        return mContext.getResources().getString(R.string.album_overview_section_header, position + 1);
    }

    public void relayout() throws Exception {
        Album backup = mAlbum.clone();

        try {
            List<Picture> pictureList = new ArrayList<>();
            List<Page> pinnedPageList = new ArrayList<>();

            /* 고정 페이지 추출 */
            Integer[] sortedPinnedPositionArray = mPinnedPositionSet.toArray(new Integer[mPinnedPositionSet.size()]);
            Arrays.sort(sortedPinnedPositionArray);

            int offset = 0;
            for (int eachPinnedPosition : sortedPinnedPositionArray) {
                pinnedPageList.add(mAlbum.removePage(eachPinnedPosition - offset));
                offset++;
            }

            /* 모든 사진 추출 및 페이지 삭제 */
            int oldPageCount = mAlbum.getPageCount();
            Log.v("tag", oldPageCount + " ");
            for (int i = 0; i < oldPageCount; i++) {
                Page eachPage = mAlbum.removePage(0);
                for (int j = 0; j < eachPage.getPictureCount(); j++) {
                    Picture eachPicture = eachPage.getPicture(j);
                    if (eachPicture != null) {
                        pictureList.add(eachPicture);
                    }
                }
            }

            /* 새롭게 적재 */
            mAlbum.addPages(pictureList);

            for (int eachPinnedPosition : sortedPinnedPositionArray) {
                int newPageCount = mAlbum.getPageCount();
                if (eachPinnedPosition < newPageCount) {
                    mAlbum.addPage(eachPinnedPosition, pinnedPageList.remove(0));
                } else {
                    mPinnedPositionSet.remove(eachPinnedPosition);
                    mPinnedPositionSet.add(mAlbum.getPageCount());
                    mAlbum.addPage(pinnedPageList.remove(0));
                }
            }
        } catch (Exception e) {
            mAlbum = backup;
            throw e;
        }
    }

    class ViewHolder {
        TextView textView;
        WebView webView;
        CheckBox checkBox;
    }
}
