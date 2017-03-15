package com.study.hancom.sharephototest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.GalleryFullSizePictureActivity;

import java.util.ArrayList;

public class SingleSelectionGalleryAdapter extends com.study.hancom.sharephototest.adapter.base.GalleryAdapter {
    public static final int REQUEST_CODE = 1;

    private int mSelectedPosition = -1;
    private ArrayList<String> mInvalidPicturePathList;

    public SingleSelectionGalleryAdapter(Context context, ArrayList<String> picturePathList) {
        super(context, picturePathList);
    }

    public void setInvalidPicturePathList(ArrayList<String> invalidPicturePathList) {
        mInvalidPicturePathList = invalidPicturePathList;
    }

    public int getSelectedPosition(){
        return mSelectedPosition;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        ImageView imageView = (ImageView) view.findViewById(R.id.gallery_image);
        Button button = (Button) view.findViewById(R.id.show_clicked_Image);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.gallery_selected_image);

        if (mInvalidPicturePathList != null) {
            if (mInvalidPicturePathList.contains(getItem(position))) {
                imageView.setAlpha(0.2f);
                checkBox.setEnabled(false);
                checkBox.setChecked(false);
                checkBox.setVisibility(View.INVISIBLE);
            } else {
               imageView.setAlpha(1.0f);
                if (mSelectedPosition == position ) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
            }
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mInvalidPicturePathList.contains(getItem(position))) {
                    mSelectedPosition = position;
                    notifyDataSetChanged();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GalleryFullSizePictureActivity.class);
                intent.putStringArrayListExtra("picturePathList", mPicturePathList);
                intent.putExtra("currentPictureIndex", position);
                ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return view;
    }
}
