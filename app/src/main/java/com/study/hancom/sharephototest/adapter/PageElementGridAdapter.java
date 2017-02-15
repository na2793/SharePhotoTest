package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.base.CustomGridAdapter;
import com.study.hancom.sharephototest.model.Picture;

import java.util.ArrayList;
import java.util.List;

public class PageElementGridAdapter extends CustomGridAdapter<Picture> {

    private static ImageLoader mImageLoader = ImageLoader.getInstance();
    private static DisplayImageOptions mImageOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.place_holder)
            .showImageForEmptyUri(R.drawable.place_holder)
            .showImageOnFail(R.drawable.place_holder)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public PageElementGridAdapter(Context context) {
        this(context, new ArrayList<Picture>());
    }

    public PageElementGridAdapter(Context context, List<Picture> itemList) {
        super(context, itemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.page_editor_page_element_list_item_grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.item_grid_text);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_grid_image);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String elementNum = Integer.toString(position + 1);

        viewHolder.textView.setText(elementNum);
        mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        mImageLoader.displayImage(getItem(position).getPath(), viewHolder.imageView, mImageOptions);

        return convertView;
    }

    static private class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
