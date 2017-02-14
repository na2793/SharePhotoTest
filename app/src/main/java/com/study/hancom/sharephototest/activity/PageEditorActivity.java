package com.study.hancom.sharephototest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.view.PageElementListAdapter;
import com.study.hancom.sharephototest.view.PageElementListView;

import java.util.ArrayList;
import java.util.List;

public class PageEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_editor_main);

        List<String> dataList = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            dataList.add(Integer.toString(i));
        }

        List<String> dataList2 = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            dataList2.add(Integer.toString(i));
        }

        List<String> dataList3 = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            dataList3.add(Integer.toString(i));
        }

        final PageElementListView pageElementListView = (PageElementListView) findViewById(R.id.page_list_view);
        final PageElementListAdapter pageElementListAdapter = new PageElementListAdapter(this);

        pageElementListView.setAdapter(pageElementListAdapter);
        pageElementListAdapter.addItem(dataList);
        pageElementListAdapter.addItem(dataList2);
        pageElementListAdapter.addItem(dataList3);
    }
}
