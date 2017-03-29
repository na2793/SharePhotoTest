package com.study.hancom.sharephototest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.LayoutGridAdapter;
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.PageLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumEditorNewLayoutSelectionActivity extends AppCompatActivity {

    private GridView mLayoutView;
    private LayoutGridAdapter mLayoutGridAdapter;
    private List<PageLayout> mPageLayoutList;
    private int mType;

    private Spinner mSpinner;
    private ArrayAdapter mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_editor_new_layout_main);

        /* 뒤로 가기 버튼 생성 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try {
             /* 데이터 파싱 */
            Bundle bundle = getIntent().getExtras();
            mType = bundle.getInt("currentElementNum");
            mPageLayoutList = Page.getAllLayoutForType(mType);

             /* 스피너 */
            mSpinner = (Spinner) findViewById(R.id.spinner);
            List<Integer> usableElementNumList = new ArrayList<>(Page.getAllLayoutType());
            Collections.sort(usableElementNumList);

            mSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, usableElementNumList);
            mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(mSpinnerAdapter);

            int spinnerPosition = mSpinnerAdapter.getPosition(mType);
            mSpinner.setSelection(spinnerPosition);
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mType = (int) mSpinner.getSelectedItem();
                    try {
                        mPageLayoutList = Page.getAllLayoutForType(mType);
                        mLayoutGridAdapter.setPageLayoutList(mPageLayoutList, mType);
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
            mLayoutView = (GridView) findViewById(R.id.new_layout_grid_view);
            mLayoutGridAdapter = new LayoutGridAdapter(getApplicationContext(), mType, mPageLayoutList);
            mLayoutView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
            mLayoutView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.v("tag", "onClick");
                }

            });
            mLayoutView.setAdapter(mLayoutGridAdapter);

        } catch (LayoutNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_editor_new_layout_main, menu);
        setTitle(R.string.title_album_editor_new_layout_main);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_new_layout_confirm:
//                Intent intent = new Intent(getApplicationContext(), AlbumEditorActivity.class);
//                intent.putExtra("selectedNewLayout", mGalleryAdapter.getItem(mGalleryAdapter.getSelectedPosition()));
//                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
