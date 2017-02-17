package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.view.PageElementGridView;
import com.study.hancom.sharephototest.base.CustomListAdapter;
import com.study.hancom.sharephototest.view.PageElementListView;

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
            viewHolder.button1 = (Button) convertView.findViewById(R.id.button1);
            viewHolder.button2 = (Button) convertView.findViewById(R.id.button2);
            viewHolder.button3 = (Button) convertView.findViewById(R.id.button3);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PageElementListView parentPageElementListView = (PageElementListView) parentView;
        final int selectedItemPosition = parentPageElementListView.getSelectedItemPosition();

        /* 텍스트뷰 처리 */
        String pageName = Integer.toString(position + 1) + " 페이지";
        viewHolder.textView.setText(pageName);
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 아이템 선택 */
                if (position == parentPageElementListView.getSelectedItemPosition()) {
                    parentPageElementListView.setSelectedItemPosition(-1);
                } else {
                    parentPageElementListView.setSelectedItemPosition(position);
                }
                notifyDataSetChanged();
            }
        });

        /* 그리드뷰 처리 */
        final PageElementGridView pageElementGridView = viewHolder.pageElementGridView;
        final PageElementGridAdapter pageElementGridAdapter = new PageElementGridAdapter(parentView.getContext());

        final Page page = mItemList.get(position);
        int elementCount = page.getPictureCount();

        for (int i = 0; i < elementCount; i++) {
            pageElementGridAdapter.addItem(page.getPicture(i));
        }

        pageElementGridView.setAdapter(pageElementGridAdapter);

        /* 분기 */
        if (pageElementGridView.isMultipleSelectItemMode()) {
            /* 그리드뷰 처리 */
            pageElementGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int itemPosition, long id) {
                    if (pageElementGridView.isMultipleSelectedItem(itemPosition)) {
                        pageElementGridView.removeMultipleSelectedItemPosition(itemPosition);
                    } else {
                        pageElementGridView.addMultipleSelectedItemPosition(itemPosition);
                    }
                    notifyDataSetChanged();
                }
            });
            /* 버튼 처리 */
            viewHolder.button1.setVisibility(View.GONE);
            viewHolder.button2.setVisibility(View.GONE);
            viewHolder.button3.setVisibility(View.GONE);
        } else {
            /* 그리드뷰 처리 */
            pageElementGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int itemPosition, long id) {
                    Log.v("tag", "test2");
                    if (selectedItemPosition > -1) {
                        PageElementGridView item = (PageElementGridView) parentView.getChildAt(selectedItemPosition).findViewById(R.id.custom_grid_view);
                        item.setSelectedItemPosition(-1);
                    }
                    parentPageElementListView.setSelectedItemPosition(position);
                    pageElementGridView.setSelectedItemPosition(itemPosition);
                    notifyDataSetChanged();
                }
            });
            pageElementGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int itemPosition, long id) {
                    Log.v("tag", "test3");
                    for (int i = 0; i < parentView.getChildCount(); i++) {
                        PageElementGridView item = (PageElementGridView) parentView.getChildAt(i).findViewById(R.id.custom_grid_view);
                        item.startMultipleSelectItemMode();
                    }
                    pageElementGridView.addMultipleSelectedItemPosition(itemPosition);
                    notifyDataSetChanged();
                    return false;
                }
            });
            /* 버튼 처리 */
            if (selectedItemPosition == position) {
                viewHolder.button1.setVisibility(View.VISIBLE);
                viewHolder.button2.setVisibility(View.VISIBLE);
                viewHolder.button3.setVisibility(View.VISIBLE);
            } else {
                viewHolder.button1.setVisibility(View.GONE);
                viewHolder.button2.setVisibility(View.GONE);
                viewHolder.button3.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    static private class ViewHolder {
        TextView textView;
        PageElementGridView pageElementGridView;
        Button button1;
        Button button2;
        Button button3;
    }
}