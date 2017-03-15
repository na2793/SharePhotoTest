package com.study.hancom.sharephototest.adapter.base;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

abstract public class SectionedRecyclerGridAdapter<T, HVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_CONTENT = 1;

    protected Context mContext;
    protected T mData;
    private GridLayoutManager mLayoutManager;

    @Override
    public int getItemCount() {
        int itemCount = 0;
        int sectionCount = getSectionCount();

        for (int i = 0; i < sectionCount; ++i) {
            itemCount += getCountInSection(i);
        }

        return itemCount + sectionCount;
    }

    abstract public int getSectionCount();

    abstract public int getCountInSection(int sectionNum);

    abstract public boolean isHeader(int position);

    public SectionedRecyclerGridAdapter(Context context, T data, GridLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isHeader(position) ? mLayoutManager.getSpanCount() : 1;
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return onCreateHeaderViewHolder(parent);
        }

        return onCreateContentViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeader(position)) {
            onBindHeaderViewHolder(holder, position);
            return;
        }

        onBindContentViewHolder(holder, position);
    }

    abstract public HVH onCreateHeaderViewHolder(ViewGroup parent);

    abstract public CVH onCreateContentViewHolder(ViewGroup parent);

    abstract public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position);

    abstract public void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_CONTENT;
    }
}
