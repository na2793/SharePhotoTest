package com.study.hancom.sharephototest.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
    private static final int MENU_MODE_EMPTY_PICTURE = 4;

    private Menu mMenu;
    private int mMenuMode = MENU_MODE_MAIN;
    private MenuInflater mMenuInflater;

    private ListView mPageListView;
    private PageEditorAdapter mPageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_editor_main);

        /* 메뉴 인플레이터 얻기 */
        mMenuInflater = getMenuInflater();

        /* 뒤로 가기 버튼 생성 */
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

        for (int i = 0; i < pageNum; i++) {
            Page page = album.getPage(i);
            int elementNum = page.getLayout().getElementNum();

            for (int j = 0; j < elementNum; j++) {
                page.addPicture(pictureList.get(usedPictureCount));
                usedPictureCount++;
            }
        }

        /* 리스트뷰에 어댑터 붙이기 */
        mPageListView = (ListView) findViewById(R.id.page_list_view);
        mPageListAdapter = new PageEditorAdapter(this, album,
                R.layout.page_editor_list_row, R.id.row_header,
                R.id.row_itemHolder, SectionableAdapter.MODE_VARY_WIDTHS);
        mPageListAdapter.setOnMultipleItemSelectModeListener(new PageEditorAdapter.OnMultipleItemSelectModeListener() {
            @Override
            public void onStart() {
                onChangeActionBar(MENU_MODE_MULTIPLE_SELECT);
            }

            @Override
            public void onSelect() {
                setTitle(String.format(getResources().getString(R.string.title_page_editor_multiple_select), mPageListAdapter.getSelectedItemCount(), mPageListAdapter.getDataCount()));
            }

            @Override
            public void onStop() {
                onChangeActionBar(MENU_MODE_MAIN);
            }
        });
        mPageListAdapter.setOnItemSelectListener(new PageEditorAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelect(Object item) {
                if (item != null) {
                    onChangeActionBar(MENU_MODE_SINGLE_SELECT);
                } else {
                    onChangeActionBar(MENU_MODE_EMPTY_PICTURE);
                }
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
                switch (mMenuMode) {
                    case MENU_MODE_MAIN:
                        this.finish();
                        break;
                    case MENU_MODE_EMPTY_PICTURE:
                        // pass ; MENU_MODE_SINGLE_SELECT와 동일
                    case MENU_MODE_SINGLE_SELECT:
                        mPageListAdapter.setSelectedItem(-1);
                        mPageListAdapter.notifyDataSetChanged();
                        break;
                    case MENU_MODE_MULTIPLE_SELECT:
                        mPageListAdapter.stopMultipleSelectMode();
                        mPageListAdapter.notifyDataSetChanged();
                        break;
                }
                return true;
            case R.id.action_confirm:
                this.finish();
                return true;
            case R.id.action_single_edit:
                return true;
            case R.id.action_single_move:
                return true;
            case R.id.action_single_delete:
                createDialog(getString(R.string.dialog_title_action_single_delete), getString(R.string.dialog_message_action_single_delete))
                        .setPositiveButton(getString(R.string.dialog_button_remain), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = mPageListAdapter.getSelectedSection();
                                int position = mPageListAdapter.getPositionInSection(mPageListAdapter.getSelectedItem());
                                mPageListAdapter.setPictureEmpty(index, position);
                                mPageListAdapter.setSelectedItem(-1);
                                mPageListAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = mPageListAdapter.getSelectedSection();
                                int position = mPageListAdapter.getPositionInSection(mPageListAdapter.getSelectedItem());
                                try {
                                    mPageListAdapter.removePicture(index, position);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mPageListAdapter.setSelectedItem(-1);
                                mPageListAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNeutralButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     // 닫기
                            }
                        })
                        .create().show();
                return true;
            case R.id.action_multiple_select_all:
                int dataCount = mPageListAdapter.getDataCount();
                for (int i = 0; i < dataCount; i++) {
                    mPageListAdapter.setSelectedItem(i);
                }
                mPageListAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_multiple_edit:
                return true;
            case R.id.action_multiple_move:
                return true;
            case R.id.action_multiple_delete:
                return true;
            case R.id.action_empty_set_picture:
                return true;
            case R.id.action_empty_delete:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onChangeActionBar(int mode) {
        mMenuMode = mode;
        mMenu.clear();
        switch (mMenuMode) {
            case MENU_MODE_MAIN:
                setTitle(R.string.title_page_editor_main);
                mMenuInflater.inflate(R.menu.page_editor_main, mMenu);
                break;
            case MENU_MODE_SINGLE_SELECT:
                setTitle(R.string.title_page_editor_single_select);
                mMenuInflater.inflate(R.menu.page_editor_select_single_picture, mMenu);
                break;
            case MENU_MODE_MULTIPLE_SELECT:
                setTitle(String.format(getResources().getString(R.string.title_page_editor_multiple_select), mPageListAdapter.getSelectedItemCount(), mPageListAdapter.getDataCount()));
                mMenuInflater.inflate(R.menu.page_editor_select_multiple_picture, mMenu);
                break;
            case MENU_MODE_EMPTY_PICTURE:
                setTitle(R.string.title_page_editor_empty_picture);
                mMenuInflater.inflate(R.menu.page_editor_select_empty_picture, mMenu);
                break;
        }
    }

    private AlertDialog.Builder createDialog(String title, String message) {
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
    }
}
