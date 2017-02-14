package com.study.hancom.sharephototest.view.base;

public interface CustomListAdapterInterface<T> {
    void addItem(T item);
    void addItem(int position, T item);
    T removeItem(int position);
    void reorderItem(int from, int to);
    void clear();
}