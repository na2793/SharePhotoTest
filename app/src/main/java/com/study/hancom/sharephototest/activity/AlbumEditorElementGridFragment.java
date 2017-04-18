package com.study.hancom.sharephototest.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.base.IObservable;
import com.study.hancom.sharephototest.activity.base.IObserver;
import com.study.hancom.sharephototest.adapter.ElementGridAdapter;
import com.study.hancom.sharephototest.adapter.base.SectionedRecyclerGridAdapter;
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.AlbumManager;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.EpubMaker;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlbumEditorElementGridFragment extends Fragment implements IObservable, IObserver {
    private static final int REQUEST_CODE = 1;

    private static final int MENU_MODE_MAIN = 1;
    private static final int MENU_MODE_SINGLE_SELECT = 2;
    private static final int MENU_MODE_MULTIPLE = 3;
    private static final int MENU_MODE_EMPTY_PICTURE = 4;

    static final String STATE_ALBUM = "album";
    static final String STATE_MENU_MODE = "menuMode";
    static final String STATE_ELEMENT_GRID_ADAPTER_SELECTED_SECTION = "elementGridAdapterSelectedSection";
    static final String STATE_ELEMENT_GRID_ADAPTER_ACTION_MODE = "elementGridAdapterSelectMode";
    static final String STATE_ELEMENT_GRID_ADAPTER_SELECTED_CONTENT_RAW_POSITION = "elementGridAdapterSelectedContentRawPosition";
    static final String STATE_ELEMENT_GRID_ADAPTER_MULTIPLEED_CONTENT_RAW_POSITION = "elementGridAdapterMultipleSelectedContentRawPosition";

    private Map<String, IObserver> mObserverMap = new HashMap<>();

    private Activity mParent;

    private Album mAlbum;

    private Menu mMenu;
    private MenuInflater mMenuInflater;
    private int mMenuMode;

    private AutoFitRecyclerGridView mElementGridView;
    private GridLayoutManager mLayoutManager;
    private ElementGridAdapter mElementGridAdapter;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Bundle bundle = getArguments();
        mAlbum = bundle.getParcelable("album");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParent = getActivity();

        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.album_editor_element_grid, container, false);
        mElementGridView = (AutoFitRecyclerGridView) view.findViewById(R.id.element_grid_view);
        mLayoutManager = (GridLayoutManager) mElementGridView.getLayoutManager();

        if (savedInstanceState != null) {
            mAlbum = savedInstanceState.getParcelable(STATE_ALBUM);
            mMenuMode = savedInstanceState.getInt(STATE_MENU_MODE);
            mElementGridAdapter = new ElementGridAdapter(mParent, mAlbum, mLayoutManager);
            mElementGridAdapter.setSelectedSection(savedInstanceState.getInt(STATE_ELEMENT_GRID_ADAPTER_SELECTED_SECTION));
            mElementGridAdapter.setSelectedItemPosition(savedInstanceState.getInt(STATE_ELEMENT_GRID_ADAPTER_SELECTED_CONTENT_RAW_POSITION));
            mElementGridAdapter.setSelectMode(savedInstanceState.getInt(STATE_ELEMENT_GRID_ADAPTER_ACTION_MODE));
            ArrayList<Integer> selectedPositionList = savedInstanceState.getIntegerArrayList(STATE_ELEMENT_GRID_ADAPTER_MULTIPLEED_CONTENT_RAW_POSITION);
            for (int eachPosition : selectedPositionList) {
                mElementGridAdapter.addSelectedItemPosition(eachPosition);
            }
        } else {
            mElementGridAdapter = new ElementGridAdapter(mParent, mAlbum, mLayoutManager);
            mMenuMode = MENU_MODE_MAIN;
        }

        /* 어댑터에 리스너 달기 */
        mElementGridAdapter.setOnOnItemClickListener(new SectionedRecyclerGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int section = mElementGridAdapter.getSectionFor(position);
                mElementGridAdapter.setSelectedSection(section);
                if (mElementGridAdapter.getSelectMode() == ElementGridAdapter.SELECT_MODE_MULTIPLE) {
                    if (!mElementGridAdapter.removeSelectedItemPosition(position)) {
                        mElementGridAdapter.addSelectedItemPosition(position);
                    }
                    changeActionBar(MENU_MODE_MULTIPLE);
                    mElementGridAdapter.notifyDataSetChanged();
                } else {
                    if (mElementGridAdapter.getSelectedItemRawPosition() != position) {
                        mElementGridAdapter.setSelectedItemPosition(position);
                        if (mElementGridAdapter.getContent(section, mElementGridAdapter.rawPositionToPosition(position)) != null) {
                            changeActionBar(MENU_MODE_SINGLE_SELECT);
                        } else {
                            changeActionBar(MENU_MODE_EMPTY_PICTURE);
                        }
                    } else {
                        mElementGridAdapter.setSelectedItemPosition(-1);
                        changeActionBar(MENU_MODE_MAIN);
                    }
                    mElementGridAdapter.notifyDataSetChanged();
                }
            }
        });
        mElementGridAdapter.setOnOnItemLongClickListener(new SectionedRecyclerGridAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                if (mElementGridAdapter.getSelectMode() == ElementGridAdapter.SELECT_MODE_MULTIPLE) {
                    return false;
                }

                if (mElementGridAdapter.isHeader(position)) {
                    return false;
                }

                mElementGridAdapter.setSelectedSection(mElementGridAdapter.getSectionFor(position));
                mElementGridAdapter.setSelectedItemPosition(-1);
                mElementGridAdapter.setSelectMode(ElementGridAdapter.SELECT_MODE_MULTIPLE);
                changeActionBar(MENU_MODE_MULTIPLE);
                mElementGridAdapter.notifyDataSetChanged();

                return true;

            }
        });

        mElementGridAdapter.setOnHeaderClickListener(new ElementGridAdapter.OnHeaderClickListener() {
            @Override
            public void onClick(int section, int rawPosition, View v) {
                int viewId = v.getId();
                switch (viewId) {
                    case R.id.header_menu_button_preview: {
                        Intent intent = new Intent(mParent, AlbumFullSizeWebViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("album", mAlbum);
                        bundle.putInt("pageIndex", mElementGridAdapter.getSelectedSection());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    }
                    case R.id.header_menu_button_change_layout: {
                        int selectedSection = mElementGridAdapter.getSelectedSection();
                        Intent intent = new Intent(mParent, AlbumEditorNewLayoutSelectionActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("currentSection", selectedSection);
                        bundle.putParcelable("currentPageLayout", mAlbum.getPage(selectedSection).getLayout());
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 1);
                        break;
                    }
                    case R.id.header_menu_button_delete: {
                        int selectedSection = mElementGridAdapter.getSelectedSection();
                        try {
                            AlbumManager.removePage(mAlbum, selectedSection);
                            mElementGridAdapter.setSelectedItemPosition(-1);
                            changeActionBar(MENU_MODE_MAIN);
                        } catch (LayoutNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:
                        mElementGridAdapter.setSelectedSection(section);
                        mElementGridAdapter.setSelectedItemPosition(-1);
                        changeActionBar(MENU_MODE_MAIN);
                        break;
                }
                mElementGridAdapter.notifyDataSetChanged();
            }
        });

        /* 뷰에 어댑터 붙이기 */
        mElementGridView.setAdapter(mElementGridAdapter);

        /* 터치 헬퍼 붙이기 */
        // Extend the Callback class
        ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.Callback() {
            //TODO: 스크롤 시에는 onMove 수행하지 않게 할 것
            //defines the enabled move directions in each state (idle, swiping, dragging).
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder source) {
                // Set movement flags based on the layout manager
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    int sourceRawPosition = source.getAdapterPosition();
                    if (!mElementGridAdapter.isHeader(sourceRawPosition)) {
                        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                        final int swipeFlags = 0;
                        return makeMovementFlags(dragFlags, swipeFlags);
                    }
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }

                return makeMovementFlags(0, 0);
            }

            //and in your implementaion of
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
                boolean isAdded = false;

                // get the viewHolder's and target's positions in your adapter data, swap them
                int sourceRawPosition = source.getAdapterPosition();
                if (mElementGridAdapter.isHeader(sourceRawPosition)) {
                    return false;
                }

                int targetRawPosition = target.getAdapterPosition();
                if (mElementGridAdapter.isHeader(targetRawPosition)) {
                    return false;
                }

                int fromSection = mElementGridAdapter.getSectionFor(sourceRawPosition);
                int fromPosition = mElementGridAdapter.rawPositionToPosition(sourceRawPosition);
                int toSection = mElementGridAdapter.getSectionFor(targetRawPosition);
                int toPosition = mElementGridAdapter.rawPositionToPosition(targetRawPosition);

                if (fromSection < toSection) {
                    if (mElementGridAdapter.getCountInSection(fromSection) > 1) {
                        toPosition++;
                    } else {
                        isAdded = true;
                    }
                }

                try {
                    Log.v("tag", (fromSection + 1) + " : " + (fromPosition + 1) + " -> " + (toSection + 1) + " : " + (toPosition + 1));
                    AlbumManager.reorderPicture(mAlbum, fromSection, fromPosition, toSection, toPosition);
                    if (isAdded) {
                        //**레인지를 제한할 필요가 있을까? use itemCount
                        mElementGridAdapter.notifyItemRangeChanged(sourceRawPosition + 1, mElementGridAdapter.getItemCount());
                    }
                    mElementGridAdapter.notifyItemMoved(sourceRawPosition, targetRawPosition);

                    return true;
                } catch (LayoutNotFoundException e) {
                    //TODO: 아이템 삽입 불가 애니메이션
                }

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder source, int direction) {
                // swipe to dismiss 사용하지 않습니다. (do nothing)
                /*int sourceRawPosition = source.getAdapterPosition();
                int section = mElementGridAdapter.getSectionFor(holderRawPosition);
                int position = mElementGridAdapter.rawPositionToPosition(holderRawPosition);
                try {
                    if (mElementGridAdapter.getContent(section, position) != null) {
                        AlbumManager.removePicture(mAlbum, section, position, true);
                    } else {
                        AlbumManager.removePicture(mAlbum, section, position, false);
                    }

                    mElementGridAdapter.notifyItemRemoved(holderRawPosition);
                } catch (LayoutNotFoundException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder source) {
                super.clearView(recyclerView, source);
                if (!recyclerView.isComputingLayout()) {
                    mElementGridAdapter.setSelectMode(ElementGridAdapter.SELECT_MODE_SINGLE);
                    changeActionBar(MENU_MODE_MAIN);
                    mElementGridAdapter.notifyDataSetChanged();
                }
            }
        };

        ItemTouchHelper ith = new ItemTouchHelper(itemTouchHelperCallback);
        ith.attachToRecyclerView(mElementGridView);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        mMenuInflater = inflater;

        int initMenuMode = mMenuMode;
        mMenuMode = -1;
        changeActionBar(initMenuMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_confirm: {
                /* 빈 요소가 존재하는지 확인 작업 */
                int emptyElementCount = 0;
                int pageCount = mAlbum.getPageCount();
                for (int i = 0; i < pageCount; i++) {
                    Page page = mAlbum.getPage(i);
                    int pictureCount = page.getPictureCount();
                    for (int j = 0; j < pictureCount; j++) {
                        if (page.getPicture(j) == null) {
                            emptyElementCount++;
                        }
                    }
                }

                if (emptyElementCount != 0) {
                    createDialog(getString(R.string.dialog_title_action_create_epub_empty), String.format(getResources().getString(R.string.dialog_message_action_create_epub_empty), emptyElementCount))
                            .setPositiveButton(getString(R.string.dialog_button_continue), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showSaveEPUBDialog();
                                }
                            })
                            .setNeutralButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                } else {
                    showSaveEPUBDialog();
                }
                return true;
            }
            case R.id.action_single_edit:
                return true;
            case R.id.action_single_move: {
                final NumberPicker pageNumberPicker = new NumberPicker(mParent);
                pageNumberPicker.setMinValue(1);
                pageNumberPicker.setMaxValue(mElementGridAdapter.getSectionCount());
                pageNumberPicker.setValue(mElementGridAdapter.getSelectedSection() + 1);
                createDialog(getString(R.string.dialog_title_action_single_move), getString(R.string.dialog_message_action_single_move))
                        .setView(pageNumberPicker)
                        .setPositiveButton(getString(R.string.dialog_button_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int fromSection = mElementGridAdapter.getSelectedSection();
                                int fromPosition = mElementGridAdapter.rawPositionToPosition(mElementGridAdapter.getSelectedItemRawPosition());
                                int toSection = pageNumberPicker.getValue() - 1;
                                int toPosition = mAlbum.getPage(toSection).getPictureCount();

                                try {
                                    AlbumManager.reorderPicture(mAlbum, fromSection, fromPosition, toSection, toPosition);
                                    mElementGridAdapter.setSelectedSection(toSection);
                                    mElementGridAdapter.setSelectedItemPosition(-1);
                                    changeActionBar(MENU_MODE_MAIN);
                                    mElementGridAdapter.notifyDataSetChanged();
                                    notifyChangedAll();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(mParent, getString(R.string.toast_action_picture_single_move_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNeutralButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return true;
            }
            case R.id.action_single_delete:
                createDialog(getString(R.string.dialog_title_action_single_delete), getString(R.string.dialog_message_action_single_delete))
                        .setPositiveButton(getString(R.string.dialog_button_remain), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int section = mElementGridAdapter.getSelectedSection();
                                    int position = mElementGridAdapter.rawPositionToPosition(mElementGridAdapter.getSelectedItemRawPosition());
                                    AlbumManager.removePicture(mAlbum, section, position, true);
                                    mElementGridAdapter.setSelectedItemPosition(-1);
                                    changeActionBar(MENU_MODE_MAIN);
                                    mElementGridAdapter.notifyDataSetChanged();
                                    notifyChangedAll();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(mParent, getString(R.string.toast_action_picture_delete_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int section = mElementGridAdapter.getSelectedSection();
                                    int position = mElementGridAdapter.rawPositionToPosition(mElementGridAdapter.getSelectedItemRawPosition());
                                    AlbumManager.removePicture(mAlbum, section, position, false);
                                    mElementGridAdapter.setSelectedItemPosition(-1);
                                    changeActionBar(MENU_MODE_MAIN);
                                    mElementGridAdapter.notifyDataSetChanged();
                                    notifyChangedAll();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(mParent, getString(R.string.toast_action_picture_delete_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNeutralButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return true;
            case R.id.action_multiple_select_all:
                int itemCount = mElementGridAdapter.getItemCount();
                for (int eachPosition = 0; eachPosition < itemCount; eachPosition++) {
                    if (!mElementGridAdapter.isHeader(eachPosition)) {
                        mElementGridAdapter.addSelectedItemPosition(eachPosition);
                    }
                }
                changeActionBar(MENU_MODE_MULTIPLE);
                mElementGridAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_multiple_edit:
                return true;
            case R.id.action_multiple_move: {
                final NumberPicker pageNumberPicker = new NumberPicker(mParent);
                pageNumberPicker.setMinValue(1);
                pageNumberPicker.setMaxValue(mElementGridAdapter.getSectionCount());
                pageNumberPicker.setValue(mElementGridAdapter.getSelectedSection() + 1);
                createDialog(getString(R.string.dialog_title_action_single_move), getString(R.string.dialog_message_action_single_move))
                        .setView(pageNumberPicker)
                        .setPositiveButton(getString(R.string.dialog_button_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int toSection = pageNumberPicker.getValue() - 1;
                                List<Integer> fromRawPositionList = mElementGridAdapter.getSelectedItemRawPositions();
                                Map<Integer, List<Integer>> fromPositionMap = new HashMap<>();
                                for (int eachRawPosition : fromRawPositionList) {
                                    int section = mElementGridAdapter.getSectionFor(eachRawPosition);
                                    int position = mElementGridAdapter.rawPositionToPosition(eachRawPosition);

                                    List<Integer> fromPositionList = fromPositionMap.get(section);
                                    if (fromPositionList == null) {
                                        fromPositionList = new ArrayList<>();
                                        fromPositionMap.put(section, fromPositionList);
                                    }

                                    fromPositionList.add(position);
                                }

                                try {
                                    AlbumManager.reorderMultiplePicture(mAlbum, fromPositionMap, toSection);
                                    mElementGridAdapter.setSelectedSection(toSection);
                                    for (int eachPosition : fromRawPositionList) {
                                        mElementGridAdapter.removeSelectedItemPosition(eachPosition);
                                    }
                                    changeActionBar(MENU_MODE_MULTIPLE);
                                    mElementGridAdapter.notifyDataSetChanged();
                                    notifyChangedAll();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(mParent, getString(R.string.toast_action_picture_single_move_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNeutralButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return true;
            }
            case R.id.action_multiple_delete:
                createDialog(getString(R.string.dialog_title_action_multiple_delete), getString(R.string.dialog_message_action_multiple_delete))
                        .setPositiveButton(getString(R.string.dialog_button_remain), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<Integer> selectedRawPositionList = mElementGridAdapter.getSelectedItemRawPositions();
                                Map<Integer, List<Integer>> selectedPositionMap = new HashMap<>();
                                for (int eachRawPosition : selectedRawPositionList) {
                                    int section = mElementGridAdapter.getSectionFor(eachRawPosition);
                                    int position = mElementGridAdapter.rawPositionToPosition(eachRawPosition);
                                    List<Integer> fromPositionList = selectedPositionMap.get(section);
                                    if (fromPositionList == null) {
                                        fromPositionList = new ArrayList<>();
                                        selectedPositionMap.put(section, fromPositionList);
                                    }
                                    fromPositionList.add(position);
                                }
                                try {
                                    AlbumManager.removeMultiplePicture(mAlbum, selectedPositionMap, true);
                                    for (int eachPosition : selectedRawPositionList) {
                                        mElementGridAdapter.removeSelectedItemPosition(eachPosition);
                                    }
                                    changeActionBar(MENU_MODE_MULTIPLE);
                                    mElementGridAdapter.notifyDataSetChanged();
                                    notifyChangedAll();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(mParent, getString(R.string.toast_action_picture_delete_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<Integer> selectedRawPositionList = mElementGridAdapter.getSelectedItemRawPositions();
                                Map<Integer, List<Integer>> selectedPositionMap = new HashMap<>();
                                for (int eachRawPosition : selectedRawPositionList) {
                                    int section = mElementGridAdapter.getSectionFor(eachRawPosition);
                                    int position = mElementGridAdapter.rawPositionToPosition(eachRawPosition);
                                    List<Integer> fromPositionList = selectedPositionMap.get(section);
                                    if (fromPositionList == null) {
                                        fromPositionList = new ArrayList<>();
                                        selectedPositionMap.put(section, fromPositionList);
                                    }
                                    fromPositionList.add(position);
                                }
                                try {
                                    AlbumManager.removeMultiplePicture(mAlbum, selectedPositionMap, false);
                                    for (int eachPosition : selectedRawPositionList) {
                                        mElementGridAdapter.removeSelectedItemPosition(eachPosition);
                                    }
                                    changeActionBar(MENU_MODE_MULTIPLE);
                                    mElementGridAdapter.notifyDataSetChanged();
                                    notifyChangedAll();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(mParent, getString(R.string.toast_action_picture_delete_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNeutralButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return true;
            case R.id.action_empty_set_picture: {
                ArrayList<String> usedPicturePathList = new ArrayList<>();
                int pageCount = mAlbum.getPageCount();
                for (int i = 0; i < pageCount; i++) {
                    Page eachPage = mAlbum.getPage(i);
                    int pictureCount = eachPage.getPictureCount();
                    for (int j = 0; j < pictureCount; j++) {
                        Picture picture = eachPage.getPicture(j);
                        if (picture != null) {
                            usedPicturePathList.add(picture.getPath());
                        }
                    }
                }
                Intent intent = new Intent(mParent.getApplicationContext(), GallerySingleSelectionActivity.class);
                intent.putStringArrayListExtra("InvalidPicturePathList", usedPicturePathList);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            }
            case R.id.action_empty_delete:
                createDialog(getString(R.string.dialog_title_action_empty_delete), getString(R.string.dialog_message_action_empty_delete))
                        .setPositiveButton(getString(R.string.dialog_button_continue), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int section = mElementGridAdapter.getSelectedSection();
                                    int position = mElementGridAdapter.rawPositionToPosition(mElementGridAdapter.getSelectedItemRawPosition());
                                    AlbumManager.removePicture(mAlbum, section, position, false);
                                    mElementGridAdapter.setSelectedItemPosition(-1);
                                    changeActionBar(MENU_MODE_MAIN);
                                    mElementGridAdapter.notifyDataSetChanged();
                                    notifyChangedAll();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(mParent, getString(R.string.toast_action_picture_delete_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeActionBar(int mode) {
        switch (mode) {
            case MENU_MODE_MAIN:
                if (mMenuMode != mode) {
                    mMenu.clear();
                    mMenuInflater.inflate(R.menu.album_editor_main, mMenu);
                }
                mParent.setTitle(R.string.title_album_editor_main);
                break;
            case MENU_MODE_SINGLE_SELECT:
                if (mMenuMode != mode) {
                    mMenu.clear();
                    mMenuInflater.inflate(R.menu.album_editor_select_single_picture, mMenu);
                }
                mParent.setTitle(R.string.title_album_editor_single_select);
                break;
            case MENU_MODE_MULTIPLE:
                if (mMenuMode != mode) {
                    mMenu.clear();
                    mMenuInflater.inflate(R.menu.album_editor_select_multiple_picture, mMenu);
                }
                mParent.setTitle(String.format(getResources().getString(R.string.title_album_editor_multiple_select), mElementGridAdapter.getSelectedItemCount(), mElementGridAdapter.getContentCount()));
                break;
            case MENU_MODE_EMPTY_PICTURE:
                if (mMenuMode != mode) {
                    mMenu.clear();
                    mMenuInflater.inflate(R.menu.album_editor_select_empty_picture, mMenu);
                }
                mParent.setTitle(R.string.title_album_editor_empty_picture);
                break;
        }

        mMenuMode = mode;
    }

    private AlertDialog.Builder createDialog(String title, String message) {
        return new AlertDialog.Builder(mParent)
                .setTitle(title)
                .setMessage(message);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_ALBUM, mAlbum);
        outState.putInt(STATE_MENU_MODE, mMenuMode);
        outState.putInt(STATE_ELEMENT_GRID_ADAPTER_SELECTED_SECTION, mElementGridAdapter.getSelectedSection());
        outState.putInt(STATE_ELEMENT_GRID_ADAPTER_ACTION_MODE, mElementGridAdapter.getSelectMode());
        outState.putInt(STATE_ELEMENT_GRID_ADAPTER_SELECTED_CONTENT_RAW_POSITION, mElementGridAdapter.getSelectedItemRawPosition());
        outState.putIntegerArrayList(STATE_ELEMENT_GRID_ADAPTER_MULTIPLEED_CONTENT_RAW_POSITION, mElementGridAdapter.getSelectedItemRawPositions());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            int section = mElementGridAdapter.getSelectedSection();
            Picture picture = new Picture(bundle.getString("selectedImage"));
            int position = mElementGridAdapter.rawPositionToPosition(mElementGridAdapter.getSelectedItemRawPosition());
            AlbumManager.setPicture(mAlbum, section, position, picture);
            changeActionBar(MENU_MODE_SINGLE_SELECT);
            mElementGridAdapter.notifyDataSetChanged();
            notifyChangedAll();
        }
    }

    @Override
    public void addObserver(String tag, IObserver observer) {
        mObserverMap.put(tag, observer);
    }

    @Override
    public IObserver removeObserver(String tag) {
        return mObserverMap.remove(tag);
    }

    @Override
    public IObserver getObserver(String tag) {
        return mObserverMap.get(tag);
    }

    @Override
    public int getObserverCount() {
        return mObserverMap.size();
    }

    @Override
    public void notifyChangedAll() {
        notifyChangedAll(null);
    }

    @Override
    public void notifyChangedAll(Bundle out) {
        Set observerTagSet = mObserverMap.keySet();
        for (Object eachTag : observerTagSet) {
            mObserverMap.get(eachTag).update(out);
        }
    }

    @Override
    public void notifyChanged(String tag) {
        notifyChanged(tag, null);
    }

    @Override
    public void notifyChanged(String tag, Bundle out) {
        mObserverMap.get(tag).update(out);
    }

    @Override
    public void update(Bundle in) {
        if (in != null) {
            int selectedSectionIndex = in.getInt("selectedPageNum");
            int sectionPosition = mElementGridAdapter.positionToRawPosition(selectedSectionIndex, -1);
            mLayoutManager.scrollToPositionWithOffset(sectionPosition, 0);
        } else {
            mElementGridAdapter.notifyDataSetChanged();
        }
    }

    public void onBackPressed() {
        switch (mMenuMode) {
            case MENU_MODE_EMPTY_PICTURE:
                // pass ; MENU_MODE_SINGLE_SELECT와 동일
            case MENU_MODE_SINGLE_SELECT:
                mElementGridAdapter.setSelectedItemPosition(-1);
                mElementGridAdapter.notifyDataSetChanged();
                changeActionBar(MENU_MODE_MAIN);
                return;
            case MENU_MODE_MULTIPLE:
                mElementGridAdapter.setSelectMode(ElementGridAdapter.SELECT_MODE_SINGLE);
                mElementGridAdapter.notifyDataSetChanged();
                changeActionBar(MENU_MODE_MAIN);
                return;
        }
        mParent.finish();
    }

    private void showSaveEPUBDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mParent);
        View promptView = layoutInflater.inflate(R.layout.album_editor_save, null);

        final EditText titleEdit = (EditText) promptView.findViewById(R.id.save_epub_editText_title);
        final EditText authorEdit = (EditText) promptView.findViewById(R.id.save_epub_editText_author);
        final EditText publisherEdit = (EditText) promptView.findViewById(R.id.save_epub_editText_publisher);

        createDialog(getString(R.string.dialog_title_action_create_epub), getString(R.string.dialog_message_action_create_epub))
                .setView(promptView)

                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_button_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (titleEdit.getText() != null && authorEdit.getText() != null && publisherEdit.getText() != null) {
                            new createEpubTask().execute(titleEdit.getText().toString(), authorEdit.getText().toString(), publisherEdit.getText().toString());
                        } else {
                            Toast.makeText(mParent, getString(R.string.toast_action_save_epub_empty_data), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.dialog_button_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })

                .create()
                .show();
    }

    private class createEpubTask extends AsyncTask<String, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(mParent);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("저장중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            new EpubMaker(mAlbum, mParent).createFile(arg0[0], arg0[1], arg0[2]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }
}