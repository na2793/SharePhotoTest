package com.study.hancom.sharephototest.adapter;

import android.app.Activity;
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

import com.bumptech.glide.Glide;
import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.GalleryFullSizePictureActivity;

import java.util.List;

public class GalleryAdapter extends BaseAdapter {

    public static final int MENU_MODE_SINGLE_SELECT = 0;
    public static final int MENU_MODE_MULTIPLE_SELECT = 1;

    private static String TAG = GalleryAdapter.class.getName();

    private Context mContext;
    private int mMode;
    private List<String> mGalleryPicturePaths;
    private List<String> mMultipleSelectedPicturePaths;
    private String mSingleSelectedPicturePath;

    private GalleryPictureHolder galleryPictureHolder;

    private OnMultipleItemSelectListener mOnMultipleItemSelectListener;

    public GalleryAdapter(Context context, List<String> galleryPicturePaths, List<String> multipleSelectedPicturePaths, int mode) {
        this.mContext = context;
        this.mGalleryPicturePaths = galleryPicturePaths;
        this.mMultipleSelectedPicturePaths = multipleSelectedPicturePaths;
        this.mMode = mode;

    }

    public GalleryAdapter(Context context, List<String> galleryPicturePaths, List<String> multipleSelectedPicturePaths, String singleSelectedPicturePath, int mode) {
        this.mContext = context;
        this.mGalleryPicturePaths = galleryPicturePaths;
        this.mMultipleSelectedPicturePaths = multipleSelectedPicturePaths;
        this.mSingleSelectedPicturePath = singleSelectedPicturePath;
        this.mMode = mode;

    }


    @Override
    public int getCount() {
        Log.v(TAG, "data.size() ---> " + String.valueOf(mGalleryPicturePaths.size()));
        return mGalleryPicturePaths.size();
    }

    @Override
    public String getItem(int position) {
        return mGalleryPicturePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            galleryPictureHolder = new GalleryPictureHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gallery_picture_grid_item, parent, false);

            galleryPictureHolder.imageView = (ImageView) convertView.findViewById(R.id.gallery_image);
            galleryPictureHolder.button = (Button) convertView.findViewById(R.id.show_clicked_Image);
            galleryPictureHolder.checkBox = (CheckBox) convertView.findViewById(R.id.gallery_selected_image);

            convertView.setTag(galleryPictureHolder);

        } else {
            galleryPictureHolder = (GalleryPictureHolder) convertView.getTag();
        }

        boolean isCheck = false;
        for (int i = 0; i < mMultipleSelectedPicturePaths.size(); i++) {
            if (mMultipleSelectedPicturePaths.get(i).contains(mGalleryPicturePaths.get(position))) {
                isCheck = true;
            }
        }


        if (mMode == MENU_MODE_MULTIPLE_SELECT) {
            if (isCheck) {
                galleryPictureHolder.checkBox.setEnabled(true);
                galleryPictureHolder.checkBox.setChecked(true);
                galleryPictureHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                galleryPictureHolder.checkBox.setEnabled(false);
                galleryPictureHolder.checkBox.setChecked(false);
                galleryPictureHolder.checkBox.setVisibility(View.INVISIBLE);
            }
        } else if (mMode == MENU_MODE_SINGLE_SELECT) {
            if (isCheck) {
                galleryPictureHolder.imageView.setAlpha(0.2f);
                galleryPictureHolder.checkBox.setEnabled(false);
                galleryPictureHolder.checkBox.setVisibility(View.INVISIBLE);
            } else {
                galleryPictureHolder.imageView.setAlpha(1.0f);
                galleryPictureHolder.checkBox.setEnabled(false);
                galleryPictureHolder.checkBox.setChecked(false);
                galleryPictureHolder.checkBox.setVisibility(View.INVISIBLE);
            }
            if (mSingleSelectedPicturePath != null) {
                if (mGalleryPicturePaths.get(position).equals(mSingleSelectedPicturePath)) {
                    galleryPictureHolder.checkBox.setEnabled(true);
                    galleryPictureHolder.checkBox.setChecked(true);
                    galleryPictureHolder.checkBox.setVisibility(View.VISIBLE);
                }
            }
        }

        final String ImagePath = mGalleryPicturePaths.get(position);
        Glide
                .with(mContext)
                .load(ImagePath)
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.place_holder)
                .into(galleryPictureHolder.imageView);

        galleryPictureHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMode == MENU_MODE_MULTIPLE_SELECT) {
                    boolean isSelectedBefore = false;
                    if (mMultipleSelectedPicturePaths.size() != 0) {
                        for (int i = 0; i < mMultipleSelectedPicturePaths.size(); i++) {
                            if (getItem(position).equals(mMultipleSelectedPicturePaths.get(i))) {
                                isSelectedBefore = true;

                                mMultipleSelectedPicturePaths.remove(i);
                                galleryPictureHolder.checkBox.setEnabled(false);
                                galleryPictureHolder.checkBox.setVisibility(View.INVISIBLE);
                                break;
                            }
                        }
                    }
                    if (!isSelectedBefore) {
                        mMultipleSelectedPicturePaths.add(getItem(position));
                    }
                    mOnMultipleItemSelectListener.onSelect();
                } else if (mMode == MENU_MODE_SINGLE_SELECT) {
                    boolean isSelectedBefore = false;
                    if (mMultipleSelectedPicturePaths.size() != 0) {
                        for (int i = 0; i < mMultipleSelectedPicturePaths.size(); i++) {
                            if (getItem(position).equals(mMultipleSelectedPicturePaths.get(i))) {
                                isSelectedBefore = true;
                                mSingleSelectedPicturePath = null;
                                break;
                            }
                        }
                    }
                    if (!isSelectedBefore || mMultipleSelectedPicturePaths.size() == 0) {
                        mSingleSelectedPicturePath = getItem(position);
                    }
                }
                notifyDataSetChanged();
            }
        });

        if (mMode == MENU_MODE_MULTIPLE_SELECT) {
            galleryPictureHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Click ---> " + String.valueOf(position));
                    Intent intent = new Intent(mContext, GalleryFullSizePictureActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("ImagePath", "file:/" + ImagePath);
                    intent.putExtras(bundle);
                    ((Activity) mContext).startActivityForResult(intent, 0);
                }
            });
        }

        return convertView;
    }

    public String getSelectedPath() {

        return mSingleSelectedPicturePath;
    }

    public void setOnMultipleItemSelectListener(OnMultipleItemSelectListener listener) {
        mOnMultipleItemSelectListener = listener;
    }

    public interface OnMultipleItemSelectListener {
        void onSelect();
    }

    private static class GalleryPictureHolder {
        ImageView imageView;
        Button button;
        CheckBox checkBox;
    }
}