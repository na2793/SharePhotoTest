package com.study.hancom.sharephototest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.MultipleSelectionGalleryAdapter;
import com.study.hancom.sharephototest.util.ImageUtil;

import java.util.ArrayList;

public class GalleryMultipleSelectionActivity extends AppCompatActivity {

    private ArrayList<String> mPicturePathList;

    private Menu mMenu;

    private GridView mGalleryView;
    private MultipleSelectionGalleryAdapter mGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_picture_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPicturePathList = ImageUtil.getMediaImage(this);
        mGalleryView = (GridView) findViewById(R.id.gallery_image_grid_view);
        mGalleryAdapter = new MultipleSelectionGalleryAdapter(this, mPicturePathList);
        mGalleryAdapter.setOnMultipleItemSelectListener(new MultipleSelectionGalleryAdapter.OnMultipleItemSelectListener() {
            @Override
            public void onSelect() {
                changeActionBar();
                mGalleryAdapter.notifyDataSetChanged();
            }
        });
        mGalleryView.setAdapter(mGalleryAdapter);
    }

    private void changeActionBar() {
        setTitle(String.format(getResources().getString(R.string.title_gallery_main), mGalleryAdapter.getSelectedPositionCount(), mPicturePathList.size()));
        if (mGalleryAdapter.getSelectedPositionCount() > 0) {
            mMenu.findItem(R.id.action_next).setEnabled(true);
        } else {
            mMenu.findItem(R.id.action_next).setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.gallery_multiple_select_main, menu);
        setTitle(String.format(getResources().getString(R.string.title_gallery_main), mGalleryAdapter.getSelectedPositionCount(), mPicturePathList.size()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;

            case R.id.action_next:
                ArrayList<String> selectedPicturePathList = new ArrayList<>();
                Integer[] selectedPositionArray = mGalleryAdapter.getAllSelectedPosition();
                for (int eachPosition : selectedPositionArray) {
                    selectedPicturePathList.add(mGalleryAdapter.getItem(eachPosition));
                }
                Intent intent = new Intent(getApplicationContext(), AlbumOverviewActivity.class);
                intent.putExtra("selectedPicturePathList", selectedPicturePathList);
                startActivity(intent);

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
                ArrayList<Integer> selectedPicturePositionList = data.getExtras().getIntegerArrayList("selectedPicturePositionList");

                mGalleryAdapter.deselectAll();
                for (Integer eachSelectedPicturePosition : selectedPicturePositionList) {
                    mGalleryAdapter.addSelectedPosition(eachSelectedPicturePosition);
                }
                mGalleryAdapter.notifyDataSetChanged();

                changeActionBar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}