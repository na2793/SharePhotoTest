package com.study.hancom.sharephototest.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.view.TouchImageView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class GalleryFullSizePictureActivity extends AppCompatActivity {

    private ArrayList<String> mGalleryPicturePaths;
    private ArrayList<Integer> mSelectedPicturePositions;
    private int mCurrentPictureIndex;

    private TouchImageView mImageView;
    private Button mButtonPrevious;
    private Button mButtonNext;
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_full_size_picture_main);

        /* 뒤로 가기 버튼 생성 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         /* 데이터 파싱 */
        Bundle bundle = getIntent().getExtras();
        mGalleryPicturePaths = bundle.getStringArrayList("galleryPicturePaths");
        mSelectedPicturePositions = bundle.getIntegerArrayList("selectedPicturePositions");
        mCurrentPictureIndex = bundle.getInt("currentPictureIndex");

        /* 이미지 */
        mImageView = (TouchImageView) findViewById(R.id.show_image_view);

        /* 버튼 처리 */
        mButtonPrevious = (Button) findViewById(R.id.button_previous_picture);
        mButtonNext = (Button) findViewById(R.id.button_next_picture);

        setButton();
        setImage(mGalleryPicturePaths.get(mCurrentPictureIndex));

        mButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPictureIndex--;
                setButton();
                if (mSelectedPicturePositions.indexOf(mCurrentPictureIndex) > -1) {
                    mCheckBox.setChecked(true);
                } else {
                    mCheckBox.setChecked(false);
                }

                mImageView.resetZoom();
                String currentPicturePath = mGalleryPicturePaths.get(mCurrentPictureIndex);
                setImage(currentPicturePath);
            }
        });
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPictureIndex++;
                setButton();
                if (mSelectedPicturePositions.indexOf(mCurrentPictureIndex) > -1) {
                    mCheckBox.setChecked(true);
                } else {
                    mCheckBox.setChecked(false);
                }
                mImageView.resetZoom();
                String currentPicturePath = mGalleryPicturePaths.get(mCurrentPictureIndex);
                setImage(currentPicturePath);
            }
        });
    }

    private void setButton() {
        int maxIndex = mGalleryPicturePaths.size() - 1;
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

    private void setImage(String picturePath) {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            fileInputStream = new FileInputStream(picturePath);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream, null, options);
            mImageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        setTitle(R.string.title_gallery_full_size_picture_main);
        inflater.inflate(R.menu.gallery_full_size_picture_main, menu);

        mCheckBox = (CheckBox) menu.findItem(R.id.action_check).getActionView();
        if (mSelectedPicturePositions.indexOf(mCurrentPictureIndex) > -1) {
            mCheckBox.setChecked(true);
        } else {
            mCheckBox.setChecked(false);
        }
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedPicturePositions.indexOf(mCurrentPictureIndex) > -1) {
                    mSelectedPicturePositions.remove(mCurrentPictureIndex);
                    mCheckBox.setChecked(false);
                } else {
                    mSelectedPicturePositions.add(mCurrentPictureIndex);
                    mCheckBox.setChecked(true);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), GalleryMultipleSelectionActivity.class);
                intent.putIntegerArrayListExtra("selectedPicturePositions", mSelectedPicturePositions);
                setResult(RESULT_OK, intent);
                finish();
                return true;

            case R.id.action_check:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}