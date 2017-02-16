package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.view.PageElementGridView;
import com.study.hancom.sharephototest.base.CustomListAdapter;

import java.util.ArrayList;
import java.util.List;

public class PageElementListAdapter extends CustomListAdapter<Page> {

    public PageElementListAdapter(Context context) {
        this(context, new ArrayList<Page>());
    }

    public PageElementListAdapter(Context context, List<Page> itemList) {
        super(context, itemList);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parentView) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.page_editor_page_element_list_item_grid, parentView, false);
            viewHolder = new ViewHolder();
            viewHolder.pageElementGridView = (PageElementGridView) convertView.findViewById(R.id.custom_grid_view);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String pageName = Integer.toString(position + 1) + " 페이지";
        viewHolder.textView.setText(pageName);

        final PageElementGridView pageElementGridView = viewHolder.pageElementGridView;
        final PageElementGridAdapter pageElementGridAdapter = new PageElementGridAdapter(parentView.getContext());

        final Page page = mItemList.get(position);
        int elementCount = page.getPictureCount();

        for (int i = 0; i < elementCount; i++) {
            pageElementGridAdapter.addItem(page.getPicture(i));
        }

        pageElementGridView.setAdapter(pageElementGridAdapter);

        return convertView;
    }

    static private class ViewHolder {
        TextView textView;
        PageElementGridView pageElementGridView;
    }
}