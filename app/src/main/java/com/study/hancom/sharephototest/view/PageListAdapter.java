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
    public View getView(int position, View convertView, final ViewGroup parentView) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_custom, parentView, false);
            viewHolder = new ViewHolder();
            viewHolder.pageGridView = (PageGridView) convertView.findViewById(R.id.custom_grid_view);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PageGridView pageGridView = viewHolder.pageGridView;
        final PageGridAdapter pageGridAdapter = new PageGridAdapter(parentView.getContext(), mItemList.get(position));
        pageGridView.setAdapter(pageGridAdapter);
        pageGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                ((PageListView) parentView).startEditMode();
                return pageGridView.drawFloatingItemView();
            }
        });
        pageGridView.setOnItemDropListener(new CustomGridView.OnItemDropListener() {
            @Override
            public boolean onItemDrop(View view, int fromPosition, int toPosition, int toRawX, int toRawY) {
                if (fromPosition > -1) {
                    if (toPosition > -1) {
                        pageGridAdapter.reorderItem(fromPosition, toPosition);
                    } else {
                        int toListPosition = ((PageListView) parentView).getUpEventItemPosition();
                        if (toListPosition > -1) {
                            String item = pageGridAdapter.removeItem(fromPosition);
                            getItem(toListPosition).add(item);
                        }
                    }
                    notifyDataSetChanged();
                }
                ((PageListView) parentView).stopEditMode();
                pageGridView.removeFloatingItemView();

                return true;
            }
        });
        pageGridView.setOnItemCancelListener(new CustomGridView.OnItemCancelListener() {
            @Override
            public void onItemCancel(View view, int x, int y) {
                ((PageListView) parentView).stopEditMode();
                pageGridView.removeFloatingItemView();
            }
        });

        return convertView;
    }

    static private class ViewHolder {
        TextView textView;
        PageGridView pageGridView;
    }
}