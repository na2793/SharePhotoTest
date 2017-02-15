package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.view.PageElementGridView;
import com.study.hancom.sharephototest.view.PageElementListView;
import com.study.hancom.sharephototest.base.CustomGridView;
import com.study.hancom.sharephototest.base.CustomListAdapter;

import java.util.ArrayList;
import java.util.List;

public class PageElementListAdapter extends CustomListAdapter<Page> {

    private int mSelectedIndex;

    public PageElementListAdapter(Context context) {
        this(context, new ArrayList<Page>());
    }

    public PageElementListAdapter(Context context, List<Page> itemList) {
        super(context, itemList);
        mSelectedIndex = -1;
    }

    public void setSelectedIndex(int position) {
        mSelectedIndex = position;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
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

        //** 임시
        if (position == mSelectedIndex) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorLightGray));
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
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
                        page.reorderPicture(fromPosition, toPosition);
                    } else {
                        int toListPosition = ((PageElementListView) parentView).getUpEventItemPosition();
                        if (toListPosition > -1) {
                            Picture targetItem = page.removePicture(fromPosition);
                            getItem(toListPosition).addPicture(targetItem);
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