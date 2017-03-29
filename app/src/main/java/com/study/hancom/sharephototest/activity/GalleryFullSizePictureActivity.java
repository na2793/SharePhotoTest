package com.study.hancom.sharephototest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.study.hancom.sharephototest.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GalleryFullSizePictureActivity extends AppCompatActivity {
    private ArrayList<String> mPicturePathList;
    private Set<Integer> mSelectedIndexSet;
    private int mCurrentPictureIndex;

    private boolean mIsMultipleSelection;
    private CheckBox mCheckBox;
    private ImageView mImageView;
    private Button mButtonPrevious;
    private Button mButtonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_full_size_picture_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* 데이터 파싱 */
        Bundle bundle = getIntent().getExtras();
        mIsMultipleSelection = bundle.getBoolean("isMultipleSelection");
        mPicturePathList = bundle.getStringArrayList("picturePathList");
        mCurrentPictureIndex = bundle.getInt("currentPictureIndex");
        if (mIsMultipleSelection) {
            mSelectedIndexSet = new HashSet<>(bundle.getIntegerArrayList("selectedPicturePositionList"));
        }

        /* 이미지뷰 처리 */
        mImageView = (ImageView) findViewById(R.id.show_image_view);
        setImageView();

        /* 버튼 처리 */
        mButtonPrevious = (Button) findViewById(R.id.button_previous);
        mButtonNext = (Button) findViewById(R.id.button_next);
        setButton();

        mButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPictureIndex--;
                setImageView();
                setButton();
                changeActionBar();
            }
        });
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPictureIndex++;
                setImageView();
                setButton();
                changeActionBar();
            }
        });

    }

    private void setImageView() {
        String picturePath = mPicturePathList.get(mCurrentPictureIndex);
        Glide.with(this).load(picturePath).override(1000, 1000).fitCenter().into(mImageView);
    }

    private void setButton() {
        int maxIndex = mPicturePathList.size() - 1;
        if (mCurrentPictureIndex <= 0) {
            mButtonPrevious.setVisibility(View.GONE);
        } else {
            mButtonPrevious.setVisibility(View.VISIBLE);
        }
        if (maxIndex <= mCurrentPictureIndex) {
            mButtonNext.setVisibility(View.GONE);
        } else {
            mButtonNext.setVisibility(View.VISIBLE);
        }
    }

    private void changeActionBar() {
        if (mIsMultipleSelection) {
            setTitle(String.format(getResources().getString(R.string.title_gallery_main), mSelectedIndexSet.size(), mPicturePathList.size()));
            if (mSelectedIndexSet.contains(mCurrentPictureIndex)) {
                mCheckBox.setChecked(true);
            } else {
                mCheckBox.setChecked(false);
            }
        } else {
            setTitle(getResources().getString(R.string.title_gallery_single_full_size_main));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_full_size_picture_main, menu);
        if (!mIsMultipleSelection) {
            MenuItem registrar = menu.findItem(R.id.action_check);
            registrar.setVisible(false);
        } else {
            mCheckBox = (CheckBox) menu.findItem(R.id.action_check).getActionView();
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mSelectedIndexSet.remove(mCurrentPictureIndex)) {
                        mSelectedIndexSet.add(mCurrentPictureIndex);
                    }
                    changeActionBar();
                }
            });
        }
        changeActionBar();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mIsMultipleSelection) {
                    Intent intent = new Intent(getApplicationContext(), GalleryMultipleSelectionActivity.class);
                    intent.putIntegerArrayListExtra("selectedPicturePositionList", new ArrayList<>(mSelectedIndexSet));
                    setResult(RESULT_OK, intent);
                }
                finish();
                return true;

            case R.id.action_check:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsMultipleSelection) {
            Intent intent = new Intent(getApplicationContext(), GalleryMultipleSelectionActivity.class);
            intent.putIntegerArrayListExtra("selectedPicturePositionList", new ArrayList<>(mSelectedIndexSet));
            setResult(RESULT_OK, intent);
        }
        finish();
    }
}