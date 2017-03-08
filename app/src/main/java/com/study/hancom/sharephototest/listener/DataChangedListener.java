package com.study.hancom.sharephototest.listener;

import java.util.HashSet;
import java.util.Set;

public class DataChangedListener {
    private static Set<OnDataChangeListener> mDataChangeListenerList = new HashSet<>();

    public static void addDataChangeListener(OnDataChangeListener listener) {
        mDataChangeListenerList.add(listener);
    }

    public static void removeDataChangeListener(OnDataChangeListener listener) {
        mDataChangeListenerList.remove(listener);
    }

    public static void notifyChanged() {
        for (OnDataChangeListener eachDataChangeListener : mDataChangeListenerList) {
            eachDataChangeListener.onDataChanged();
        }
    }

    public interface OnDataChangeListener {
        void onDataChanged();
    }
}
