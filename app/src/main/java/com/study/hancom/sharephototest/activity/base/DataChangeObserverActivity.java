package com.study.hancom.sharephototest.activity.base;

import android.support.v7.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class DataChangeObserverActivity extends AppCompatActivity {

    private Set<OnDataChangeListener> mDataChangeListenerList = new HashSet<>();

    public void addDataChangeListener(OnDataChangeListener listener) {
        mDataChangeListenerList.add(listener);
    }

    public void removeDataChangeListener(OnDataChangeListener listener) {
        mDataChangeListenerList.remove(listener);
    }

    public void notifyChanged() {
        for (OnDataChangeListener eachDataChangeListener : mDataChangeListenerList) {
            eachDataChangeListener.onDataChanged();
        }
    }

    public interface OnDataChangeListener {
        void onDataChanged();
    }
}