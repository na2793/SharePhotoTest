package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.base.CustomGridAdapter;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.view.PageElementGridView;

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
        if (!mImageLoader.isInited()) {
            mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.page_editor_page_element_list_item_grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.item_grid_text);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_grid_image);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.item_grid_checkbox);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PageElementGridView parentPageElementGridView = (PageElementGridView) parent;

        if (parentPageElementGridView.isMultipleSelectItemMode()) {
            /* 체크박스 처리 */
            if (parentPageElementGridView.isMultipleSelectedItem(position)) {
                viewHolder.checkBox.setChecked(true);
            }
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        } else {
            final int selectedItemPosition = parentPageElementGridView.getSelectedItemPosition();

            /* 체크박스 처리 */
            if (selectedItemPosition == position) {
                viewHolder.checkBox.setChecked(true);
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkBox.setChecked(false);
                viewHolder.checkBox.setVisibility(View.GONE);
            }
        }

        /* 텍스트뷰 처리 */
        String elementNum = Integer.toString(position + 1);
        viewHolder.textView.setText(elementNum);

        /* 이미지뷰 처리 */
        mImageLoader.displayImage(getItem(position).getPath(), viewHolder.imageView, mImageOptions);

        return convertView;
    }

    static private class ViewHolder {
        TextView textView;
        ImageView imageView;
        CheckBox checkBox;
    }
}
