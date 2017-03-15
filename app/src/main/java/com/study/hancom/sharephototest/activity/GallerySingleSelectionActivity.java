package com.study.hancom.sharephototest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.SingleSelectionGalleryAdapter;
import com.study.hancom.sharephototest.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class GallerySingleSelectionActivity extends AppCompatActivity {
    
    private ArrayList<String> mPicturePathList;
    private ArrayList<String> mInvalidPicturePathList;

    private GridView mGalleryView;
    private SingleSelectionGalleryAdapter mGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_picture_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* 데이터 파싱 */
        Bundle bundle = getIntent().getExtras();
        mInvalidPicturePathList = bundle.getStringArrayList("InvalidPicturePathList");

        mGalleryView = (GridView) findViewById(R.id.gallery_image_grid_view);
        mPicturePathList = ImageUtil.getMediaImage(this);
        mGalleryAdapter = new SingleSelectionGalleryAdapter(this, mPicturePathList);
        mGalleryAdapter.setInvalidPicturePathList(mInvalidPicturePathList);
        mGalleryView.setAdapter(mGalleryAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_single_select_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.action_gallery_confirm:
                Intent intent = new Intent(getApplicationContext(), AlbumEditorActivity.class);
                intent.putExtra("selectedImage", mGalleryAdapter.getItem(mGalleryAdapter.getSelectedPosition()));
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
