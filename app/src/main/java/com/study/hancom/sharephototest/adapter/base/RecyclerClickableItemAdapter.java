package com.study.hancom.sharephototest.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

abstract public class RecyclerClickableItemAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(holder.itemView, position);
                }
                return false;
            }
        });
    }

    public void setOnOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    public boolean performItemClick(View view, int position) {
        final boolean result;
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, position);
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    /* 리스너 인터페이스 */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }
}
