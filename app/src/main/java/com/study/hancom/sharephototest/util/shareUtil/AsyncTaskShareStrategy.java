package com.study.hancom.sharephototest.util.shareUtil;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class AsyncTaskShareStrategy<T> extends AsyncTask<Object, T, String> {
    abstract protected T validate(File file, List params);
    abstract protected T sendRequest(File file, List params) throws IOException;
    abstract protected T receiveResponse(StringBuilder result) throws IOException;

    @Override
    protected final String doInBackground(Object... params) {
        File file = (File) params[0];
        List paramList = (List) params[1];

        StringBuilder result = new StringBuilder();

        publishProgress(validate(file, paramList));
        if (isCancelled()) {
            return null;
        }

        try {
            publishProgress(sendRequest(file, paramList));
        } catch (IOException e) {
            e.printStackTrace();
            cancel(true);
        }
        if (isCancelled()) {
            return null;
        }

        try {
            publishProgress(receiveResponse(result));
        } catch (IOException e) {
            e.printStackTrace();
            cancel(true);
        }
        if (isCancelled()) {
            return null;
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        Log.v("tag", "끝남");
        super.onPostExecute(s);
    }

    @Override
    protected void onCancelled() {
        Log.v("tag", "취소");
        super.onCancelled();
    }
}
