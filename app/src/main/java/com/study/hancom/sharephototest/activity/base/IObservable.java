package com.study.hancom.sharephototest.activity.base;

import android.os.Bundle;

public interface IObservable {
    void addObserver(String tag, IObserver observer);
    IObserver removeObserver(String tag);
    IObserver getObserver(String tag);
    int getObserverCount();
    void notifyChanged(String tag);
    void notifyChanged(String tag, Bundle out);
    void notifyChangedAll();
    void notifyChangedAll(Bundle out);
}
