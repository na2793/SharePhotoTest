package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.AlbumEditorPageFullSizeWebViewActivity;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.MathUtil;
import com.study.hancom.sharephototest.util.WebViewUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlbumGridAdapter extends RecyclerView.Adapter<AlbumGridAdapter.ViewHolder> {

    private Context mContext;
    private Album mAlbum;

    private Set<Integer> mPinnedPositionSet = new HashSet<>();
    private boolean mFirstLoading = true;

    private WebViewUtil mWebViewUtil = new WebViewUtil();

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
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

        /* 이미지뷰 처리 */
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AlbumEditorPageFullSizeWebViewActivity.class);

                Bundle bundle = new Bundle();
                bundle.putParcelable("album", mAlbum);
                bundle.putInt("pageIndex", position);
                intent.putExtras(bundle);

                mContext.startActivity(intent);
            }
        });

       /* 웹뷰 처리 */
        holder.webView.setWebViewClient(new WebViewClient() {
            boolean loadingFinished = true;
            boolean redirect = false;

            long last_page_start;
            long now;

            // Load the url
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadingFinished = false;
                last_page_start = System.nanoTime();
                show_splash();
            }

            // When finish loading page
            public void onPageFinished(final WebView view, String url) {
                injectAll(position, view);
                mFirstLoading = false;
                if (!redirect) {
                    loadingFinished = true;
                }
                //call remove_splash in 500 miSec
                if (loadingFinished && !redirect) {
                    now = System.nanoTime();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    remove_splash();
                                }
                            },
                            3000);
                } else {
                    redirect = false;
                }
            }

            private void show_splash() {
                if (holder.webView.getVisibility() == View.VISIBLE) {
                    holder.webView.setVisibility(View.VISIBLE);
                    holder.imageView.setVisibility(View.GONE);
                }
            }

            //if a new "page start" was fired dont remove splash screen
            private void remove_splash() {
                if (last_page_start < now) {
                    holder.webView.setVisibility(View.GONE);
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.imageView.setImageBitmap(convertWebviewToBitmap(holder.webView));
                }
            }
        });

        if (!mFirstLoading) {
            injectAll(position, holder.webView);
        }
    }

    public List<Integer> getPinnedPositionAll() {
        return new ArrayList<>(mPinnedPositionSet);
    }

    private void injectAll(int position, WebView view) {
        final Page page = mAlbum.getPage(position);
        int pictureCount = page.getPictureCount();
        mWebViewUtil.injectDivByScript(view, pictureCount);
        // inject data
        for (int i = 0; i < pictureCount; i++) {
            mWebViewUtil.injectStyleByScript(view, page.getLayout().getPath());
            Picture eachPicture = page.getPicture(i);
            if (eachPicture != null) {
                mWebViewUtil.injectImageByScript(view, "_" + (i + 1), eachPicture.getPath());
            } else {
                mWebViewUtil.injectImageByScript(view, "_" + (i + 1), "");
            }
        }
    }

    public Bitmap convertWebviewToBitmap(WebView webView) {
        Bitmap bmp = Bitmap.createBitmap(webView.getWidth(), webView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        webView.draw(canvas);
        return bmp;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox;
        ImageView imageView;
        WebView webView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.page_header_text);
            checkBox = (CheckBox) itemView.findViewById(R.id.page_checkbox);
            imageView = (ImageView) itemView.findViewById(R.id.page_image_view);
            webView = (WebView) itemView.findViewById(R.id.page_web_view);
            webView.loadDataWithBaseURL("file:///android_asset/", mWebViewUtil.getDefaultHTMLData(), "text/html", "UTF-8", null);
        }
    }
}
