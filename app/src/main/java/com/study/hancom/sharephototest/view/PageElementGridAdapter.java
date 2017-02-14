package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.view.base.CustomGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class PageElementGridAdapter extends CustomGridAdapter<String> {

    public PageElementGridAdapter(Context context) {
        this(context, new ArrayList<String>());
    }

    public PageElementGridAdapter(Context context, List<String> itemList) {
        super(context, itemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.page_editor_page_element_list_item_grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.item_grid_custom_text);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(getItem(position));

        return convertView;
    }

    static private class ViewHolder {
        TextView textView;
    }
}
