package com.study.hancom.sharephototest.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.activity.base.DataChangeObserverActivity;
import com.study.hancom.sharephototest.adapter.ElementGridAdapter;
import com.study.hancom.sharephototest.exception.LayoutNotFoundException;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.AlbumAction;
import com.study.hancom.sharephototest.model.Page;
import com.study.hancom.sharephototest.model.Picture;
import com.study.hancom.sharephototest.util.EpubMaker;
import com.study.hancom.sharephototest.view.AutoFitRecyclerGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlbumEditorElementListFragment extends Fragment implements DataChangeObserverActivity.OnDataChangeListener {

    private static final int REQUEST_CODE = 1;

    private static final int MENU_MODE_MAIN = 1;
    private static final int MENU_MODE_SINGLE_SELECT = 2;
    private static final int MENU_MODE_MULTIPLE_SELECT = 3;
    private static final int MENU_MODE_EMPTY_PICTURE = 4;

    private Album mAlbum;
    private AlbumAction mAlbumAction = new AlbumAction();

    private Menu mMenu;
    private MenuInflater mMenuInflater;
    private int mMenuMode = MENU_MODE_MAIN;

    private AutoFitRecyclerGridView mElementGridView;
    private ElementGridAdapter mElementGridAdapter;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Bundle bundle = getArguments();
        mAlbum = bundle.getParcelable("album");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.album_editor_element_grid, container, false);

        /* 리스트뷰에 어댑터 붙이기 */
        mElementGridView = (AutoFitRecyclerGridView) view.findViewById(R.id.element_grid_view);
        mElementGridAdapter = new ElementGridAdapter(getActivity(), mAlbum, (GridLayoutManager) mElementGridView.getLayoutManager());
        mElementGridAdapter.setOnContentSelectListener(new ElementGridAdapter.OnContentSelectListener() {
            @Override
            public void onSelect(int section, int position) {
                if (mElementGridAdapter.isMultipleSelectModeEnabled()) {
                    changeActionBar(MENU_MODE_MULTIPLE_SELECT);
                    getActivity().setTitle(String.format(getResources().getString(R.string.title_album_editor_multiple_select),
                            mElementGridAdapter.getSelectedContentCount(), mElementGridAdapter.getContentCount()));
                } else {
                    if (mElementGridAdapter.getContent(section, position) != null) {
                        changeActionBar(MENU_MODE_SINGLE_SELECT);
                    } else {
                        changeActionBar(MENU_MODE_EMPTY_PICTURE);
                    }
                }
            }

            @Override
            public void onCancel() {
                changeActionBar(MENU_MODE_MAIN);
            }
        });

        mElementGridAdapter.setOnDataChangeListener(new ElementGridAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged() {
                ((DataChangeObserverActivity) getActivity()).notifyChanged();
            }
        });
        mElementGridView.setAdapter(mElementGridAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        mMenuInflater = inflater;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                switch (mMenuMode) {
                    case MENU_MODE_MAIN:
                        getActivity().finish();
                        break;
                    case MENU_MODE_EMPTY_PICTURE:
                        // pass ; MENU_MODE_SINGLE_SELECT와 동일
                    case MENU_MODE_SINGLE_SELECT:
                        mElementGridAdapter.selectContent(-1);
                        mElementGridAdapter.notifyDataSetChanged();
                        break;
                    case MENU_MODE_MULTIPLE_SELECT:
                        mElementGridAdapter.stopMultipleSelectMode();
                        mElementGridAdapter.notifyDataSetChanged();
                        break;
                }
                return true;
            case R.id.action_confirm:
                // @임시
                new Thread(new Runnable() {
                    public void run() {
                        new EpubMaker(mAlbum, getActivity()).saveEpub("test");
                    }
                }).start();
                return true;
            case R.id.action_single_edit:
                return true;
            case R.id.action_single_move:
                final NumberPicker pageNumberPicker = new NumberPicker(getActivity());
                pageNumberPicker.setMinValue(1);
                pageNumberPicker.setMaxValue(mElementGridAdapter.getSectionCount());
                pageNumberPicker.setValue(mElementGridAdapter.getSelectedSection() + 1);
                createDialog(getString(R.string.dialog_title_action_single_move), getString(R.string.dialog_message_action_single_move))
                        .setView(pageNumberPicker)
                        .setPositiveButton(getString(R.string.dialog_button_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int fromSection = mElementGridAdapter.getSelectedSection();
                                int fromPosition = mElementGridAdapter.rawPositionToPosition(mElementGridAdapter.getSelectedContentRawPosition());
                                int toSection = pageNumberPicker.getValue() - 1;
                                int toPosition = mAlbum.getPage(toSection).getPictureCount();

                                try {
                                    mAlbumAction.reorderPicture(mAlbum, fromSection, fromPosition, toSection, toPosition);
                                    mElementGridAdapter.selectSection(toSection);
                                    mElementGridAdapter.selectContent(-1);
                                    mElementGridAdapter.notifyDataSetChanged();

                                    ((DataChangeObserverActivity) getActivity()).notifyChanged();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_picture_single_move_fail), Toast.LENGTH_LONG).show();
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
            case R.id.action_single_delete:
                createDialog(getString(R.string.dialog_title_action_single_delete), getString(R.string.dialog_message_action_single_delete))
                        .setPositiveButton(getString(R.string.dialog_button_remain), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int section = mElementGridAdapter.getSelectedSection();
                                    int position = mElementGridAdapter.rawPositionToPosition(mElementGridAdapter.getSelectedContentRawPosition());
                                    mAlbumAction.removePicture(mAlbum, section, position, true);
                                    mElementGridAdapter.selectContent(-1);
                                    ((DataChangeObserverActivity) getActivity()).notifyChanged();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_picture_delete_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int section = mElementGridAdapter.getSelectedSection();
                                    int position = mElementGridAdapter.rawPositionToPosition(mElementGridAdapter.getSelectedContentRawPosition());
                                    mAlbumAction.removePicture(mAlbum, section, position, false);
                                    mElementGridAdapter.selectContent(-1);
                                    ((DataChangeObserverActivity) getActivity()).notifyChanged();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_picture_delete_fail), Toast.LENGTH_LONG).show();
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
                int dataCount = mElementGridAdapter.getContentCount();
                for (int eachContentRawPosition = 0; eachContentRawPosition < dataCount; eachContentRawPosition++) {
                    if (!mElementGridAdapter.selectContent(eachContentRawPosition)) {
                        mElementGridAdapter.selectContent(eachContentRawPosition);
                    }
                }

                mElementGridAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_multiple_edit:
                return true;
            case R.id.action_multiple_move:
                return true;
            case R.id.action_multiple_delete:
                createDialog(getString(R.string.dialog_title_action_multiple_delete), getString(R.string.dialog_message_action_multiple_delete))
                        .setPositiveButton(getString(R.string.dialog_button_remain), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    List<Integer> selectedContentRawPositionList = mElementGridAdapter.getMultipleSelectedContentRawPosition();
                                    for (int eachSelectedItemPosition : selectedContentRawPositionList) {
                                        int section = mElementGridAdapter.getSectionFor(eachSelectedItemPosition);
                                        int position = mElementGridAdapter.rawPositionToPosition(eachSelectedItemPosition);
                                        mAlbumAction.removePicture(mAlbum, section, position, true);
                                    }
                                    mElementGridAdapter.stopMultipleSelectMode();
                                    ((DataChangeObserverActivity) getActivity()).notifyChanged();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_picture_delete_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    /* 삭제 가능 여부(레이아웃 존재 여부) 판별 */
                                    List<Integer> selectedContentRawPositionList = mElementGridAdapter.getMultipleSelectedContentRawPosition();
                                    Map<Integer, List<Integer>> selectedContentBySectionMap = new HashMap<>();

                                    for (int eachSelectedContentRawPosition : selectedContentRawPositionList) {
                                        int eachSection = mElementGridAdapter.getSectionFor(eachSelectedContentRawPosition);
                                        List<Integer> eachRawPositionList;
                                        if (selectedContentBySectionMap.containsKey(eachSection)) {
                                            eachRawPositionList = selectedContentBySectionMap.get(eachSection);
                                        } else {
                                            eachRawPositionList = new ArrayList<>();
                                            selectedContentBySectionMap.put(eachSection, eachRawPositionList);
                                        }
                                        eachRawPositionList.add(eachSelectedContentRawPosition);
                                    }

                                    Set<Integer> selectedContentCountBySectionMapKeySet = selectedContentBySectionMap.keySet();
                                    Set<Integer> allLayoutTypeSet = Page.getAllLayoutType();

                                    for (int eachSection : selectedContentCountBySectionMapKeySet) {
                                        int pictureCount = mAlbum.getPage(eachSection).getPictureCount();
                                        int selectedCount = selectedContentBySectionMap.get(eachSection).size();
                                        int result = pictureCount - selectedCount;
                                        if (result > 0) {
                                            if (!allLayoutTypeSet.contains(result)) {
                                                throw new LayoutNotFoundException();
                                            }
                                        }
                                    }

                                    /* 삭제 */
                                    List<Integer> sortedSelectedSectionList = new ArrayList<>(selectedContentCountBySectionMapKeySet);
                                    Collections.sort(sortedSelectedSectionList);
                                    for (int i = sortedSelectedSectionList.size() - 1 ; i >= 0 ; i--) {
                                        int eachSection = sortedSelectedSectionList.get(i);
                                        mAlbumAction.removeMultiplePicture(mAlbum, eachSection, selectedContentBySectionMap.get(eachSection));
                                    }
                                    mElementGridAdapter.stopMultipleSelectMode();
                                    ((DataChangeObserverActivity) getActivity()).notifyChanged();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_picture_delete_fail), Toast.LENGTH_LONG).show();
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
            case R.id.action_empty_set_picture:
                ArrayList<String> usedPicturePathList = new ArrayList<>();
                int contentCount = mElementGridAdapter.getContentCount();
                for (int i = 0; i < contentCount; i++) {
                    Picture picture = mElementGridAdapter.getContent(i);
                    if (picture != null) {
                        usedPicturePathList.add(picture.getPath());
                    }
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), GallerySingleSelectionActivity.class);
                intent.putStringArrayListExtra("InvalidPicturePathList", usedPicturePathList);
                startActivityForResult(intent, REQUEST_CODE);

                return true;
            case R.id.action_empty_delete:
                createDialog(getString(R.string.dialog_title_action_empty_delete), getString(R.string.dialog_message_action_empty_delete))
                        .setPositiveButton(getString(R.string.dialog_button_continue), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int section = mElementGridAdapter.getSelectedSection();
                                    int position = mElementGridAdapter.rawPositionToPosition(mElementGridAdapter.getSelectedContentRawPosition());
                                    mAlbumAction.removePicture(mAlbum, section, position, false);
                                    mElementGridAdapter.selectContent(-1);
                                    ((DataChangeObserverActivity) getActivity()).notifyChanged();
                                } catch (LayoutNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_picture_delete_fail), Toast.LENGTH_LONG).show();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeActionBar(int mode) {
        mMenuMode = mode;
        mMenu.clear();
        switch (mMenuMode) {
            case MENU_MODE_MAIN:
                getActivity().setTitle(R.string.title_album_editor_main);
                mMenuInflater.inflate(R.menu.album_editor_main, mMenu);
                break;
            case MENU_MODE_SINGLE_SELECT:
                getActivity().setTitle(R.string.title_album_editor_single_select);
                mMenuInflater.inflate(R.menu.album_editor_select_single_picture, mMenu);
                break;
            case MENU_MODE_MULTIPLE_SELECT:
                getActivity().setTitle(String.format(getResources().getString(R.string.title_album_editor_multiple_select), mElementGridAdapter.getSelectedContentCount(), mElementGridAdapter.getContentCount()));
                mMenuInflater.inflate(R.menu.album_editor_select_multiple_picture, mMenu);
                break;
            case MENU_MODE_EMPTY_PICTURE:
                getActivity().setTitle(R.string.title_album_editor_empty_picture);
                mMenuInflater.inflate(R.menu.album_editor_select_empty_picture, mMenu);
                break;
        }
    }

    private AlertDialog.Builder createDialog(String title, String message) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message);
    }

    @Override
    public void onDataChanged() {
        mElementGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Picture picture = new Picture(bundle.getString("selectedImage"));
            int section = mElementGridAdapter.getSelectedSection();
            int position = mElementGridAdapter.rawPositionToPosition(mElementGridAdapter.getSelectedContentRawPosition());
            mAlbumAction.setPicture(mAlbum, section, position, picture);

            ((DataChangeObserverActivity) getActivity()).notifyChanged();
        }
    }
}