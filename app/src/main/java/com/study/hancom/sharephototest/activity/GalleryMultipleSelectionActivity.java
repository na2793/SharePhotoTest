package com.study.hancom.sharephototest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.GalleryAdapter;
import com.study.hancom.sharephototest.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class GalleryMultipleSelectionActivity extends AppCompatActivity {

    private List<String> mGalleryPicturePaths = new ArrayList<>();
    private ArrayList<String> mSelectedPicturePaths = new ArrayList<>();
    private GalleryAdapter mGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_picture_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridView gridView = (GridView) findViewById(R.id.gallery_image_grid_view);
        mGalleryPicturePaths = ImageUtil.getMediaImage(this);
        mGalleryAdapter = new GalleryAdapter(this, mGalleryPicturePaths, mSelectedPicturePaths, GalleryAdapter.MENU_MODE_MULTIPLE_SELECT);
        gridView.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setOnMultipleItemSelectListener(new GalleryAdapter.OnMultipleItemSelectListener() {
            @Override
            public void onSelect() {
                setTitle(String.format(getResources().getString(R.string.title_gallery_main), mSelectedPicturePaths.size(), mGalleryPicturePaths.size()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gallery_multiple_select_main, menu);
        setTitle(String.format(getResources().getString(R.string.title_gallery_main), mSelectedPicturePaths.size(), mGalleryPicturePaths.size()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.action_next:
                Intent intent = new Intent(getApplicationContext(), AlbumOverviewActivity.class);
                intent.putExtra("AlbumElementPaths", mSelectedPicturePaths);
                Log.v("tag", mSelectedPicturePaths.size() + " gallery");
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
                Bundle bundle = data.getExtras();
                String selectedImagePath = bundle.getString("selectedImage");
                if (selectedImagePath != null) {
                    mSelectedPicturePaths.add(selectedImagePath);
                    setTitle(String.format(getResources().getString(R.string.title_gallery_main), mSelectedPicturePaths.size(), mGalleryPicturePaths.size()));
                    mGalleryAdapter.notifyDataSetInvalidated();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}