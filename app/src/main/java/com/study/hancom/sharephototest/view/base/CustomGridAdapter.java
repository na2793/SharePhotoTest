package com.study.hancom.sharephototest.view.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomGridAdapter<T> extends BaseAdapter implements CustomGridAdapterInterface<T> {

    protected Context mContext;
    protected List<T> mItemList;
    protected LayoutInflater mInflater;

    private boolean isNotifyBlocked = false;

    public CustomGridAdapter(Context context)
    {
        this(context, new ArrayList<T>());
    }

    public CustomGridAdapter(Context context, List<T> itemList)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mItemList = itemList;
    }

    public void addItem(T item)
    {
        mItemList.add(item);
        if (!isNotifyBlocked) {
            notifyDataSetChanged();
        }
    }

    public void addItem(int position, T item)
    {
        mItemList.add(position, item);
        if (!isNotifyBlocked) {
            notifyDataSetChanged();
        }
    }

    public T removeItem(int position)
    {
        T oldData = mItemList.remove(position);
        if (!isNotifyBlocked) {
            notifyDataSetChanged();
        }

        return oldData;
    }

    public void reorderItem(int from, int to)
    {
        try {
            isNotifyBlocked = true;
            T oldItem = removeItem(from);
            addItem(to, oldItem);
            notifyDataSetChanged();
        } finally {
            isNotifyBlocked = false;
        }
    }

    public void clear()
    {
        mItemList.clear();
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public T getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    abstract public View getView(int position, View convertView, ViewGroup parent);
}
