package com.study.hancom.sharephototest.activity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.FileUtil;
import com.study.hancom.sharephototest.util.WebViewUtil;

import java.io.IOException;
import java.io.InputStream;

public class AlbumFullSizeWebViewActivity extends AppCompatActivity {
    private Album mAlbum;
    private int mCurrentPageIndex;

    private String mDefaultHTMLData;
    private WebView mWebView;

    private Button mButtonPrevious;
    private Button mButtonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_editor_page_full_size_webview_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(getResources().getString(R.string.epubData_fileName_default_html));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDefaultHTMLData = FileUtil.fileToString(inputStream);

        /* 인텐트 데이터 처리 */
        Bundle bundle = getIntent().getExtras();
        mAlbum = bundle.getParcelable("album");
        mCurrentPageIndex = bundle.getInt("pageIndex");

        /* 뷰 처리 */
        mWebView = (WebView) findViewById(R.id.show_webview);

        // Add a WebViewClient
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectAll(mCurrentPageIndex, view);
            }
        });

        mWebView.loadDataWithBaseURL("file:///android_asset/", mDefaultHTMLData, "text/html", "UTF-8", null);

        /* 버튼 처리 */
        mButtonPrevious = (Button) findViewById(R.id.button_previous);
        mButtonNext = (Button) findViewById(R.id.button_next);
        setButton();

        mButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPageIndex--;
                setButton();
                injectAll(mCurrentPageIndex, mWebView);
            }
        });
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPageIndex++;
                setButton();
                injectAll(mCurrentPageIndex, mWebView);
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
