package com.study.hancom.sharephototest.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomListAdapter<T> extends BaseAdapter implements CustomListAdapterInterface<T> {

    protected Context mContext;
    protected List<T> mItemList;
    protected LayoutInflater mInflater;

    public CustomListAdapter(Context context) {
        this(context, new ArrayList<T>());
    }

    public CustomListAdapter(Context context, List<T> itemList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mItemList = itemList;
    }

    public void addItem(T item) {
        mItemList.add(item);
    }

    public void addItem(int position, T item) {
        mItemList.add(position, item);
    }

    public T removeItem(int position) {
        return mItemList.remove(position);
    }

    public void reorderItem(int from, int to) {
        T oldItem = removeItem(from);
        addItem(to, oldItem);
    }

    public void clear() {
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