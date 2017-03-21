package com.study.hancom.sharephototest.adapter.base;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

abstract public class SectionedRecyclerGridAdapter<T, HVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_CONTENT = 1;

    protected Context mContext;
    protected T mData;
    private GridLayoutManager mLayoutManager;

    private List<Integer> mSectionPositionList = new ArrayList<>();

    public SectionedRecyclerGridAdapter(Context context, T data, GridLayoutManager layoutManager) {
        mContext = context;
        mData = data;
        mLayoutManager = layoutManager;
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int rawPosition) {
                return isHeader(rawPosition) ? mLayoutManager.getSpanCount() : 1;
            }
        });
    }

    @Override
    public int getItemCount() {
        int sectionCount = getSectionCount();
        int itemCount = 0;

        mSectionPositionList.clear();
        mSectionPositionList.add(0);
        for (int i = 0; i < sectionCount; ++i) {
            itemCount += getCountInSection(i) + 1;
            mSectionPositionList.add(itemCount);
        }

        return itemCount;
    }

    abstract public int getSectionCount();

    abstract public int getCountInSection(int sectionIndex);

    public boolean isHeader(int rawPosition) {
        return mSectionPositionList.contains(rawPosition);
    }

    public int rawPositionToPosition(int rawPosition) {
        int section = getSectionFor(rawPosition);
        int sectionPosition = mSectionPositionList.get(section);

        return rawPosition - sectionPosition - 1;
    }

    public int getSectionFor(int rawPosition) {
        int section = -1;
        for (int eachSectionPosition : mSectionPositionList) {
            if (eachSectionPosition <= rawPosition) {
                section++;
            } else {
                return section;
            }
        }

        // this will never happen;
        return section;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return onCreateHeaderViewHolder(parent);
        }

        return onCreateContentViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int rawPosition) {
        int section = getSectionFor(rawPosition);

        if (isHeader(rawPosition)) {
            onBindHeaderViewHolder((HVH) holder, section, rawPosition);
            return;
        }

        int position = rawPositionToPosition(rawPosition);
        onBindContentViewHolder((CVH) holder, section, position, rawPosition);
    }

    abstract public HVH onCreateHeaderViewHolder(ViewGroup parent);

    abstract public CVH onCreateContentViewHolder(ViewGroup parent);

    abstract public void onBindHeaderViewHolder(HVH holder, int section, int rawPosition);

    abstract public void onBindContentViewHolder(CVH holder, int section, int position, int rawPosition);

    @Override
    public int getItemViewType(int rawPosition) {
        return isHeader(rawPosition) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_CONTENT;
    }
}
