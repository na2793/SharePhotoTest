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
import android.widget.RelativeLayout;

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

    private boolean mLoadingFinished = true;
    private boolean mRedirect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_editor_page_full_size_webview_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* 인텐트 데이터 처리 */
        parseIntentData();

        /* 뷰 처리 */
        mWebView = (WebView) findViewById(R.id.show_webview);
        setWebView();

    }

    public void parseIntentData() {
        Bundle bundle = getIntent().getExtras();
        mAlbum = bundle.getParcelable("album");
        mCurrentPageIndex = bundle.getInt("pageIndex");
    }

    private void setWebView(){
        final Page page = mAlbum.getPage(mCurrentPageIndex);

        // Add a WebViewClient
        mWebView.setWebViewClient(new WebViewClient() {
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
                } else{
                    mRedirect = false;
                }
            }
        });

        mWebView.loadUrl("file://" + page.getLayout().getFramePath());
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
