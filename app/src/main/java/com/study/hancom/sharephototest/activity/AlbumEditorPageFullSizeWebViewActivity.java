package com.study.hancom.sharephototest.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.WebViewUtil;

public class AlbumEditorPageFullSizeWebViewActivity extends AppCompatActivity {
    private Album mAlbum;
    private int mCurrentPageIndex;

    private WebViewUtil mWebViewUtil = new WebViewUtil();

    private WebView mWebView;
    private Button mButtonPrevious;
    private Button mButtonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_editor_page_full_size_webview_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* 인텐트 데이터 처리 */
        parseIntentData();

        /* 웹뷰 처리 */
        mWebView = (WebView) findViewById(R.id.show_webview);
        setWebView();

        /* 버튼 처리 */
        mButtonPrevious = (Button) findViewById(R.id.button_previous);
        mButtonNext = (Button) findViewById(R.id.button_next);
        setButton();

        mButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPageIndex--;
                setButton();
                setWebView();
            }
        });
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPageIndex++;
                setButton();
                setWebView();
            }
        });
    }

    public void parseIntentData() {
        Bundle bundle = getIntent().getExtras();
        mAlbum = bundle.getParcelable("album");
        mCurrentPageIndex = bundle.getInt("pageIndex");
    }

    private void setButton() {
        int maxIndex = mAlbum.getPageCount() - 1;
        if (0 >= mCurrentPageIndex) {
            mButtonPrevious.setVisibility(View.GONE);
        } else {
            mButtonPrevious.setVisibility(View.VISIBLE);
        }
        if (mCurrentPageIndex >= maxIndex) {
            mButtonNext.setVisibility(View.GONE);
        } else {
            mButtonNext.setVisibility(View.VISIBLE);
        }
    }

    private void setWebView() {
        // Add a WebViewClient
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                    injectAll(mCurrentPageIndex, view);
            }
        });

        mWebView.loadDataWithBaseURL("file:///android_asset/", mWebViewUtil.getDefaultHTMLData(), "text/html", "UTF-8", null);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater();
        setTitle(R.string.title_album_editor_page_full_size_webview_main);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
