package com.study.hancom.sharephototest.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.view.base.CustomGridView;
import com.study.hancom.sharephototest.view.base.CustomListAdapter;

import java.util.ArrayList;
import java.util.List;

public class PageListAdapter extends CustomListAdapter<List<String>> {

    public PageListAdapter(Context context) {
        this(context, new ArrayList<List<String>>());
    }

    public PageListAdapter(Context context, List<List<String>> itemList) {
        super(context, itemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.grid_custom, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.pageGridView = (PageGridView) convertView.findViewById(R.id.custom_grid_view);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PageGridView pageGridView = viewHolder.pageGridView;
        PageGridAdapter pageGridAdapter = new PageGridAdapter(parent.getContext(), mItemList.get(position));
        pageGridView.setAdapter(pageGridAdapter);

        return convertView;
    }

    static private class ViewHolder {
        TextView textView;
        PageGridView pageGridView;
    }
}