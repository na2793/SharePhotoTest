package com.study.hancom.sharephototest.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.study.hancom.sharephototest.R;
import com.study.hancom.sharephototest.adapter.ElementListAdapter;
import com.study.hancom.sharephototest.adapter.base.SectionableAdapter;
import com.study.hancom.sharephototest.listener.DataChangedListener;
import com.study.hancom.sharephototest.model.Album;
import com.study.hancom.sharephototest.model.Picture;

import java.util.ArrayList;

public class AlbumEditorElementListFragment extends Fragment implements DataChangedListener.OnDataChangeListener {

    private static final int MENU_MODE_MAIN = 1;
    private static final int MENU_MODE_SINGLE_SELECT = 2;
    private static final int MENU_MODE_MULTIPLE_SELECT = 3;
    private static final int MENU_MODE_EMPTY_PICTURE = 4;

    private static final int REQUEST_CODE = 1;

    private Menu mMenu;
    private int mMenuMode = MENU_MODE_MAIN;
    private MenuInflater mMenuInflater;

    private ListView mElementListView;
    private ElementListAdapter mElementListAdapter;

    private Album mAlbum;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Bundle extra = getArguments();
        mAlbum = extra.getParcelable("album");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* 뷰 생성 */
        View view = inflater.inflate(R.layout.album_editor_element_list, container, false);

        /* 리스트뷰에 어댑터 붙이기 */
        mElementListView = (ListView) view.findViewById(R.id.element_list_view);
        mElementListAdapter = new ElementListAdapter(getActivity(), mAlbum,
                R.layout.album_editor_element_list_row, R.id.row_menuHolder, R.id.row_header_text,
                R.id.row_itemHolder, SectionableAdapter.MODE_VARY_WIDTHS);
        mElementListAdapter.setOnMultipleItemSelectModeListener(new ElementListAdapter.OnMultipleItemSelectModeListener() {
            @Override
            public void onStart() {
                onChangeActionBar(MENU_MODE_MULTIPLE_SELECT);
            }

            @Override
            public void onSelect() {
                getActivity().setTitle(String.format(getResources().getString(R.string.title_album_editor_multiple_select), mElementListAdapter.getSelectedItemCount(), mElementListAdapter.getDataCount()));
            }

            @Override
            public void onStop() {
                onChangeActionBar(MENU_MODE_MAIN);
            }
        });
        mElementListAdapter.setOnItemSelectListener(new ElementListAdapter.OnItemSelectListener() {
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
        mElementListView.setAdapter(mElementListAdapter);
        mElementListView.setDividerHeight(0);

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
                        mElementListAdapter.setSelectedItem(-1);
                        DataChangedListener.notifyChanged();
                        break;
                    case MENU_MODE_MULTIPLE_SELECT:
                        mElementListAdapter.stopMultipleSelectMode();
                        DataChangedListener.notifyChanged();
                        break;
                }
                return true;
            case R.id.action_confirm:
                // @임시
                Toast.makeText(getActivity(), "epub으로 저장하였습니다.", Toast.LENGTH_LONG).show();
                getActivity().finish();
                return true;
            case R.id.action_single_edit:
                return true;
            case R.id.action_single_move:
                final NumberPicker pageNumberPicker = new NumberPicker(getActivity());
                pageNumberPicker.setMinValue(1);
                pageNumberPicker.setMaxValue(mElementListAdapter.getSectionsCount() + 1);
                pageNumberPicker.setValue(mElementListAdapter.getSelectedSection() + 1);
                createDialog(getString(R.string.dialog_title_action_single_move), getString(R.string.dialog_message_action_single_move))
                        .setView(pageNumberPicker)
                        .setPositiveButton(getString(R.string.dialog_button_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int fromIndex = mElementListAdapter.getSelectedSection();
                                int fromPosition = mElementListAdapter.getPositionInSection(mElementListAdapter.getSelectedItem());
                                int toIndex = pageNumberPicker.getValue() - 1;
                                int toPosition = mElementListAdapter.getCountInSection(toIndex);
                                try {
                                    mElementListAdapter.reorderPicture(fromIndex, fromPosition, toIndex, toPosition);
                                    mElementListAdapter.setSelectedSection(toIndex);
                                    mElementListAdapter.setSelectedItem(-1);
                                    DataChangedListener.notifyChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_single_move_fail), Toast.LENGTH_LONG).show();
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
                                    int index = mElementListAdapter.getSelectedSection();
                                    int position = mElementListAdapter.getPositionInSection(mElementListAdapter.getSelectedItem());
                                    mElementListAdapter.removePicture(index, position, true);
                                    DataChangedListener.notifyChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_delete_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int index = mElementListAdapter.getSelectedSection();
                                    int position = mElementListAdapter.getPositionInSection(mElementListAdapter.getSelectedItem());
                                    mElementListAdapter.removePicture(index, position, false);
                                    DataChangedListener.notifyChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_delete_fail), Toast.LENGTH_LONG).show();
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
                int dataCount = mElementListAdapter.getDataCount();
                for (int eachPosition = 0; eachPosition < dataCount; eachPosition++) {
                    mElementListAdapter.addMultipleSelectedItem(eachPosition);
                }
                DataChangedListener.notifyChanged();
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
                                    for (int eachSelectedItemPosition : mElementListAdapter.getMultipleSelectedItem()) {
                                        int index = mElementListAdapter.getTypeFor(eachSelectedItemPosition);
                                        int position = mElementListAdapter.getPositionInSection(eachSelectedItemPosition);
                                        mElementListAdapter.removePicture(index, position, true);
                                    }
                                    mElementListAdapter.stopMultipleSelectMode();
                                    DataChangedListener.notifyChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_delete_fail), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Integer[] sortedMultipleSelectedItemArray = mElementListAdapter.getMultipleSelectedItem(true);
                                    for (int i = sortedMultipleSelectedItemArray.length - 1; i > -1; i--) {
                                        int eachSelectedItemPosition = sortedMultipleSelectedItemArray[i];
                                        int index = mElementListAdapter.getTypeFor(eachSelectedItemPosition);
                                        int position = mElementListAdapter.getPositionInSection(eachSelectedItemPosition);
                                        mElementListAdapter.removePicture(index, position, false);
                                    }
                                    mElementListAdapter.stopMultipleSelectMode();
                                    DataChangedListener.notifyChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_delete_fail), Toast.LENGTH_LONG).show();
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
                ArrayList<String> albumElementPictureListPaths = new ArrayList<>();
                for (int i = 0; i < mElementListAdapter.getDataCount(); i++) {
                    Picture picture = mElementListAdapter.getItem(i);
                    if(picture != null) {
                        albumElementPictureListPaths.add(picture.getPath());
                    }
                }

                Intent intent = new Intent(getActivity().getApplicationContext(), GallerySingleSelectionActivity.class);
                intent.putStringArrayListExtra("albumElementPaths", albumElementPictureListPaths);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            case R.id.action_empty_delete:
                createDialog(getString(R.string.dialog_title_action_empty_delete), getString(R.string.dialog_message_action_empty_delete))
                        .setPositiveButton(getString(R.string.dialog_button_continue), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int index = mElementListAdapter.getSelectedSection();
                                    int position = mElementListAdapter.getPositionInSection(mElementListAdapter.getSelectedItem());
                                    mElementListAdapter.removePicture(index, position, false);
                                    DataChangedListener.notifyChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), getString(R.string.toast_action_delete_fail), Toast.LENGTH_LONG).show();
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

    public void onChangeActionBar(int mode) {
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
                getActivity().setTitle(String.format(getResources().getString(R.string.title_album_editor_multiple_select), mElementListAdapter.getSelectedItemCount(), mElementListAdapter.getDataCount()));
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
        mElementListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == Activity.RESULT_OK) {
            try {
                Bundle bundle = data.getExtras();
                Picture picture = new Picture(bundle.getString("selectedImage"));
                int index = mElementListAdapter.getSelectedSection();
                int position = mElementListAdapter.getPositionInSection(mElementListAdapter.getSelectedItem());
                mElementListAdapter.addPicture(index, position, picture);
                mElementListAdapter.removePicture(index, position + 1, false);
                DataChangedListener.notifyChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


