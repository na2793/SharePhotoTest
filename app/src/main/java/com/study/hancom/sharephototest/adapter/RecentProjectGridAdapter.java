package com.study.hancom.sharephototest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.base.RecyclerClickableItemAdapter;
import com.study.hancom.sharephototest.util.MathUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecentProjectGridAdapter extends RecyclerClickableItemAdapter<RecentProjectGridAdapter.ViewHolder> {
    private Context mContext;

    private ArrayList<String> mRecentProjectPaths;
    private Map<String, Integer> mCoverColor = new HashMap<>();

    public RecentProjectGridAdapter(Context context, ArrayList<String> recentProjectPaths) {
        mContext = context;
        mRecentProjectPaths = recentProjectPaths;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mRecentProjectPaths == null) {
            return 0;
        }
        return mRecentProjectPaths.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.activity_main_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        int color[] = {mContext.getResources().getColor(R.color.colorLightGray),
                mContext.getResources().getColor(R.color.colorAccent),
                mContext.getResources().getColor(R.color.colorPrimary),
                mContext.getResources().getColor(R.color.lb_search_bar_hint),
                mContext.getResources().getColor(R.color.lb_speech_orb_recording)};

        String path = mRecentProjectPaths.get(position);
        if(mCoverColor.get(path) == null) {
            mCoverColor.put(path, color[MathUtil.getRandomMath(color.length - 1, 0)]);
        }
        holder.textView.setBackgroundColor(mCoverColor.get(path));
        holder.textView.setText(mRecentProjectPaths.get(position));

    }

    public void setRecentProjectPaths(ArrayList<String> recentProjectPaths) {
        mRecentProjectPaths = recentProjectPaths;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.recent_project_title);
        }
    }
}