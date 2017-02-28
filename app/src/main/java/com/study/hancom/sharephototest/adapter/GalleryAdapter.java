package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.GalleryFullSizePictureActivity;
import com.study.hancom.sharephototest.util.ImageUtil;

import java.util.List;

public class GalleryAdapter extends BaseAdapter {

    private static String TAG = GalleryAdapter.class.getName();
    private static ImageLoader imageLoader;

    private Context context;
    private List<String> galleryPicturePaths;
    private List<String> checkedPicturePaths;

    private GalleryPictureHolder galleryPictureHolder;

    public GalleryAdapter(Context context, List<String> galleryPicturePaths, List<String> checkedPicturePaths) {
        this.context = context;
        this.galleryPicturePaths = galleryPicturePaths;
        this.checkedPicturePaths = checkedPicturePaths;

        imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
    }

    @Override
    public int getCount() {
        Log.v(TAG, "data.size() ---> " + String.valueOf(galleryPicturePaths.size()));
        return galleryPicturePaths.size();
    }

    @Override
    public String getItem(int position) {
        return galleryPicturePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            galleryPictureHolder = new GalleryPictureHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.gallery_picture_grid_item, parent, false);

            galleryPictureHolder.imageView = (ImageView) convertView.findViewById(R.id.gallery_image);
            galleryPictureHolder.button = (Button) convertView.findViewById(R.id.show_clicked_Image);
            galleryPictureHolder.checkBox = (CheckBox) convertView.findViewById(R.id.gallery_selected_image);

            convertView.setTag(galleryPictureHolder);

        } else {
            galleryPictureHolder = (GalleryPictureHolder) convertView.getTag();
        }

        boolean isCheck = false;
        for (int i = 0; i < checkedPicturePaths.size(); i++) {
            if (checkedPicturePaths.get(i).equals(galleryPicturePaths.get(position))) {
                isCheck = true;
            }
        }
        if (isCheck) {
            galleryPictureHolder.checkBox.setEnabled(true);
            galleryPictureHolder.checkBox.setChecked(true);
            galleryPictureHolder.checkBox.setVisibility(View.VISIBLE);
        } else {
            galleryPictureHolder.checkBox.setEnabled(false);
            galleryPictureHolder.checkBox.setVisibility(View.INVISIBLE);
        }

        final String ImagePath = "file://" + galleryPicturePaths.get(position);
        imageLoader.displayImage(ImagePath, galleryPictureHolder.imageView, ImageUtil.options);

        galleryPictureHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Click ---> " + String.valueOf(position));

                boolean isSelectedBefore = false;
                if (checkedPicturePaths.size() != 0) {
                    for (int i = 0; i < checkedPicturePaths.size(); i++) {
                        if (getItem(position).equals(checkedPicturePaths.get(i))) {
                            isSelectedBefore = true;
                            checkedPicturePaths.remove(i);
                            galleryPictureHolder.checkBox.setEnabled(false);
                            galleryPictureHolder.checkBox.setVisibility(View.INVISIBLE);
                            break;
                        }
                    }
                }
                if (!isSelectedBefore) {
                    checkedPicturePaths.add(getItem(position));
                }
                notifyDataSetChanged();
            }
        });

        galleryPictureHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Click ---> " + String.valueOf(position));
                Intent intent = new Intent(context, GalleryFullSizePictureActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("ImagePath", ImagePath);

                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private static class GalleryPictureHolder {
        ImageView imageView;
        Button button;
        CheckBox checkBox;
    }
}