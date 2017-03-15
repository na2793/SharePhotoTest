package com.study.hancom.sharephototest.adapter.base;

import android.content.Context;
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

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter {
    
    protected Context mContext;
    protected ArrayList<String> mPicturePathList;

    public GalleryAdapter(Context context, ArrayList<String> picturePaths) {
        this.mContext = context;
        this.mPicturePathList = picturePaths;
    }

    @Override
    public int getCount() {
        return mPicturePathList.size();
    }

    @Override
    public String getItem(int position) {
        return mPicturePathList.get(position);
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

        final String ImagePath = mPicturePathList.get(position);
        Glide.with(mContext).load(ImagePath).centerCrop().into(viewHolder.imageView);

        return convertView;
    }

    protected static class ViewHolder {
        ImageView imageView;
        Button button;
        CheckBox checkBox;
    }
}