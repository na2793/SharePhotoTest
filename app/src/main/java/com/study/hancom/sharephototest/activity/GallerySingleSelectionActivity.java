package com.study.hancom.sharephototest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.MultipleSelectionGalleryAdapter;
import com.study.hancom.sharephototest.adapter.SingleSelectionGalleryAdapter;
import com.study.hancom.sharephototest.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class GallerySingleSelectionActivity extends AppCompatActivity {
    static final String STATE_PICTURE_PATH_LIST = "picturePathList";
    static final String STATE_INVALID_PICTURE_PATH_LIST = "invalidPicturePathList";

    private ArrayList<String> mPicturePathList;
    private ArrayList<String> mInvalidPicturePathList;

    private GridView mGalleryView;
    private SingleSelectionGalleryAdapter mGalleryAdapter;

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_picture_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mPicturePathList = savedInstanceState.getStringArrayList(STATE_PICTURE_PATH_LIST);
            mInvalidPicturePathList = savedInstanceState.getStringArrayList(STATE_INVALID_PICTURE_PATH_LIST);
        } else {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            mPicturePathList = ImageUtil.getMediaImage(this);
            mInvalidPicturePathList = bundle.getStringArrayList("InvalidPicturePathList");
        }

        mGalleryView = (GridView) findViewById(R.id.gallery_image_grid_view);
        mGalleryAdapter = new SingleSelectionGalleryAdapter(this, mPicturePathList);
        mGalleryAdapter.setInvalidPicturePathList(mInvalidPicturePathList);
        mGalleryAdapter.setOnSingleItemSelectListener(new SingleSelectionGalleryAdapter.OnSingleItemSelectListener() {
            @Override
            public void onSelect(int position) {
                mGalleryAdapter.setSelectedPosition(position);
                changeActionBar();
                mGalleryAdapter.notifyDataSetChanged();
            }
        });
        mGalleryView.setAdapter(mGalleryAdapter);

    }

    private void changeActionBar() {
        if (mGalleryAdapter.getSelectedPosition() > 0) {
            mMenu.findItem(R.id.action_gallery_confirm).setEnabled(true);
        } else {
            mMenu.findItem(R.id.action_gallery_confirm).setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;

        getMenuInflater().inflate(R.menu.gallery_single_select_main, menu);
        setTitle(getResources().getString(R.string.title_album_editor_single_select));

        changeActionBar();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            try {
                Bundle bundle = data.getExtras();
                if (data.hasExtra("selectedImage")) {
                    mGalleryAdapter.setSelectedPosition(bundle.getInt("selectedImage"));
                }
                ArrayList<String> selectedPicturePathList = bundle.getStringArrayList("InvalidPicturePathList");
                mGalleryAdapter.deselectAll();
                for (String eachSelectedPicturePosition : selectedPicturePathList) {
                    mGalleryAdapter.addSelectedPosition(eachSelectedPicturePosition);
                }
                mGalleryAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(STATE_PICTURE_PATH_LIST, mPicturePathList);
        outState.putStringArrayList(STATE_INVALID_PICTURE_PATH_LIST, mInvalidPicturePathList);
        super.onSaveInstanceState(outState);
    }
}