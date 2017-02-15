package com.study.hancom.sharephototest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.GalleryAdapter;
import com.study.hancom.sharephototest.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private List<String> galleryPicturePaths = new ArrayList<>();
    private List<String> selectedPicturePaths = new ArrayList<>();
    private ArrayList<String> selectedPictures = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_picture_main);
        init();
    }

    private void init() {
        GridView gridView = (GridView) findViewById(R.id.gallery_image_grid_view);
        Button ConfirmBtn = (Button) findViewById(R.id.confirm);

        galleryPicturePaths = ImageUtil.getMediaImage(this);
        GalleryAdapter myGalleryAdapter = new GalleryAdapter(this, galleryPicturePaths, selectedPicturePaths);
        gridView.setAdapter(myGalleryAdapter);

        ConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // @임시 뒤로 가기 버튼으로 다시 수행될 때
                if (selectedPictures != null) {
                    selectedPictures.clear();
                }

                for (int i = 0; i < selectedPicturePaths.size(); i++) {
                    selectedPictures.add(selectedPicturePaths.get(i));
                }

                //** 임시 인텐트 위치
                Intent intent = new Intent(getApplicationContext(), PageEditorActivity.class);
                intent.putExtra("selectedImage", selectedPictures);
                startActivity(intent);
            }
        });
    }
}