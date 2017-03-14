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

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends BaseAdapter {

    public static final int MENU_MODE_SINGLE_SELECT = 0;
    public static final int MENU_MODE_MULTIPLE_SELECT = 1;

    private static String TAG = GalleryAdapter.class.getName();

    private Context mContext;
    private int mMode;
    private ArrayList<String> mGalleryPicturePaths;
    private List<String> mMultipleSelectedPicturePaths;
    private String mSingleSelectedPicturePath;

    private OnMultipleItemSelectListener mOnMultipleItemSelectListener;

    public GalleryAdapter(Context context, ArrayList<String> galleryPicturePaths, List<String> multipleSelectedPicturePaths, int mode) {
        this.mContext = context;
        this.mGalleryPicturePaths = galleryPicturePaths;
        this.mMultipleSelectedPicturePaths = multipleSelectedPicturePaths;
        this.mMode = mode;
    }

    public GalleryAdapter(Context context, ArrayList<String> galleryPicturePaths, List<String> multipleSelectedPicturePaths, String singleSelectedPicturePath, int mode) {
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
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gallery_picture_grid_item, parent, false);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.gallery_image);
            viewHolder.button = (Button) convertView.findViewById(R.id.show_clicked_Image);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.gallery_selected_image);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        boolean isCheck = false;
        for (int i = 0; i < mMultipleSelectedPicturePaths.size(); i++) {
            if (mMultipleSelectedPicturePaths.get(i).contains(mGalleryPicturePaths.get(position))) {
                isCheck = true;
            }
        }

        if (mMode == MENU_MODE_MULTIPLE_SELECT) {
            if (isCheck) {
                viewHolder.checkBox.setEnabled(true);
                viewHolder.checkBox.setChecked(true);
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkBox.setEnabled(false);
                viewHolder.checkBox.setChecked(false);
                viewHolder.checkBox.setVisibility(View.INVISIBLE);
            }
        } else if (mMode == MENU_MODE_SINGLE_SELECT) {
            if (isCheck) {
                viewHolder.imageView.setAlpha(0.2f);
                viewHolder.checkBox.setEnabled(false);
                viewHolder.checkBox.setChecked(false);
                viewHolder.checkBox.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.imageView.setAlpha(1.0f);
                viewHolder.checkBox.setEnabled(false);
                viewHolder.checkBox.setChecked(false);
                viewHolder.checkBox.setVisibility(View.INVISIBLE);
            }
            if (mSingleSelectedPicturePath != null) {
                if (mGalleryPicturePaths.get(position).equals(mSingleSelectedPicturePath)) {
                    viewHolder.checkBox.setEnabled(true);
                    viewHolder.checkBox.setChecked(true);
                    viewHolder.checkBox.setVisibility(View.VISIBLE);
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
                .into(viewHolder.imageView);

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMode == MENU_MODE_MULTIPLE_SELECT) {
                    boolean isSelectedBefore = false;
                    if (mMultipleSelectedPicturePaths.size() != 0) {
                        for (int i = 0; i < mMultipleSelectedPicturePaths.size(); i++) {
                            if (getItem(position).equals(mMultipleSelectedPicturePaths.get(i))) {
                                isSelectedBefore = true;

                                mMultipleSelectedPicturePaths.remove(i);
                                viewHolder.checkBox.setEnabled(false);
                                viewHolder.checkBox.setChecked(false);
                                viewHolder.checkBox.setVisibility(View.INVISIBLE);
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

        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Click ---> " + String.valueOf(position));
                Intent intent = new Intent(mContext, GalleryFullSizePictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("galleryPicturePaths", mGalleryPicturePaths);
                bundle.putString("ImagePath", "file:/" + ImagePath);
                intent.putExtras(bundle);
                ((Activity) mContext).startActivityForResult(intent, 0);
            }
        });

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

    private static class ViewHolder {
        ImageView imageView;
        Button button;
        CheckBox checkBox;
    }
}