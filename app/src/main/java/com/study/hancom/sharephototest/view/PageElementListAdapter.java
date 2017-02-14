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

public class PageElementListAdapter extends CustomListAdapter<List<String>> {

    public PageElementListAdapter(Context context) {
        this(context, new ArrayList<List<String>>());
    }
    public PageElementListAdapter(Context context, List<List<String>> itemList) {
        super(context, itemList);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parentView) {

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

        final PageElementGridView pageElementGridView = viewHolder.pageElementGridView;
        final PageElementGridAdapter pageElementGridAdapter = new PageElementGridAdapter(parentView.getContext(), mItemList.get(position));
        pageElementGridView.setAdapter(pageElementGridAdapter);
        pageElementGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                ((PageElementListView) parentView).startEditMode();
                return pageElementGridView.drawFloatingItemView();
            }
        });
        pageElementGridView.setOnItemDropListener(new CustomGridView.OnItemDropListener() {
            @Override
            public boolean onItemDrop(View view, int fromPosition, int toPosition, int toRawX, int toRawY) {
                if (fromPosition > -1) {
                    if (toPosition > -1) {
                        pageElementGridAdapter.reorderItem(fromPosition, toPosition);
                    } else {
                        int toListPosition = ((PageElementListView) parentView).getUpEventItemPosition();
                        if (toListPosition > -1) {
                            String item = pageElementGridAdapter.removeItem(fromPosition);
                            getItem(toListPosition).add(item);
                        }
                    }
                    notifyDataSetChanged();
                }
                ((PageElementListView) parentView).stopEditMode();
                pageElementGridView.removeFloatingItemView();

                return true;
            }
        });
        pageElementGridView.setOnItemCancelListener(new CustomGridView.OnItemCancelListener() {
            @Override
            public void onItemCancel(View view, int x, int y) {
                ((PageElementListView) parentView).stopEditMode();
                pageElementGridView.removeFloatingItemView();
            }
        });

        return convertView;
    }

    static private class ViewHolder {
        TextView textView;
        PageElementGridView pageElementGridView;
    }
}