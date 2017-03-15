package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.base.SectionedRecyclerGridAdapter;
import com.study.hancom.sharephototest.model.Album;

public class ElementGridAdapter extends SectionedRecyclerGridAdapter<Album, ElementGridAdapter.HeaderViewHolder, ElementGridAdapter.ContentViewHolder> {

    @Override
    public int getSectionCount() {
        return 0;
    }

    @Override
    public int getCountInSection(int sectionNum) {
        return 0;
    }

    @Override
    public boolean isHeader(int position) {
        return false;
    }

    public ElementGridAdapter(Context context, Album data, GridLayoutManager layoutManager) {
        super(context, data, layoutManager);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View headerView = LayoutInflater.from(mContext).inflate(R.layout.album_overview_grid_item, parent, false);
        return new HeaderViewHolder(headerView);
    }

    @Override
    public ContentViewHolder onCreateContentViewHolder(ViewGroup parent) {
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.album_overview_grid_item, parent, false);
        return new ContentViewHolder(itemView);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        ContentViewHolder(View itemView) {
            super(itemView);
        }
    }
}
