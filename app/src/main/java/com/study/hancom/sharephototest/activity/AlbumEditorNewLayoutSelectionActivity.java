package com.study.hancom.sharephototest.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.LayoutGridAdapter;
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.PageLayout;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumEditorNewLayoutSelectionActivity extends AppCompatActivity {

    private Spinner mSpinner;
    private ArrayAdapter mSpinnerAdapter;

    private AutoFitRecyclerGridView mLayoutView;
    private LayoutGridAdapter mLayoutGridAdapter;

    private List<PageLayout> mPageLayoutList;
    private List<Integer> usableElementNumList;

    private int mType;
    private int mCurrentSection;

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_editor_new_layout_main);

        /* 뒤로 가기 버튼 생성 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
             /* 데이터 파싱 */
            Bundle bundle = getIntent().getExtras();
            mCurrentSection = bundle.getInt("currentSection");
            PageLayout currentPageLayout = bundle.getParcelable("currentPageLayout");

            mType = currentPageLayout.getElementNum();
            mPageLayoutList = Page.getAllLayoutForType(mType);

             /* 스피너 */
            mSpinner = (Spinner) findViewById(R.id.spinner);
            usableElementNumList = new ArrayList<>(Page.getAllLayoutType());
            Collections.sort(usableElementNumList);
            mSpinnerAdapter = new ArrayAdapter<Integer>(getApplicationContext(), android.R.layout.simple_spinner_item, usableElementNumList) {
                @Override
                public boolean isEnabled(int position) {
                    if (mType > usableElementNumList.get(position)) {
                        return false;
                    }
                    return true;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view;
                    if (mType > usableElementNumList.get(position)) {
                        textView.setTextColor(Color.GRAY);
                    } else {
                        textView.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };

            mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(mSpinnerAdapter);

            int spinnerPosition = mSpinnerAdapter.getPosition(mType);
            mSpinner.setSelection(spinnerPosition);
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int newType = (int) mSpinner.getSelectedItem();
                    try {
                        mPageLayoutList = Page.getAllLayoutForType(newType);
                        mLayoutGridAdapter.setPageLayoutList(mPageLayoutList);
                        mLayoutGridAdapter.setSelectedPosition(-1);
                        mLayoutGridAdapter.notifyDataSetChanged();
                    } catch (LayoutNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

            /* 레이아웃 그리드 */
            mLayoutView = (AutoFitRecyclerGridView) findViewById(R.id.new_layout_grid_view);
            mLayoutGridAdapter = new LayoutGridAdapter(getApplicationContext(), mPageLayoutList);
            mLayoutView.setAdapter(mLayoutGridAdapter);

        } catch (LayoutNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void changeActionBar() {
        if (mLayoutGridAdapter.getSelectedPosition() > -1) {
            mMenu.findItem(R.id.action_new_layout_confirm).setEnabled(true);
        } else {
            mMenu.findItem(R.id.action_new_layout_confirm).setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.album_editor_new_layout_main, menu);

        setTitle(R.string.title_album_editor_new_layout_main);
        changeActionBar();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_new_layout_confirm:
                Intent intent = new Intent(getApplicationContext(), AlbumEditorActivity.class);

                /* 쩐송할 데이터 */
                Bundle bundle = new Bundle();
                bundle.putInt("currentSection", mCurrentSection);
                bundle.putParcelable("currentPageLayout", mLayoutGridAdapter.getItem(mLayoutGridAdapter.getSelectedPosition()));
                intent.putExtras(bundle);

                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}