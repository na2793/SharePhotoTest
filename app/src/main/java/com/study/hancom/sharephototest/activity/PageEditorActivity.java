package com.study.hancom.sharephototest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.PageEditorAdapter;
import com.study.hancom.sharephototest.adapter.base.SectionableAdapter;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class PageEditorActivity extends AppCompatActivity {

    private static final int MAX_ELEMENT_OF_PAGE_NUM = 4;

    private static final int MENU_MODE_MAIN = 1;
    private static final int MENU_MODE_SINGLE_SELECT = 2;
    private static final int MENU_MODE_MULTIPLE_SELECT = 3;

    private Menu mMenu;
    private ListView mPageListView;
    PageEditorAdapter mPageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_editor_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* 데이터 변환 (path -> picture) */
        List<String> picturePathList = getIntentData("selectedImage");
        List<Picture> pictureList = new ArrayList<>();

        for (String eachPicturePath : picturePathList) {
            Picture picture = new Picture(eachPicturePath, 0, 0);
            pictureList.add(picture);
        }

        /* 앨범 구성 */
        int pictureNum = pictureList.size();
        Album album = new Album("testAlbumName");
        int temp = 0;

        while (temp < pictureNum) {
            try {
                int elementCount = MathUtil.getRandomMath(MAX_ELEMENT_OF_PAGE_NUM, 1);
                if (pictureNum > temp + elementCount) {
                    album.addPage(new Page(elementCount));
                    temp += elementCount;
                } else {
                    album.addPage(new Page(pictureNum - temp));
                    break;
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }

        /* 페이지에 사진 넣기 */
        int pageNum = album.getPageCount();
        int usedPictureCount = 0;

        for (int i = 0 ; i < pageNum ; i++) {
            Page page = album.getPage(i);
            int elementNum = page.getLayout().getElementNum();

            for (int j = 0 ; j < elementNum ; j++) {
                page.addPicture(pictureList.get(usedPictureCount));
                usedPictureCount++;
            }
        }

        mPageListView = (ListView) findViewById(R.id.page_list_view);
        mPageListAdapter = new PageEditorAdapter(this, album,
                R.layout.page_editor_list_row, R.id.row_header,
                R.id.row_itemHolder, SectionableAdapter.MODE_VARY_WIDTHS);
        mPageListAdapter.setOnMultipleItemSelectionModeListener(new PageEditorAdapter.OnMultipleItemSelectionModeListener() {
            @Override
            public void onStart() {
                onChangeActionBar(MENU_MODE_MULTIPLE_SELECT);
            }
            @Override
            public void onStop() {
                onChangeActionBar(MENU_MODE_MAIN);
            }
        });
        mPageListAdapter.setOnItemSelectListener(new PageEditorAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelect() {
                onChangeActionBar(MENU_MODE_SINGLE_SELECT);
            }
            @Override
            public void onItemSelectCancel() {
                onChangeActionBar(MENU_MODE_MAIN);
            }
        });
        mPageListView.setAdapter(mPageListAdapter);
        mPageListView.setDividerHeight(0);
    }

    private List<String> getIntentData(String extraName) {
        Intent intent = getIntent();

        return intent.getStringArrayListExtra(extraName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.page_editor_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_next:
                return true;
            case R.id.action_multiple_edit:
                return true;
            case R.id.action_multiple_move:
                return true;
            case R.id.action_multiple_delete:
                return true;
            case R.id.action_single_edit:
                return true;
            case R.id.action_single_move:
                return true;
            case R.id.action_single_delete:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onChangeActionBar(int mode){
        MenuInflater menuInFlater = getMenuInflater();
        mMenu.clear();
        switch (mode) {
            case MENU_MODE_MAIN:
                setTitle("page editor");
                menuInFlater.inflate(R.menu.page_editor_main, mMenu);
                break;
            case MENU_MODE_SINGLE_SELECT:
                setTitle("single select");
                menuInFlater.inflate(R.menu.page_editor_select_single_picture, mMenu);
                break;
            case MENU_MODE_MULTIPLE_SELECT:
                setTitle("multiple select");
                menuInFlater.inflate(R.menu.page_editor_select_multiple_picture, mMenu);
                break;
        }
    }
}
